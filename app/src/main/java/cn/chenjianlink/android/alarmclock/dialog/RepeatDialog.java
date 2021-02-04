package cn.chenjianlink.android.alarmclock.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.viewmodel.AlarmClockViewModel;

/**
 * @author chenjian
 * 响铃重复对话框
 */
public class RepeatDialog extends DialogFragment {

    /**
     * 选项实际选中列表
     */
    private List<Integer> selectedItems;

    /**
     * 选项是否被选中列表
     */
    private boolean[] checkedItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmClockViewModel viewModel = new ViewModelProvider(requireActivity()).get(AlarmClockViewModel.class);
        selectedItems = viewModel.getRingRepeatList();
        checkedItems = new boolean[7];
        setCheckedItems();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(R.string.setting_repeat)
                .setMultiChoiceItems(R.array.week, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(which + 1);
                        } else if (selectedItems.contains(which + 1)) {
                            selectedItems.remove(Integer.valueOf(which + 1));
                        }
                        Collections.sort(selectedItems);
                        LogUtil.i("week list", "items:" + selectedItems.toString());
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogNegativeClick();
                    }
                })
                .setCancelable(true);
        return builder.create();
    }

    /**
     * 对话框监听事件接口
     */
    public interface NoticeDialogListener {
        /**
         * 对话框确认方法
         */
        public void onDialogPositiveClick();

        /**
         * 对话框取消方法
         */
        public void onDialogNegativeClick();
    }

    private NoticeDialogListener listener;

    /**
     * Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    /**
     * 初始化多选项表单
     */
    public void setCheckedItems() {
        for (int item : selectedItems) {
            checkedItems[item - 1] = true;
        }
    }
}
