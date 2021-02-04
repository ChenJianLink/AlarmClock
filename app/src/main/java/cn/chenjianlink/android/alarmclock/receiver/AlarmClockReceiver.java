package cn.chenjianlink.android.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.chenjianlink.android.alarmclock.service.AlarmClockService;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * @author chenjian
 * 闹钟广播接收器，用于接收应用程序启用闹钟的广播
 */
public class AlarmClockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("AlarmClockReceiver", "--------------闹钟广播接收器开始执行-------------");
        String clockJson = intent.getStringExtra(CommonValue.ALARM_CLOCK_INTENT);
        LogUtil.i("AlarmClockMessage", clockJson);

        //构建Intent
        Intent ringIntent = new Intent(context, AlarmClockService.class);
        ringIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(ringIntent);
    }

}
