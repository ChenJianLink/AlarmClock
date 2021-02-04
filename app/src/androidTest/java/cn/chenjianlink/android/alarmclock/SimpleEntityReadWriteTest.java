package cn.chenjianlink.android.alarmclock;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import cn.chenjianlink.android.alarmclock.db.AlarmClockDao;
import cn.chenjianlink.android.alarmclock.db.BaseAlarmClockDatabase;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * @author chenjian
 * 数据库测试类
 */
@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private AlarmClockDao alarmClockDao;
    private BaseAlarmClockDatabase alarmClockDatabase;

    /**
     * 前处理，获得DAO和DB对象
     */
    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        alarmClockDatabase = BaseAlarmClockDatabase.getDatabase(context);
        alarmClockDao = alarmClockDatabase.getAlarmClockDao();
    }

    @After
    public void closeDatabase() {
        alarmClockDatabase.close();
    }

    /**
     * DAO读写测试
     */
    @Test
    public void readAndWriteTest() {
        AlarmClock alarmClock = new AlarmClock();
        alarmClock.setHour(12);
        alarmClock.setMinute(24);
        alarmClockDao.insert(alarmClock);
        LiveData<List<AlarmClock>> alarmClockList = alarmClockDao.selectAll();
        LogUtil.v("List",""+alarmClockList.getValue());
        List<AlarmClock> alarmClocks = alarmClockList.getValue();
//        assert alarmClocks != null;
//        AlarmClock clock = alarmClocks.get(0);
//        LogUtil.i("hour", "" + clock.getHour());
//        LogUtil.i("minute", "" + clock.getMinute());
    }
}
