package cn.chenjianlink.android.alarmclock.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Objects;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.ActivityAlarmClockManageBinding;
import cn.chenjianlink.android.alarmclock.dialog.RemarkDialog;
import cn.chenjianlink.android.alarmclock.dialog.RepeatDialog;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.model.Messager;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.viewmodel.AlarmClockViewModel;

/**
 * @author chenjian
 * 闹钟添加页面对应的Activity
 */
public class AlarmClockManageActivity extends AppCompatActivity implements View.OnClickListener, RepeatDialog.NoticeDialogListener, TimePicker.OnTimeChangedListener, RemarkDialog.RemarkNoticeDialogListener {

    private AlarmClockViewModel alarmClockViewModel;

    private TimePicker clockTimePicker;

    private DialogFragment repeatDialog;

    private RemarkDialog remarkDialog;

    private TextView nextRingTime;

    private TextView alarmClockSettingRepeat;

    private TextView ringMusic;

    private CheckBox isVibrated;

    private TextView remarkTextShow;


    /**
     * 临时存放AlarmClockId,若为0，则当前是新建闹钟，反之是更新闹钟
     */
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAlarmClockManageBinding binding = ActivityAlarmClockManageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        alarmClockViewModel = new ViewModelProvider(this).get(AlarmClockViewModel.class);
        clockTimePicker = binding.clockTimePicker;
        nextRingTime = binding.nextRingTime;
        alarmClockSettingRepeat = binding.alarmClockSettingRepeat;
        ringMusic = binding.clockSettingRingMusic;
        isVibrated = binding.clockIsVibrated;
        remarkTextShow = binding.clockSettingTag;

        Toolbar toolbar = binding.setClockToolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(this);
        binding.settingSave.setOnClickListener(this);
        binding.ringRepeat.setOnClickListener(this);
        binding.clockRingMusic.setOnClickListener(this);
        binding.settingCancel.setOnClickListener(this);
        clockTimePicker.setOnTimeChangedListener(this);
        binding.clockTag.setOnClickListener(this);

        //根据Intent内是否带有参数来判断是新建闹钟还是更新闹钟
        Intent intent = getIntent();
        flag = intent.getIntExtra(CommonValue.UPDATE_ALARM_CLOCK_ID, 0);
        dataInit(flag);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //保存
            case R.id.setting_save:
                LogUtil.i("时间管理", "保存闹钟");
                AlarmClock alarmClock = new AlarmClock();
                alarmClock.setClockId(flag);
                alarmClock.setHour(clockTimePicker.getHour());
                alarmClock.setMinute(clockTimePicker.getMinute());
                alarmClock.setVibrated(isVibrated.isChecked());
                alarmClock.setRemark(remarkTextShow.getText().toString());
                //获取下一次响铃时间
                String nextRingTime = alarmClockViewModel.getClockMessage(clockTimePicker.getHour(), clockTimePicker.getMinute()).getNextRingTime();
                alarmClockViewModel.saveAlarmClock(alarmClock);

                //设置返回信息
                Intent resultIntent = new Intent();
                resultIntent.putExtra(CommonValue.NEXT_RING_TIME, nextRingTime);
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            //设置重复形式
            case R.id.ring_repeat:
                showRepeatNoticeDialog();
                break;
            //设置铃声
            case R.id.clock_ring_music:
                Intent ringMusicIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                String musicUri = alarmClockViewModel.getRingMusicUri();
                if (musicUri != null) {
                    ringMusicIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(musicUri));
                }
                startActivityForResult(ringMusicIntent, CommonValue.RINGTONE_PICKER);
                break;
            case R.id.clock_tag:
                LogUtil.i("remark", "is checked");
                showRemarkNoticeDialog();
                break;
            default:
                //默认取消或退出，清空临时数据
                alarmClockViewModel.clearCache();
                finish();
        }
    }

    /**
     * 创建重复周期弹窗
     */
    private void showRepeatNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        repeatDialog = new RepeatDialog();
        repeatDialog.show(getSupportFragmentManager(), CommonValue.REPEAT_DIALOG_TAG);
        repeatDialog.setCancelable(false);
    }

    /**
     * 创建备注弹窗
     */
    private void showRemarkNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        remarkDialog = new RemarkDialog();
        remarkDialog.setRemark(remarkTextShow.getText().toString());
        remarkDialog.show(getSupportFragmentManager(), CommonValue.REMARK_DIALOG_TAG);
        remarkDialog.setCancelable(false);
    }

    /**
     * 读取被选取的日期
     */
    @Override
    public void onDialogPositiveClick() {
        timeMessageShow(clockTimePicker.getHour(), clockTimePicker.getMinute());
        repeatDialog.dismiss();
    }

    /**
     * 关闭对话框
     */
    @Override
    public void onDialogNegativeClick() {
        repeatDialog.dismiss();
    }

    /**
     * 设置时间改变对应监听时间
     *
     * @param timePicker timePicker
     * @param hour       小时
     * @param minute     分钟
     */
    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        timeMessageShow(hour, minute);
    }

    /**
     * 设置时间信息（当前时间到下一次响铃时间的时间差，响铃周期）
     *
     * @param hour   设置的响铃小时
     * @param minute 设置的响铃分钟
     */
    private void timeMessageShow(int hour, int minute) {
        Messager messager = alarmClockViewModel.getClockMessage(hour, minute);
        LogUtil.i("message", messager.getNextRingTime());
        nextRingTime.setText(messager.getNextRingTime());
        if (messager.getRingRepeatTime() != null) {
            alarmClockSettingRepeat.setText(messager.getRingRepeatTime());
            LogUtil.i("message", messager.getNextRingTime());
        } else {
            alarmClockSettingRepeat.setText(R.string.repeat_once);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理ringtone picker
        if (requestCode == CommonValue.RINGTONE_PICKER) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    LogUtil.i("AlarmClockManageActivity", uri.toString());
                    String uriMessage = uri.toString();
                    alarmClockViewModel.setRingMusicUri(uriMessage);
                    //设置响铃标题
                    Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                    ringMusic.setText(ringtone.getTitle(this));
                } else {
                    alarmClockViewModel.setRingMusicUri(null);
                    ringMusic.setText(R.string.default_clock_ring_music);
                }
            }
        }
    }

    @Override
    public void onRemarkDialogPositiveClick(String remark) {
        remarkTextShow.setText(remark);
        remarkDialog.dismiss();
    }

    /**
     * 若为修改闹钟则初始化闹钟数据
     *
     * @param alarmClockId 闹钟id
     */
    private void dataInit(int alarmClockId) {
        if (alarmClockId != 0) {
            LogUtil.i("AlarmClockActivity.onCreate", "Intent data not null");
            AlarmClock alarmClock = alarmClockViewModel.findAlarmClockById(alarmClockId);
            clockTimePicker.setHour(alarmClock.getHour());
            clockTimePicker.setMinute(alarmClock.getMinute());
            timeMessageShow(alarmClock.getHour(), alarmClock.getMinute());
            String musicUri = alarmClock.getRingMusicUri();
            if (musicUri != null) {
                //获取响铃音乐标题
                Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(musicUri));
                ringMusic.setText(ringtone.getTitle(this));
            } else {
                ringMusic.setText(R.string.default_clock_ring_music);
            }
            isVibrated.setChecked(alarmClock.isVibrated());
            remarkTextShow.setText(alarmClock.getRemark());
        }
    }
}