package cn.chenjianlink.android.alarmclock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author chenjian
 * 闹钟bean
 */
@Entity(tableName = "clock_table")
public class AlarmClock {

    /**
     * 闹钟id
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "clock_id")
    private int clockId;

    /**
     * 闹钟的小时部分（24小时）
     */
    private int hour;

    /**
     * 闹钟的分钟部分
     */
    private int minute;

    /**
     * 重复周期
     */
    private int ringCycle;

    /**
     * 判断闹钟对象是否启用
     */
    private boolean isStarted;

    /**
     * 备注
     */
    private String remark;

    /**
     * 闹钟铃声uri
     */
    private String ringMusicUri;

    /**
     * 是否震动
     */
    private boolean isVibrated;

    /**
     * 以下为各个属性对应的getter和setter方法
     * *******************************************************************************
     */
    public int getClockId() {
        return clockId;
    }

    public void setClockId(int clockId) {
        this.clockId = clockId;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public int getRingCycle() {
        return ringCycle;
    }

    public void setRingCycle(int ringCycle) {
        this.ringCycle = ringCycle;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRingMusicUri() {
        return ringMusicUri;
    }

    public void setRingMusicUri(String ringMusicUri) {
        this.ringMusicUri = ringMusicUri;
    }

    public boolean isVibrated() {
        return isVibrated;
    }

    public void setVibrated(boolean vibrated) {
        isVibrated = vibrated;
    }

    @NonNull
    @Override
    public String toString() {
        return "AlarmClock{" +
                "clockId=" + clockId +
                ", hour=" + hour +
                ", minute=" + minute +
                ", ringCycle=" + ringCycle +
                ", isStarted=" + isStarted +
                ", remark='" + remark + '\'' +
                ", ringMusicUri='" + ringMusicUri + '\'' +
                ", isVibrated=" + isVibrated +
                '}';
    }
}
