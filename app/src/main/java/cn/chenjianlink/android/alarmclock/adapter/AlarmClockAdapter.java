package cn.chenjianlink.android.alarmclock.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.chenjianlink.android.alarmclock.activity.AlarmClockManageActivity;
import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.receiver.AlarmClockReceiver;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;
import cn.chenjianlink.android.alarmclock.viewmodel.AlarmClockViewModel;

/**
 * @author chenjian
 * 首页闹钟列表显示RecyclerView适配器
 */
public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.ViewHolder> implements AlarmClockItemTouchAdapter {

    private List<AlarmClock> alarmClockList;

    private AlarmClockViewModel alarmClockViewModel;

    private AlarmManager alarmManager;

    private Context context;

    public AlarmClockAdapter(AlarmClockViewModel alarmClockViewModel) {
        this.alarmClockViewModel = alarmClockViewModel;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        //获取子项视图，便于设置点击事件
        private View alarmClockView;
        //响铃时间
        private TextView clockRingTime;
        //闹钟信息
        private TextView clockRingMessage;
        //闹钟对象
        private int alarmClockId;

        //闹钟启用情况
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private Switch startSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmClockView = itemView;
            clockRingTime = itemView.findViewById(R.id.clock_ring_time);
            clockRingMessage = itemView.findViewById(R.id.clock_ring_message);
            startSwitch = itemView.findViewById(R.id.start_switch);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_clock_item, parent, false);
        LogUtil.i("parent", parent.getContext().toString());
        alarmManager = (AlarmManager) parent.getContext().getSystemService(Context.ALARM_SERVICE);
        final ViewHolder holder = new ViewHolder(view);

        holder.alarmClockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent.getContext(), AlarmClockManageActivity.class);
                intent.putExtra(CommonValue.UPDATE_ALARM_CLOCK_ID, holder.alarmClockId);
                ((Activity) parent.getContext()).startActivityForResult(intent, CommonValue.SETTING_CLOCK_CODE);
            }
        });

        holder.startSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isStared) {
                LogUtil.i("switch", " is Change");
                //闹钟开关被点击了
                if (compoundButton.isPressed()) {
                    LogUtil.i("switch", " is " + isStared);
                    AlarmClock alarmClock = alarmClockList.get(holder.getBindingAdapterPosition());
                    alarmClock.setStarted(isStared);
                    alarmClockViewModel.updateAlarmClockStart(alarmClock);
                    if (isStared) {
                        int nextRingTime = TimeUtil.nextRingTime(alarmClock.getRingCycle(), alarmClock.getHour(), alarmClock.getMinute());
                        Toast.makeText(parent.getContext(), TimeUtil.dateFormat(nextRingTime), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //判断列表非空
        LogUtil.i("alarmClockList", String.valueOf(alarmClockList != null));
        if (alarmClockList != null && alarmClockList.size() > 0) {
            AlarmClock alarmClock = alarmClockList.get(position);
            //保存id
            holder.alarmClockId = alarmClock.getClockId();
            String ringTime = TimeUtil.getShowTime(alarmClock.getHour(), alarmClock.getMinute());
            LogUtil.i("ringTime", ringTime);
            //显示响铃时间
            holder.clockRingTime.setText(ringTime);
            //设置是否启用
            holder.startSwitch.setChecked(alarmClock.isStarted());
            //显示响铃信息
            if (alarmClock.getRingCycle() == 0) {
                holder.clockRingMessage.setText(R.string.repeat_once);
            } else {
                holder.clockRingMessage.setText(TimeUtil.showRingCycle(alarmClock.getRingCycle(), alarmClock.getHour(), alarmClock.getMinute()));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (alarmClockList != null) {
            return alarmClockList.size();
        } else {
            return 0;
        }
    }

    /**
     * 更新原有的Item
     *
     * @param alarmClockList 更改的ItemList
     */
    public void updateAlarmClockList(List<AlarmClock> alarmClockList) {
        this.alarmClockList = alarmClockList;
        LogUtil.i("alarmClockList2", String.valueOf(alarmClockList != null));
        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(int position) {
        AlarmClock alarmClock = alarmClockList.remove(position);
        //删除闹钟需要将已经启用的闹钟的定时响铃取消
        if (alarmClock.isStarted()) {
            Intent intent = new Intent(context, AlarmClockReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, alarmClock.getClockId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pi);
        }
        notifyItemRemoved(position);
        alarmClockViewModel.removeAlarmClock(alarmClock);
    }
}
