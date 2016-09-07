package cn.zwf.checker;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * 自定义DialogFragment，优化生命周期管理
 * Created by ZhangWF(zhangwf0929@gmail.com) on 15/6/30.
 */
public class ProgressDialogFragment extends DialogFragment {

    private ProgressDialog mDialog;
    private CharSequence mMsg;
    private DialogInterface.OnCancelListener mOnCancelListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage(mMsg);
        setCancelable(true);
        return mDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(dialog);
        }
    }

    public void setMessage(CharSequence s) {
        mMsg = s;
        if (mDialog != null) {
            mDialog.setMessage(s);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }
}
