package cn.chenjianlink.android.alarmclock.service;

/**
 * @author chenjian
 * 响铃服务中间人接口
 */
public interface AlarmClockServiceProvider {
    /**
     * 5分钟贪睡功能
     */
    void pauseFiveMinRing();

    /**
     * 关闭闹钟
     */
    void close();

    /**
     * 响铃后如果没有在一分钟内做出操作，默认直接进入贪睡模式
     */
    void oneMinClose();
}
