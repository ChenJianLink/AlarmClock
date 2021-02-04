package cn.chenjianlink.android.alarmclock.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cn.chenjianlink.android.alarmclock.model.AlarmClock;

/**
 * @author chenjian
 * 对数据库进行查询操作
 */
@Dao
public interface AlarmClockDao {

    /**
     * 向数据库插入新闹钟
     *
     * @param alarmClock 闹钟信息
     * @return 闹钟id
     */
    @Insert
    long insert(AlarmClock alarmClock);

    /**
     * 批量向数据库插入闹钟
     *
     * @param alarmClockList 闹钟列表
     */
    @Insert
    void insert(List<AlarmClock> alarmClockList);

    /**
     * 查询所有闹钟数据
     *
     * @return LiveData包装的闹钟列表
     */
    @Query("SELECT * FROM clock_table")
    LiveData<List<AlarmClock>> selectAll();

    /**
     * 查询单个闹钟
     *
     * @param alarmClockId 要查询的闹钟的ID
     * @return 闹钟对象
     */
    @Query("SELECT * FROM clock_table WHERE clock_id = :alarmClockId")
    AlarmClock selectById(int alarmClockId);

    /**
     * 更新闹钟
     *
     * @param alarmClock 要更新的闹钟对象
     */
    @Update
    void update(AlarmClock alarmClock);

    /**
     * 获取闹钟全部信息
     *
     * @return 闹钟列表
     */
    @Query("SELECT * FROM clock_table")
    List<AlarmClock> selectAllWithoutLiveData();

    /**
     * 删除闹钟
     *
     * @param alarmClock 要删除的闹钟对象
     */
    @Delete
    void delete(AlarmClock alarmClock);
}
