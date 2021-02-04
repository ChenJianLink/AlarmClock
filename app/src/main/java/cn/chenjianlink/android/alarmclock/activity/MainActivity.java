package cn.chenjianlink.android.alarmclock.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.adapter.AlarmClockAdapter;
import cn.chenjianlink.android.alarmclock.adapter.AlarmClockItemTouchCallback;
import cn.chenjianlink.android.alarmclock.databinding.ActivityMainBinding;
import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.receiver.AlarmClockReceiver;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;
import cn.chenjianlink.android.alarmclock.viewmodel.AlarmClockViewModel;

/**
 * @author chenjian
 * 闹钟主界面，包含闹钟列表和添加闹钟按钮
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 创建MainActivity,将ToolBar控件显示
     *
     * @param savedInstanceState 保存的数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        AlarmClockViewModel alarmClockViewModel = new ViewModelProvider(this).get(AlarmClockViewModel.class);
        setSupportActionBar(binding.mainToolbar);

        //配置RecyclerView
        RecyclerView alarmClockList = binding.alarmClockList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        alarmClockList.setLayoutManager(layoutManager);
        final AlarmClockAdapter adapter = new AlarmClockAdapter(alarmClockViewModel);
        //设置监听事件
        alarmClockViewModel.getAlarmClockList().observe(this, new Observer<List<AlarmClock>>() {
            @Override
            public void onChanged(List<AlarmClock> alarmClockList) {
                LogUtil.i("Main", "data changed");
                adapter.updateAlarmClockList(alarmClockList);
                //发送闹钟广播
                for (AlarmClock alarmClock : alarmClockList) {
                    LogUtil.i("Intent", "send message");
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(MainActivity.this, AlarmClockReceiver.class);
                    intent.putExtra(CommonValue.ALARM_CLOCK_INTENT, new Gson().toJson(alarmClock));
                    int nextRingTime = TimeUtil.nextRingTime(alarmClock.getRingCycle(), alarmClock.getHour(), alarmClock.getMinute());
                    LogUtil.i("nextTime", "" + nextRingTime);
                    PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, alarmClock.getClockId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (alarmClock.isStarted()) {
                        //闹钟启用则发送PendingIntent,否则取消发送
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextRingTime, pi);
                    } else {
                        alarmManager.cancel(pi);
                    }
                }
            }
        });
        alarmClockList.setAdapter(adapter);

        //设置滑动删除
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new AlarmClockItemTouchCallback(adapter));
        itemTouchHelper.attachToRecyclerView(alarmClockList);
    }

    /**
     * 创建Toolbar上的menu
     *
     * @param menu menu对象
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tool_bar_menu, menu);
        return true;
    }

    /**
     * 设置menu点击事件
     *
     * @param item item对象
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AlarmClockManageActivity.class);
        startActivityForResult(intent, CommonValue.SETTING_CLOCK_CODE);
        return true;
    }

    /**
     * 处理返回信息（返回闹钟下一次响铃时间信息）
     *
     * @param requestCode 请求码
     * @param resultCode  请求结果码
     * @param data        包含处理结果的信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("ActivityResult", "is used");
        if (requestCode == CommonValue.SETTING_CLOCK_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String nextRingTime = data.getStringExtra(CommonValue.NEXT_RING_TIME);
                LogUtil.i("NextRingTime:", nextRingTime);
                Toast.makeText(this, nextRingTime, Toast.LENGTH_SHORT).show();
            }
        }
    }
}