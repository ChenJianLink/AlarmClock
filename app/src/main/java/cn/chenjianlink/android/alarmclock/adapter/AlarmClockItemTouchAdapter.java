package cn.chenjianlink.android.alarmclock.adapter;

/**
 * @author chenjian
 * RecyclerView Adapter回调接口
 */
public interface AlarmClockItemTouchAdapter {

    /**
     * 在侧滑删除时调用
     *
     * @param position 侧滑的位置
     */
    void onItemDismiss(int position);
}
