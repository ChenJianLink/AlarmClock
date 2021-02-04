package cn.chenjianlink.android.alarmclock.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.chenjianlink.android.alarmclock.model.AlarmClock;

/**
 * @author chenjian
 * 闹钟应用数据库
 */
@Database(entities = {AlarmClock.class}, version = 1)
public abstract class BaseAlarmClockDatabase extends RoomDatabase {
    /**
     * 获取AlarmClockDao对象
     *
     * @return AlarmClockDao对象
     */
    public abstract AlarmClockDao getAlarmClockDao();

    private static volatile BaseAlarmClockDatabase INSTANCE;

    public static BaseAlarmClockDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (BaseAlarmClockDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, BaseAlarmClockDatabase.class, "alarm_clock")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
