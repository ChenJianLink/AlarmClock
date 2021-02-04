package cn.chenjianlink.android.alarmclock.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Objects;

import cn.chenjianlink.android.alarmclock.activity.AlarmClockRingActivity;
import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.receiver.AlarmClockReceiver;
import cn.chenjianlink.android.alarmclock.repository.AlarmClockRepository;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

/**
 * @author chenjian
 * 响铃服务
 */
public class AlarmClockService extends Service {

    private static final String TAG = "AlarmClockService";

    private AlarmManager alarmManager;

    private AlarmClock alarmClock;

    private Intent toReceiverIntent;

    private Vibrator vibrator;

    private Ringtone ringtone;

    private PowerManager powerManager;

    private static final String CLOSE = "close";

    private static final String SNOOZE = "snooze";

    private static final String DELETE = "delete";

    private static final int FOREGROUND_SERVICE = 0b1010101;

    private static final String NOTIFICATION = "notification";

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "-----------onCreate---------");
        alarmManager = getSystemService(AlarmManager.class);
        vibrator = getSystemService(Vibrator.class);
        powerManager = getSystemService(PowerManager.class);
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannel();
    }

    @SuppressLint({"NewApi", "WrongConstant"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "--------------onStartCommand----------");
        LogUtil.i(TAG, "flag:" + flags + ",startId:" + startId);
        //判断是否是前台服务，若是根据Intent则执行关闭或者贪睡(二进宫)
        if (intent.getIntExtra(NOTIFICATION, 0) == FOREGROUND_SERVICE) {
            String action = intent.getAction();
            //关闭闹钟
            notificationManager.cancel(alarmClock.getClockId());
            if (Objects.equals(action, CLOSE)) {
                dismiss();
            } else if (Objects.equals(action, SNOOZE) || Objects.equals(action, DELETE)) {
                //贪睡模式
                snooze();
            }
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        String clockJson = intent.getStringExtra(CommonValue.ALARM_CLOCK_INTENT);
        //若是service还未执行过，则执行相应的操作，若已经执行过了，则将重复的闹钟往后延
        if (startId == 1) {

            alarmClock = new Gson().fromJson(clockJson, AlarmClock.class);
            //在闹铃界面点击 暂停再响 或 关闭 时 发送的Intent
            toReceiverIntent = new Intent(this, AlarmClockReceiver.class);
            toReceiverIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);

            //若用户正在和设备交互，则弹出通知，反之弹出Activity
            if (powerManager.isInteractive()) {
                buildNotification(alarmClock);
            } else {
                callActivity(clockJson);
            }

            //设置响铃音乐以及震动
            if (alarmClock.getRingMusicUri() != null) {
                ringtone = RingtoneManager.getRingtone(this, Uri.parse(alarmClock.getRingMusicUri()));
                ringtone.setLooping(true);
                ringtone.play();
            }
            if (alarmClock.isVibrated()) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000, 1000}, 0));
            }

        } else {
            AlarmClock clock = new Gson().fromJson(clockJson, AlarmClock.class);
            PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, clock.getClockId(), toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            int nextRingTime = TimeUtil.nextRingTime(clock.getRingCycle(), clock.getHour(), clock.getMinute());
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
            LogUtil.i(TAG, "时间冲突，该闹钟延后" + TimeUtil.dateFormat(nextRingTime) + "执行");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return new AlarmClockRingServiceProvider();
    }

    /**
     * 对外暴露接口
     */
    private class AlarmClockRingServiceProvider extends Binder implements AlarmClockServiceProvider {
        /**
         * 5分钟贪睡功能
         */
        @Override
        public void pauseFiveMinRing() {
            snooze();
        }

        /**
         * 关闭闹钟
         */
        @Override
        public void close() {
            dismiss();
        }

        /**
         * 响铃后如果没有在一分钟内做出操作，默认直接进入贪睡模式
         */
        @Override
        public void oneMinClose() {
            pauseFiveMinRing();
        }
    }

    /**
     * 贪睡模式真实实现
     */
    private void snooze() {
        stopMusic();
        stopVibrator();
        int alarmClockId = alarmClock.getClockId();
        PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, pi);
        LogUtil.i(TAG, "----------5分钟后响铃的广播发送完毕！-----------");
    }

    /**
     * 关闭闹钟真实实现
     */
    private void dismiss() {
        stopMusic();
        stopVibrator();
        int alarmClockId = alarmClock.getClockId();
        int ringCycle = alarmClock.getRingCycle();
        int hour = alarmClock.getHour();
        int minute = alarmClock.getMinute();
        PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ringCycle == 0) {
            //如果闹钟只响一次，则更改数据库将闹钟启动选项改成false
            alarmManager.cancel(pi);
            AlarmClockRepository repository = new AlarmClockRepository(AlarmClockService.this);
            alarmClock.setStarted(false);
            repository.update(alarmClock);
            LogUtil.i(TAG, "--只响一次的闹钟广播已取消，数据库更新完毕！！-----");
        } else {
            int nextRingTime = TimeUtil.nextRingTime(ringCycle, hour, minute);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
            LogUtil.i(TAG, "--距离下一次闹钟的剩余时间为" + TimeUtil.dateFormat(nextRingTime) + "-----");
        }
    }

    /**
     * 关闭铃声
     */
    private void stopMusic() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

    /**
     * 停止震动
     */
    private void stopVibrator() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(CommonValue.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 创建通知
     *
     * @param alarmClock 响铃对应的闹钟对象
     */
    @SuppressLint("WrongConstant")
    private void buildNotification(AlarmClock alarmClock) {
        PendingIntent piClose = buildPendingIntent(CLOSE);
        PendingIntent piSnooze = buildPendingIntent(SNOOZE);
        PendingIntent piDelete = buildPendingIntent(DELETE);

        Notification notification = new NotificationCompat.Builder(this, CommonValue.CHANNEL_ID)
                .setContentTitle(TimeUtil.getShowTime(alarmClock.getHour(), alarmClock.getMinute()))
                .setContentText(alarmClock.getRemark())
                .setWhen(System.currentTimeMillis())
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.close_clock), piClose)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.ring_later), piSnooze)
                .setDeleteIntent(piDelete)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(alarmClock.getClockId(), notification);
    }

    /**
     * 启动响铃界面
     *
     * @param clockJson 闹铃数据
     */
    private void callActivity(String clockJson) {
        //发送到 闹铃界面的意图
        Intent alarmClockRingIntent = new Intent(this, AlarmClockRingActivity.class);
        alarmClockRingIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        alarmClockRingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmClockRingIntent);
    }

    /**
     * 根据action构建对应的PendingIntent
     *
     * @param action 对应的action
     * @return PendingIntent
     */
    private PendingIntent buildPendingIntent(String action) {
        Intent intent = new Intent(this, AlarmClockService.class);
        intent.setAction(action);
        intent.putExtra(NOTIFICATION, FOREGROUND_SERVICE);
        return PendingIntent.getService(this, 0, intent, 0);
    }

}
