package cn.chenjianlink.android.alarmclock.utils;

/**
 * @author chenjian
 * 通用各种属性值的封装
 */
public class CommonValue {

    /**
     * 星期日的二进制代码
     */
    public static final int SUNDAY = 0b1000000;

    /**
     * 星期一的二进制代码
     */
    public static final int MONDAY = 0b0100000;

    /**
     * 星期二的二进制代码
     */
    public static final int TUESDAY = 0b0010000;

    /**
     * 星期三的二进制代码
     */
    public static final int WEDNESDAY = 0b0001000;

    /**
     * 星期四的二进制代码
     */
    public static final int THURSDAY = 0b0000100;

    /**
     * 星期五的二进制代码
     */
    public static final int FRIDAY = 0b0000010;

    /**
     * 星期六的二进制代码
     */
    public static final int SATURDAY = 0b0000001;

    /**
     * 更新闹钟的标识符
     */
    public static final String UPDATE_ALARM_CLOCK_ID = "update_alarm_clock_id";

    /**
     * RepeatDialogFragment TAG
     */
    public static final String REPEAT_DIALOG_TAG = "RepeatDialogFragment";

    /**
     * RemarkDialogFragment TAG
     */
    public static final String REMARK_DIALOG_TAG = "RemarkDialogFragment";

    /**
     * 闹钟设置标识
     */
    public static final int SETTING_CLOCK_CODE = 0b10;

    /**
     * 下一次响铃时间
     */
    public static final String NEXT_RING_TIME = "next_ring_time";

    /**
     * 传递闹钟广播标识
     */
    public static final String ALARM_CLOCK_INTENT = "alarm_clock_intent";

    /**
     * 设置铃声标识：
     */
    public static final int RINGTONE_PICKER = 0b101;

    /**
     * 通知渠道id
     */
    public static final String CHANNEL_ID = "channel_id";
}
