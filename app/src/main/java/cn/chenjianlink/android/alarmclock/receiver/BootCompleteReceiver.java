package cn.chenjianlink.android.alarmclock.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;

import com.google.gson.Gson;

import java.util.List;

import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.repository.AlarmClockRepository;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

/**
 * @author chenjian
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "----------手机重启完毕！-------------");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //读取数据库 发送下一次闹钟的广播
        AlarmClockRepository repository = new AlarmClockRepository(context);
        List<AlarmClock> alarmClockList = repository.findAllWithoutLiveData();
        for (AlarmClock alarmClock : alarmClockList) {
            Intent alarmIntent = new Intent(context, AlarmClockReceiver.class);
            alarmIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, new Gson().toJson(alarmClock));
            PendingIntent pi = PendingIntent.getBroadcast(context, alarmClock.getClockId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            int nextRingTime = TimeUtil.nextRingTime(alarmClock.getRingCycle(), alarmClock.getHour(), alarmClock.getMinute());
            if (alarmClock.getRingCycle() == 0 && alarmClock.isStarted()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmClock.getHour());
                calendar.set(Calendar.MINUTE, alarmClock.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
                    LogUtil.i(TAG, "--重启广播发送成功！  只响一次的闹钟时间未到，广播已发送--" + alarmClock.toString());
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
                LogUtil.i(TAG, "--重启广播发送成功！  --" + alarmClock.toString());
            }

        }
    }
}
