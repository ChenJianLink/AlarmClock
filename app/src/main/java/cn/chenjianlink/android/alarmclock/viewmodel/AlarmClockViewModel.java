package cn.chenjianlink.android.alarmclock.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.model.Messager;
import cn.chenjianlink.android.alarmclock.repository.AlarmClockRepository;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

/**
 * @author chenjian
 * Activity对应的ViewModel
 */
public class AlarmClockViewModel extends AndroidViewModel {

    private final AlarmClockRepository repository;

    private final LiveData<List<AlarmClock>> alarmClockList;

    /**
     * 用于临时存放响铃周期的列表
     */
    private List<Integer> ringRepeatList;

    /**
     * 用于临时存放铃声uri
     */
    private String ringMusicUri;

    public AlarmClockViewModel(@NonNull Application application) {
        super(application);
        repository = new AlarmClockRepository(application);
        alarmClockList = repository.findAll();
        ringRepeatList = new ArrayList<>();
    }

    /**
     * 获取闹钟列表
     *
     * @return LiveData包装的闹钟列表
     */
    public LiveData<List<AlarmClock>> getAlarmClockList() {
        LogUtil.i("ViewModel.getAlarmClockList", alarmClockList.toString());
        return alarmClockList;
    }

    /**
     * 保存闹钟（新建闹钟，更改原有闹钟的设置）
     *
     * @param alarmClock 要保存的闹钟对象
     */
    public void saveAlarmClock(AlarmClock alarmClock) {
        //将闹钟重复信息封装到AlarmClock对象中
        if (ringRepeatList.size() > 0) {
            int ringCycle = changeRingRepeatListToInt();
            alarmClock.setRingCycle(ringCycle);
        }
        alarmClock.setRingMusicUri(ringMusicUri);
        alarmClock.setStarted(true);
        //根据传入id判断是新建一个闹钟还是更新一个闹钟
        LogUtil.i("ViewModel.saveAlarmClock", "AlarmClockId:" + alarmClock.getClockId());
        if (alarmClock.getClockId() != 0) {
            LogUtil.i("ViewModel.saveAlarmClock", "更新闹钟");
            repository.update(alarmClock);
        } else {
            LogUtil.i("ViewModel.saveAlarmClock", "插入新闹钟");
            repository.insert(alarmClock);
        }
        //保存之后将日期列表清空
        clearCache();
    }

    /**
     * 通过id查找闹钟对象
     *
     * @param alarmClockId 闹钟id
     * @return id对应的闹钟对象
     */
    public AlarmClock findAlarmClockById(int alarmClockId) {
        //查找得到id对应的AlarmClock对象
        AlarmClock alarmClock = repository.findById(alarmClockId);
        ringMusicUri = alarmClock.getRingMusicUri();
        //判断闹钟是否只响一次
        int ringCycle = alarmClock.getRingCycle();
        if (ringCycle != 0) {
            //将一些闹钟重复信息进行解封装
            ringRepeatList = TimeUtil.buildList(ringCycle);
        }
        return alarmClock;
    }

    /**
     * 返回响铃信息
     *
     * @param hour   设置的小时
     * @param minute 设置的分钟
     * @return 包含闹钟下一次响铃时间和响铃重复周期
     */
    public Messager getClockMessage(int hour, int minute) {
        Messager messager = new Messager();
        LogUtil.i("getClockMessage", "" + ringRepeatList.size());
        if (ringRepeatList.size() == 0) {
            messager.setNextRingTime(TimeUtil.dateFormat(TimeUtil.nextTime(hour, minute)));
        } else {
            messager.setNextRingTime(TimeUtil.dateFormat(TimeUtil.nextTime(hour, minute, ringRepeatList)));
            messager.setRingRepeatTime(TimeUtil.repeatMessage(ringRepeatList));
        }
        return messager;
    }

    /**
     * 清除临时数据
     */
    public void clearCache() {
        ringRepeatList.clear();
        ringMusicUri = null;
    }

    /**
     * 将List形式的响铃信息封装成int类型
     *
     * @return 封装信息
     */
    private int changeRingRepeatListToInt() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (ringRepeatList.contains(i + 1)) {
                builder.append(1);
            } else {
                builder.append(0);
            }
        }
        return Integer.parseUnsignedInt(builder.toString(), 2);
    }

    /**
     * 更新闹钟是否启用的信息
     *
     * @param alarmClock 要更新的闹钟对象
     */
    public void updateAlarmClockStart(AlarmClock alarmClock) {
        repository.update(alarmClock);
    }

    /**
     * 移除闹钟
     *
     * @param alarmClock 要移除的闹钟对象
     */
    public void removeAlarmClock(AlarmClock alarmClock) {
        repository.delete(alarmClock);
    }

    /**
     * getter setter 方法
     * *********************************************************************************
     */
    public List<Integer> getRingRepeatList() {
        return ringRepeatList;
    }

    public void setRingMusicUri(String ringMusicUri) {
        this.ringMusicUri = ringMusicUri;
    }

    public String getRingMusicUri() {
        return ringMusicUri;
    }

}
