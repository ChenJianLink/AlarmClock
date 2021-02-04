package cn.chenjianlink.android.alarmclock.model;

/**
 * @author chenjian
 * 信使类
 */
public class Messager {
    /**
     * 下一次响铃还有多少时间
     */
    private String nextRingTime;

    /**
     * 响铃周期（周日到周六）
     */
    private String ringRepeatTime;

    public String getNextRingTime() {
        return nextRingTime;
    }

    public void setNextRingTime(String nextRingTime) {
        this.nextRingTime = nextRingTime;
    }

    public String getRingRepeatTime() {
        return ringRepeatTime;
    }

    public void setRingRepeatTime(String ringRepeatTime) {
        this.ringRepeatTime = ringRepeatTime;
    }

}
