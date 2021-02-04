package cn.chenjianlink.android.alarmclock.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.ActivityAlarmClockRingBinding;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.service.AlarmClockService;
import cn.chenjianlink.android.alarmclock.service.AlarmClockServiceProvider;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

/**
 * @author chenjian
 * 响铃界面Activity
 */
public class AlarmClockRingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlarmClockRingActivity";

    private TextView nowTime;

    private TextView alarmClockLabel;

    private Intent serviceIntent;

    private AlarmClockServiceProvider serviceProvider;

    private RingServiceConnection connection;

    /**
     * 判断用户是否按下界面按钮
     */
    private boolean pauseOrNot;

    /**
     * 计时器，在界面无操作响应一分钟之后进行
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!pauseOrNot) {
                        serviceProvider.oneMinClose();
                        AlarmClockRingActivity.this.finish();
                        LogUtil.i(TAG, "--------响铃一分钟无反应。。--成功调用服务里五分钟后再响的方法!----------------");
                    } else {
                        LogUtil.i(TAG, "--------点击了暂停或关闭闹钟。--一分钟失效！---------------");
                    }
                }
            }, 60 * 1000);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        ActivityAlarmClockRingBinding binding = ActivityAlarmClockRingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //锁屏状态下 唤醒屏幕 保持常亮
        setTurnScreenOn(true);
        setShowWhenLocked(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        //锁屏时亮屏
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);

        nowTime = binding.nowTime;
        alarmClockLabel = binding.alarmClockLabel;
        Button clockClose = binding.clockClose;
        Button clockSnooze = binding.clockSnooze;
        clockClose.setOnClickListener(this);
        clockSnooze.setOnClickListener(this);

        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //闹钟来时绑定对应的服务
        connection = new RingServiceConnection();
        serviceIntent = new Intent(this, AlarmClockService.class);
        bindService(serviceIntent, connection, 0);
        AlarmClock alarmClock = new Gson().fromJson(getIntent().getStringExtra(CommonValue.ALARM_CLOCK_INTENT), AlarmClock.class);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        nowTime.setText(TimeUtil.getShowTime(hour, minute));
        assert alarmClock != null;
        if (alarmClock.getRemark() != null) {
            alarmClockLabel.setText(alarmClock.getRemark());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clock_close:
                pauseOrNot = true;
                serviceProvider.close();
                finish();
                break;
            case R.id.clock_snooze:
                pauseOrNot = true;
                serviceProvider.pauseFiveMinRing();
                finish();
                break;
            default:
        }
    }

    /**
     * 中间人接口连接类
     */
    private class RingServiceConnection implements ServiceConnection {
        //当服务被连接的时候调用 服务识别成功 绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            serviceProvider = (AlarmClockServiceProvider) service;
            LogUtil.i(TAG, "-------在activity里面得到了服务的中间人对象-------------");
        }

        //当服务失去连接的时候调用（一般进程挂了，服务被异常杀死）
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        stopService(serviceIntent);
        super.onDestroy();
    }
}