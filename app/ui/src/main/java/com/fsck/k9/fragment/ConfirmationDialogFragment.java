package com.fsck.k9.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.fsck.k9.Account;

import java.io.Serializable;

import timber.log.Timber;

public class ConfirmationDialogFragment extends DialogFragment implements OnClickListener,
        OnCancelListener {
    private ConfirmationDialogFragmentListener mListener;

    private static final String ARG_DIALOG_ID = "dialog_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_CONFIRM_TEXT = "confirm";
    private static final String ARG_CANCEL_TEXT = "cancel";
    private static final String ARG_ACCOUNT="Account";
    private String title;


    public static ConfirmationDialogFragment newInstance(int dialogId, String title, String message,
                                                         String confirmText, String cancelText, Account account) {
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_ID, dialogId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_CONFIRM_TEXT, confirmText);
        args.putString(ARG_CANCEL_TEXT, cancelText);
        args.putSerializable(ARG_ACCOUNT,account);
        fragment.setArguments(args);

        return fragment;
    }

    public static ConfirmationDialogFragment newInstance(int dialogId, String title, String message,
                                                         String cancelText, Account account) {
        return newInstance(dialogId, title, message, null, cancelText,account);
    }


    public interface ConfirmationDialogFragmentListener {
        void doPositiveClick(int dialogId,Account account);

        void doNegativeClick(int dialogId,Account account);

        void dialogCancelled(int dialogId,Account account);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);
        String confirmText = args.getString(ARG_CONFIRM_TEXT);
        String cancelText = args.getString(ARG_CANCEL_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        if (confirmText != null && cancelText != null) {
            builder.setPositiveButton(confirmText, this);
            if (title.equals("删除账户")) {
                builder.setNegativeButton(cancelText, this);
            }
            //TODO 取消继续
//            builder.setNegativeButton(cancelText, this);
        } else if (cancelText != null) {
            builder.setNeutralButton(cancelText, this);
        } else {
            throw new RuntimeException("Set at least cancelText!");
        }

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {

                getListener().doPositiveClick(getDialogId(),getAccount());
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE: {
                getListener().doNegativeClick(getDialogId(),getAccount());
                break;
            }
            case DialogInterface.BUTTON_NEUTRAL: {
                getListener().doNegativeClick(getDialogId(),getAccount());
                break;
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getListener().dialogCancelled(getDialogId(),getAccount());
    }

    private int getDialogId() {
        return getArguments().getInt(ARG_DIALOG_ID);
    }
    private Account getAccount(){
        return (Account) getArguments().getSerializable(ARG_ACCOUNT);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ConfirmationDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            Timber.d("%s did not implement ConfirmationDialogFragmentListener", activity);
        }
    }

    public ConfirmationDialogFragmentListener getListener() {
        if (mListener != null) {
            return mListener;
        }

        // fallback to getTargetFragment...
        try {
            return (ConfirmationDialogFragmentListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().getClass() +
                    " must implement ConfirmationDialogFragmentListener");
        }
    }
}
