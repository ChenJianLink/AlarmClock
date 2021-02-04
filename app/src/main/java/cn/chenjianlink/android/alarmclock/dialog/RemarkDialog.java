package cn.chenjianlink.android.alarmclock.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import cn.chenjianlink.android.alarmclock.R;

/**
 * @author chenjian
 * 备注对话框
 */
public class RemarkDialog extends DialogFragment {

    private String remark;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.remark_dialog, null, false);
        final EditText remarkText = view.findViewById(R.id.remark_text);
        remarkText.setText(remark);
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(R.string.clock_setting_tag)
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onRemarkDialogPositiveClick(remarkText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true);
        return builder.create();
    }

    /**
     * 对话框监听事件接口
     */
    public interface RemarkNoticeDialogListener {
        /**
         * 对话框确认方法
         *
         * @param remark 备注
         */
        public void onRemarkDialogPositiveClick(String remark);
    }

    private RemarkNoticeDialogListener listener;

    /**
     * Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (RemarkNoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
