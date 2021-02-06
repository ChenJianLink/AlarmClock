package cn.chenjianlink.android.alarmclock.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.chenjianlink.android.alarmclock.db.AlarmClockDao;
import cn.chenjianlink.android.alarmclock.db.BaseAlarmClockDatabase;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * @author chenjian
 * 对数据库操作进行封装，向ViewModel提供封装方法
 */
public class AlarmClockRepository {

    private final AlarmClockDao alarmClockDao;

    private final LiveData<List<AlarmClock>> alarmClockList;

    /**
     * 线程池，在子线程中处理增删改
     */
    private final ExecutorService executor;

    public AlarmClockRepository(Context context) {
        alarmClockDao = BaseAlarmClockDatabase.getDatabase(context).getAlarmClockDao();
        alarmClockList = alarmClockDao.selectAll();
        executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.MINUTES, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * 更新闹钟信息
     *
     * @param alarmClock 要更新的闹钟
     */
    public void update(final AlarmClock alarmClock) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                alarmClockDao.update(alarmClock);
            }
        });
    }

    /**
     * 插入新闹钟
     *
     * @param alarmClock 要插入的闹钟
     */
    public void insert(final AlarmClock alarmClock) {
        LogUtil.i("Repository", "insert data");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                alarmClockDao.insert(alarmClock);
            }
        });
    }

    /**
     * 删除闹钟
     *
     * @param alarmClock 要删除的闹钟
     */
    public void delete(final AlarmClock alarmClock) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                alarmClockDao.delete(alarmClock);
            }
        });
    }

    /**
     * 查询出所有闹钟，若没有闹钟，则查找系统服务中的闹钟
     *
     * @return 返回LiveData包装的AlarmClock列表
     */
    public LiveData<List<AlarmClock>> findAll() {
        return alarmClockList;
    }

    /**
     * 查询单个闹钟
     *
     * @param alarmClockId 要查询的闹钟id
     * @return 闹钟对象
     */
    public AlarmClock findById(int alarmClockId) {
        return alarmClockDao.selectById(alarmClockId);
    }

    /**
     * 返回未封装在LiveData中的闹钟列表
     *
     * @return 闹钟列表
     */
    public List<AlarmClock> findAllWithoutLiveData() {
        return alarmClockDao.selectAllWithoutLiveData();
    }

}
