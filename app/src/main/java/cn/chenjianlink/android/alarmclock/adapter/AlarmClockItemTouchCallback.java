package cn.chenjianlink.android.alarmclock.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author chenjian
 * RecyclerView滑动事件
 */
public class AlarmClockItemTouchCallback extends ItemTouchHelper.Callback {

    private AlarmClockItemTouchAdapter adapter;

    public AlarmClockItemTouchCallback(AlarmClockItemTouchAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlag = ItemTouchHelper.LEFT;
        return makeMovementFlags(0,swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getBindingAdapterPosition());
    }
}
