package com.fsck.k9.ui.messageview;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.fsck.k9.Account;
import com.fsck.k9.DI;
import com.fsck.k9.K9;
import com.fsck.k9.MyApplication;
import com.fsck.k9.Preferences;
import com.fsck.k9.activity.EnclosureActivity;
import com.fsck.k9.activity.MessageList;
import com.fsck.k9.db.FujianBeanDB;
import com.fsck.k9.ui.choosefolder.ChooseFolderActivity;
import com.fsck.k9.activity.MessageLoaderHelper;
import com.fsck.k9.activity.MessageLoaderHelper.MessageLoaderCallbacks;
import com.fsck.k9.activity.MessageLoaderHelperFactory;
import com.fsck.k9.controller.MessageReference;
import com.fsck.k9.controller.MessagingController;
import com.fsck.k9.fragment.AttachmentDownloadDialogFragment;
import com.fsck.k9.fragment.ConfirmationDialogFragment;
import com.fsck.k9.fragment.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
import com.fsck.k9.mail.Flag;
import com.fsck.k9.mailstore.AttachmentViewInfo;
import com.fsck.k9.mailstore.LocalMessage;
import com.fsck.k9.mailstore.MessageViewInfo;
import com.fsck.k9.ui.R;
import com.fsck.k9.ui.ThemeManager;
import com.fsck.k9.ui.dialog.CommonDialog;
import com.fsck.k9.ui.dialog.DownloadDialog;
import com.fsck.k9.ui.helper.SizeFormatter;
import com.fsck.k9.ui.messageview.CryptoInfoDialog.OnClickShowCryptoKeyListener;
import com.fsck.k9.ui.messageview.MessageCryptoPresenter.MessageCryptoMvpView;
import com.fsck.k9.ui.settings.account.AccountSettingsActivity;
import com.fsck.k9.util.NetworkUtils;
import com.fsck.k9.util.OpenFile;
import com.fsck.k9.util.PermissionPageUtils;
import com.fsck.k9.view.MessageCryptoDisplayStatus;
import com.tbruyelle.rxpermissions2.RxPermissions;

import timber.log.Timber;


public class MessageViewFragment extends Fragment implements ConfirmationDialogFragmentListener,
        AttachmentViewCallback, OnClickShowCryptoKeyListener {

    private static final String ARG_REFERENCE = "reference";

    private static final int ACTIVITY_CHOOSE_FOLDER_MOVE = 1;
    private static final int ACTIVITY_CHOOSE_FOLDER_COPY = 2;
    private static final int REQUEST_CODE_CREATE_DOCUMENT = 3;

    public static final int REQUEST_MASK_LOADER_HELPER = (1 << 8);
    public static final int REQUEST_MASK_CRYPTO_PRESENTER = (1 << 9);

    public static final int PROGRESS_THRESHOLD_MILLIS = 500 * 1000;
    private final String Fujian_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/我的文档/附件管理";
    private static MessageList mMessageList;
    private String messageReferenceString;
    private DownloadDialog dialog1;

    public static MessageViewFragment newInstance(MessageReference reference, MessageList messageList) {
        mMessageList = messageList;
        MessageViewFragment fragment = new MessageViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE, reference.toIdentityString());
        fragment.setArguments(args);
        return fragment;
    }

    public static void newInstance1(Activity mContext) {
        mContext.onBackPressed();
    }

    private final ThemeManager themeManager = DI.get(ThemeManager.class);
    private final MessageLoaderHelperFactory messageLoaderHelperFactory = DI.get(MessageLoaderHelperFactory.class);

    private MessageTopView mMessageView;

    private Account mAccount;
    private MessageReference mMessageReference;
    private LocalMessage mMessage;
    private MessagingController mController;
    private DownloadManager downloadManager;
    private int is_Open_Save = 0;
    private int FileSize = 2187509;
    private String NullString = "";
    private Handler handler = new Handler();
    //Todo  Handler
    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("tag", "sssssssssssssssssss1212121212121212121212121212");
                    if (currentAttachmentViewInfo != null) {
                        String displayName = setFileReleaseNames(Fujian_path, currentAttachmentViewInfo.displayName, currentAttachmentViewInfo.displayName);
                        String save_name_path = filePath() + File.separator + displayName;
                        if (is_Open_Save == 1) { //保存
                            SaveDateBase(displayName, save_name_path);
                            Log.i("tag", "sssssssssssssssssss7777777777777777777777777");
                        } else if (is_Open_Save == 2) {//打开
                            Log.i("tag", "sssssssssssssssssss8888888888888888888888888");
                            SaveDateBase(currentAttachmentViewInfo.displayName, save_name_path);
                            if (save_name_path.endsWith(".doc") || save_name_path.endsWith(".docx") || save_name_path.endsWith(".ppt")
                                    || save_name_path.endsWith(".pptx") || save_name_path.endsWith(".xls") || save_name_path.endsWith(".xlsx")
                                    || save_name_path.endsWith(".txt") || save_name_path.endsWith(".htxt") || save_name_path.endsWith(".pdf")
                                    || save_name_path.endsWith(".epub") || save_name_path.endsWith(".chm") || save_name_path.endsWith(".hveb")
                                    || save_name_path.endsWith(".heb") || save_name_path.endsWith(".mobi") || save_name_path.endsWith(".fb2")
                                    || save_name_path.endsWith(".htm") || save_name_path.endsWith(".html") || save_name_path.endsWith(".php")
                                    || save_name_path.endsWith(".apk") || save_name_path.endsWith(".ofdx") || save_name_path.endsWith(".cebx")
                                    || save_name_path.endsWith(".ofd") || save_name_path.endsWith(".png") || save_name_path.endsWith(".gif") || save_name_path.endsWith(".jpg") || save_name_path.endsWith(".jpeg") || save_name_path.endsWith(".bmp")) {
                                new OpenFile(mContext).openFile(new File(save_name_path));

                            } else {
                                Toast.makeText(getActivity(), "不支持打开本文件", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;
                case 2:
                    Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private MessageLoaderHelper messageLoaderHelper;
    private MessageCryptoPresenter messageCryptoPresenter;
    private Long showProgressThreshold;
    private final String FILEPATH = "/storage/emulated/0/我的文档/附件管理/";
    /**
     * Used to temporarily store the destination folder for refile operations if a confirmation
     * dialog is shown.
     */
    private String mDstFolder;

    private MessageViewFragmentListener mFragmentListener;

    /**
     * {@code true} after {@link #onCreate(Bundle)} has been executed. This is used by
     * {@code MessageList.configureMenu()} to make sure the fragment has been initialized before
     * it is used.
     */
    private boolean mInitialized = false;

    private Context mContext;

    private AttachmentViewInfo currentAttachmentViewInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context.getApplicationContext();

        try {
            mFragmentListener = (MessageViewFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("This fragment must be attached to a MessageViewFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragments adds options to the action bar
        setHasOptionsMenu(true);
        Context context = getActivity().getApplicationContext();
//        setDatabase(context);
        mController = MessagingController.getInstance(context);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        messageCryptoPresenter = new MessageCryptoPresenter(messageCryptoMvpView);
        messageLoaderHelper = messageLoaderHelperFactory.createForMessageView(
                context, getLoaderManager(), getParentFragmentManager(), messageLoaderCallbacks);
        mInitialized = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        messageCryptoPresenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Activity activity = getActivity();
        boolean isChangingConfigurations = activity != null && activity.isChangingConfigurations();
        if (isChangingConfigurations) {
            messageLoaderHelper.onDestroyChangingConfigurations();
            return;
        }

        messageLoaderHelper.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int messageViewThemeResourceId = themeManager.getMessageViewThemeResourceId();
        Context context = new ContextThemeWrapper(inflater.getContext(), messageViewThemeResourceId);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.message, container, false);

        mMessageView = view.findViewById(R.id.message_view);
        mMessageView.setAttachmentCallback(this);
        mMessageView.setMessageCryptoPresenter(messageCryptoPresenter);

        mMessageView.setOnToggleFlagClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleFlagged();
            }
        });

        mMessageView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.reply) {
                    onReply();
                    return true;
                } else if (id == R.id.reply_all) {
                    onReplyAll();
                    return true;
                } else if (id == R.id.forward) {
                    onForward();
                    return true;
                } else if (id == R.id.forward_as_attachment) {
                    onForwardAsAttachment();
                    return true;
                } else if (id == R.id.share) {
                    onSendAlternate();
                    return true;
                }
                return false;
            }
        });

        mMessageView.setOnDownloadButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                    /**
                     *   下载完整邮件
                     */
                    DownloadEmail();
                } else {
                    Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    private void ShowDialog() {
        dialog1 = new DownloadDialog(getActivity());
        dialog1.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        messageReferenceString = arguments.getString(ARG_REFERENCE);
        MessageReference messageReference = MessageReference.parse(messageReferenceString);

        displayMessage(messageReference);
    }

    private void displayMessage(MessageReference messageReference) {
        mMessageReference = messageReference;
        Timber.d("MessageView displaying message %s", mMessageReference);

        mAccount = Preferences.getPreferences(getApplicationContext()).getAccount(mMessageReference.getAccountUuid());
        messageLoaderHelper.asyncStartOrResumeLoadingMessage(messageReference, null);

        mFragmentListener.updateMenu();
    }

    private void hideKeyboard() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null) {
            imm.hideSoftInputFromWindow(decorView.getApplicationWindowToken(), 0);
        }
    }

    private void showUnableToDecodeError() {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, R.string.message_view_toast_unable_to_display_message, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(MessageViewInfo messageViewInfo) {
        hideKeyboard();

        boolean handledByCryptoPresenter = messageCryptoPresenter.maybeHandleShowMessage(
                mMessageView, mAccount, messageViewInfo);
        if (!handledByCryptoPresenter) {
            mMessageView.showMessage(mAccount, messageViewInfo);
            if (mAccount.isOpenPgpProviderConfigured()) {
                mMessageView.getMessageHeaderView().setCryptoStatusDisabled();
            } else {
                mMessageView.getMessageHeaderView().hideCryptoStatus();
            }
        }

        if (messageViewInfo.subject != null) {
            displaySubject(messageViewInfo.subject);
        }
    }

    private void displayHeaderForLoadingMessage(LocalMessage message) {
        mMessageView.setHeaders(message, mAccount);
        if (mAccount.isOpenPgpProviderConfigured()) {
            mMessageView.getMessageHeaderView().setCryptoStatusLoading();
        }
        displaySubject(message.getSubject());
        mFragmentListener.updateMenu();
    }

    private void displaySubject(String subject) {
        if (TextUtils.isEmpty(subject)) {
            subject = mContext.getString(R.string.general_no_subject);
        }

        mMessageView.setSubject(subject);
    }

    /**
     * Called from UI thread when user select Delete
     */
    public void onDelete() {
        if (K9.isConfirmDelete() || (K9.isConfirmDeleteStarred() && mMessage.isSet(Flag.FLAGGED))) {
            showDialog(R.id.dialog_confirm_delete);
        } else {
            delete();
        }
    }

    public void onClose() {
        if (messageReferenceString != null) {
            mMessageList.onBackPressed();//销毁自己
        }
    }

    //TODO 创建文件夹
    public String filePath() {


        File file1 = new File(Fujian_path);
        if (!file1.exists()) {
            //创建文件夹
            boolean mkdirs = file1.mkdirs();
            if (mkdirs) {
                Log.i("tag", "bbbbbbbb11111222");
            }
            notifySystemToScan(mContext, file1);
        }


        return Fujian_path;
    }


    public static void notifyDirUpdate(Context context, File file) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://" + MediaStore.AUTHORITY + "/external/file");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.Files.FileColumns.PARENT, file.getParent());
        values.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Files.FileColumns.TITLE, file.getName());
        values.put("format", "12289");
        values.put("storage_id", "65537");
        try {
            resolver.insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onToggleAllHeadersView() {
        mMessageView.getMessageHeaderView().onShowAdditionalHeaders();
    }

    public boolean allHeadersVisible() {
        return mMessageView.getMessageHeaderView().additionalHeadersVisible();
    }

    private void delete() {
        if (mMessage != null) {
            // Disable the delete button after it's tapped (to try to prevent
            // accidental clicks)
            mFragmentListener.disableDeleteAction();
            LocalMessage messageToDelete = mMessage;
            mFragmentListener.showNextMessageOrReturn();
            mController.deleteMessage(mMessageReference, null);
        }
    }

    public void onRefile(String dstFolder) {
        if (!mController.isMoveCapable(mAccount)) {
            return;
        }
        if (!mController.isMoveCapable(mMessageReference)) {
            Toast toast = Toast.makeText(getActivity(), R.string.move_copy_cannot_copy_unsynced_message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (dstFolder == null) {
            return;
        }

        if (dstFolder.equals(mAccount.getSpamFolder()) && K9.isConfirmSpam()) {
            mDstFolder = dstFolder;
            showDialog(R.id.dialog_confirm_spam);
        } else {
            refileMessage(dstFolder);
        }
    }

    private void refileMessage(String dstFolder) {
        String srcFolder = mMessageReference.getFolderServerId();
        MessageReference messageToMove = mMessageReference;
        mFragmentListener.showNextMessageOrReturn();
        mController.moveMessage(mAccount, srcFolder, messageToMove, dstFolder);
    }

    public void onReply() {
        if (mMessage != null) {
            mFragmentListener.onReply(mMessage.makeMessageReference(), messageCryptoPresenter.getDecryptionResultForReply());
        }
    }

    public void onReplyAll() {
        if (mMessage != null) {
            mFragmentListener.onReplyAll(mMessage.makeMessageReference(), messageCryptoPresenter.getDecryptionResultForReply());
        }
    }

    public void onForward() {
        if (mMessage != null) {
            mFragmentListener.onForward(mMessage.makeMessageReference(), messageCryptoPresenter.getDecryptionResultForReply());
        }
    }

    public void onForwardAsAttachment() {
        if (mMessage != null) {
            mFragmentListener.onForwardAsAttachment(mMessage.makeMessageReference(), messageCryptoPresenter.getDecryptionResultForReply());
        }
    }

    public void onToggleFlagged() {
        if (mMessage != null) {
            boolean newState = !mMessage.isSet(Flag.FLAGGED);
            mController.setFlag(mAccount, mMessage.getFolder().getServerId(),
                    Collections.singletonList(mMessage), Flag.FLAGGED, newState);
            mMessageView.setHeaders(mMessage, mAccount);
        }
    }

    public void onMove() {
        if ((!mController.isMoveCapable(mAccount))
                || (mMessage == null)) {
            return;
        }
        if (!mController.isMoveCapable(mMessageReference)) {
            Toast toast = Toast.makeText(getActivity(), R.string.move_copy_cannot_copy_unsynced_message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        startRefileActivity(ACTIVITY_CHOOSE_FOLDER_MOVE);

    }

    public void onCopy() {
        if ((!mController.isCopyCapable(mAccount))
                || (mMessage == null)) {
            return;
        }
        if (!mController.isCopyCapable(mMessageReference)) {
            Toast toast = Toast.makeText(getActivity(), R.string.move_copy_cannot_copy_unsynced_message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        startRefileActivity(ACTIVITY_CHOOSE_FOLDER_COPY);
    }

    public void onArchive() {
        onRefile(mAccount.getArchiveFolder());
    }

    public void onSpam() {
        onRefile(mAccount.getSpamFolder());
    }

    private void startRefileActivity(int requestCode) {
        String accountUuid = mAccount.getUuid();
        String currentFolder = mMessageReference.getFolderServerId();
        String scrollToFolder = mAccount.getLastSelectedFolder();
        Intent intent = ChooseFolderActivity.buildLaunchIntent(requireActivity(), accountUuid, currentFolder,
                scrollToFolder, false, mMessageReference);

        startActivityForResult(intent, requestCode);
    }

    public void onPendingIntentResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode & REQUEST_MASK_LOADER_HELPER) == REQUEST_MASK_LOADER_HELPER) {
            requestCode ^= REQUEST_MASK_LOADER_HELPER;
            messageLoaderHelper.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if ((requestCode & REQUEST_MASK_CRYPTO_PRESENTER) == REQUEST_MASK_CRYPTO_PRESENTER) {
            requestCode ^= REQUEST_MASK_CRYPTO_PRESENTER;
            messageCryptoPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // Note: because fragments do not have a startIntentSenderForResult method, pending intent activities are
        // launched through the MessageList activity, and delivered back via onPendingIntentResult()
        //TODO 在这里保存的数据库
        switch (requestCode) {
            case REQUEST_CODE_CREATE_DOCUMENT: {
                Log.i("tag", "tag+22222222");
                if (data != null && data.getData() != null) {
                    String mimeType = currentAttachmentViewInfo.mimeType;
                    String displayName = currentAttachmentViewInfo.displayName;
                    String ReturnUri = data.getData().toString();
                    long size = currentAttachmentViewInfo.size;
                    long currentTimeMillis = System.currentTimeMillis();
                    String InternalUri = currentAttachmentViewInfo.internalUri.toString();
                    ContentValues values = new ContentValues();
                    values.put(FujianBeanDB.DISPLAYNAME, currentAttachmentViewInfo.displayName);
                    values.put(FujianBeanDB.RETURNURI, data.getData().toString());
                    values.put(FujianBeanDB.INTERNALURI, currentAttachmentViewInfo.internalUri.toString());
                    values.put(FujianBeanDB.FILE_SIZE, String.valueOf(currentAttachmentViewInfo.size));
                    values.put(FujianBeanDB.MIMETYPE, currentAttachmentViewInfo.mimeType);
                    values.put(FujianBeanDB.DATE, String.valueOf(currentTimeMillis));
                    MyApplication.getInstance().getDb().insert(FujianBeanDB.BIAO_NAME, null, values);
                    values.clear();
                    getAttachmentController(currentAttachmentViewInfo).saveAttachmentTo(data.getData());
                }
                break;
            }
            case ACTIVITY_CHOOSE_FOLDER_MOVE:
            case ACTIVITY_CHOOSE_FOLDER_COPY: {
                if (data == null) {
                    return;
                }

                String destFolder = data.getStringExtra(ChooseFolderActivity.RESULT_SELECTED_FOLDER);
                String messageReferenceString = data.getStringExtra(ChooseFolderActivity.RESULT_MESSAGE_REFERENCE);
                MessageReference ref = MessageReference.parse(messageReferenceString);
                if (mMessageReference.equals(ref)) {
                    mAccount.setLastSelectedFolder(destFolder);
                    switch (requestCode) {
                        case ACTIVITY_CHOOSE_FOLDER_MOVE: {
                            mFragmentListener.showNextMessageOrReturn();
                            moveMessage(ref, destFolder);
                            break;
                        }
                        case ACTIVITY_CHOOSE_FOLDER_COPY: {
                            copyMessage(ref, destFolder);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    public void onSendAlternate() {
        if (mMessage != null) {
            mController.sendAlternate(getActivity(), mAccount, mMessage);
        }
    }

    public void onToggleRead() {
        if (mMessage != null) {
            mController.setFlag(mAccount, mMessage.getFolder().getServerId(),
                    Collections.singletonList(mMessage), Flag.SEEN, !mMessage.isSet(Flag.SEEN));
            mMessageView.setHeaders(mMessage, mAccount);
            mFragmentListener.updateMenu();
        }
    }

    private void setProgress(boolean enable) {
        if (mFragmentListener != null) {
            mFragmentListener.setProgress(enable);
        }
    }

    public void moveMessage(MessageReference reference, String destFolderName) {
        mController.moveMessage(mAccount, mMessageReference.getFolderServerId(), reference, destFolderName);
    }

    public void copyMessage(MessageReference reference, String destFolderName) {
        mController.copyMessage(mAccount, mMessageReference.getFolderServerId(), reference, destFolderName);
    }

    private void showDialog(int dialogId) {
        DialogFragment fragment;
        if (dialogId == R.id.dialog_confirm_delete) {
            String title = getString(R.string.dialog_confirm_delete_title);
            String message = getString(R.string.dialog_confirm_delete_message);
            String confirmText = getString(R.string.dialog_confirm_delete_confirm_button);
            String cancelText = getString(R.string.dialog_confirm_delete_cancel_button);

            fragment = ConfirmationDialogFragment.newInstance(dialogId, title, message,
                    confirmText, cancelText);
        } else if (dialogId == R.id.dialog_confirm_spam) {
            String title = getString(R.string.dialog_confirm_spam_title);
            String message = getResources().getQuantityString(R.plurals.dialog_confirm_spam_message, 1);
            String confirmText = getString(R.string.dialog_confirm_spam_confirm_button);
            String cancelText = getString(R.string.dialog_confirm_spam_cancel_button);

            fragment = ConfirmationDialogFragment.newInstance(dialogId, title, message,
                    confirmText, cancelText);
        } else if (dialogId == R.id.dialog_attachment_progress) {
            String message = getString(R.string.dialog_attachment_progress_title);
            long size = currentAttachmentViewInfo.size;
            fragment = AttachmentDownloadDialogFragment.newInstance(size, message);
        } else {
            throw new RuntimeException("Called showDialog(int) with unknown dialog id.");
        }

        fragment.setTargetFragment(this, dialogId);
        fragment.show(getParentFragmentManager(), getDialogTag(dialogId));
    }

    private void removeDialog(int dialogId) {
        if (!isAdded()) {
            return;
        }

        FragmentManager fm = getParentFragmentManager();

        // Make sure the "show dialog" transaction has been processed when we call
        // findFragmentByTag() below. Otherwise the fragment won't be found and the dialog will
        // never be dismissed.
        fm.executePendingTransactions();

        DialogFragment fragment = (DialogFragment) fm.findFragmentByTag(getDialogTag(dialogId));

        if (fragment != null) {
            fragment.dismissAllowingStateLoss();
        }
    }

    private String getDialogTag(int dialogId) {
        return String.format(Locale.US, "dialog-%d", dialogId);
    }

    public void zoom(KeyEvent event) {
        // mMessageView.zoom(event);
    }

    @Override
    public void doPositiveClick(int dialogId) {
        if (dialogId == R.id.dialog_confirm_delete) {
            delete();
        } else if (dialogId == R.id.dialog_confirm_spam) {
            refileMessage(mDstFolder);
            mDstFolder = null;
        }
    }

    @Override
    public void doNegativeClick(int dialogId) {
        /* do nothing */
    }

    @Override
    public void dialogCancelled(int dialogId) {
        /* do nothing */
    }

    /**
     * Get the {@link MessageReference} of the currently displayed message.
     */
    public MessageReference getMessageReference() {
        return mMessageReference;
    }

    public boolean isMessageRead() {
        return (mMessage != null) && mMessage.isSet(Flag.SEEN);
    }

    public boolean isCopyCapable() {
        return mController.isCopyCapable(mAccount);
    }

    public boolean isMoveCapable() {
        return mController.isMoveCapable(mAccount);
    }

    public boolean canMessageBeArchived() {
        return (!mMessageReference.getFolderServerId().equals(mAccount.getArchiveFolder())
                && mAccount.hasArchiveFolder());
    }

    public boolean canMessageBeMovedToSpam() {
        return (!mMessageReference.getFolderServerId().equals(mAccount.getSpamFolder())
                && mAccount.hasSpamFolder());
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public void disableAttachmentButtons(AttachmentViewInfo attachment) {
        // mMessageView.disableAttachmentButtons(attachment);
    }

    public void enableAttachmentButtons(AttachmentViewInfo attachment) {
        // mMessageView.enableAttachmentButtons(attachment);
    }

    public void runOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void showAttachmentLoadingDialog() {
        // mMessageView.disableAttachmentButtons();
        showDialog(R.id.dialog_attachment_progress);
    }

    public void hideAttachmentLoadingDialogOnMainThread() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                removeDialog(R.id.dialog_attachment_progress);
                // mMessageView.enableAttachmentButtons();
            }
        });
    }

    public void refreshAttachmentThumbnail(AttachmentViewInfo attachment) {
        // mMessageView.refreshAttachmentThumbnail(attachment);
    }

    private MessageCryptoMvpView messageCryptoMvpView = new MessageCryptoMvpView() {
        @Override
        public void redisplayMessage() {
            messageLoaderHelper.asyncReloadMessage();
        }

        @Override
        public void startPendingIntentForCryptoPresenter(IntentSender si, Integer requestCode, Intent fillIntent,
                                                         int flagsMask, int flagValues, int extraFlags) throws SendIntentException {
            if (requestCode == null) {
                getActivity().startIntentSender(si, fillIntent, flagsMask, flagValues, extraFlags);
                return;
            }

            requestCode |= REQUEST_MASK_CRYPTO_PRESENTER;
            getActivity().startIntentSenderForResult(
                    si, requestCode, fillIntent, flagsMask, flagValues, extraFlags);
        }

        @Override
        public void showCryptoInfoDialog(MessageCryptoDisplayStatus displayStatus, boolean hasSecurityWarning) {
            CryptoInfoDialog dialog = CryptoInfoDialog.newInstance(displayStatus, hasSecurityWarning);
            dialog.setTargetFragment(MessageViewFragment.this, 0);
            dialog.show(getParentFragmentManager(), "crypto_info_dialog");
        }

        @Override
        public void restartMessageCryptoProcessing() {
            mMessageView.setToLoadingState();
            messageLoaderHelper.asyncRestartMessageCryptoProcessing();
        }

        @Override
        public void showCryptoConfigDialog() {
            AccountSettingsActivity.startCryptoSettings(getActivity(), mAccount.getUuid());
        }
    };

    @Override
    public void onClickShowSecurityWarning() {
        messageCryptoPresenter.onClickShowCryptoWarningDetails();
    }

    @Override
    public void onClickSearchKey() {
        messageCryptoPresenter.onClickSearchKey();
    }

    @Override
    public void onClickShowCryptoKey() {
        messageCryptoPresenter.onClickShowCryptoKey();
    }


    public interface MessageViewFragmentListener {
        void onForward(MessageReference messageReference, Parcelable decryptionResultForReply);

        void onForwardAsAttachment(MessageReference messageReference, Parcelable decryptionResultForReply);

        void disableDeleteAction();

        void onReplyAll(MessageReference messageReference, Parcelable decryptionResultForReply);

        void onReply(MessageReference messageReference, Parcelable decryptionResultForReply);

        void setProgress(boolean b);

        void showNextMessageOrReturn();

        void updateMenu();
    }

    public boolean isInitialized() {
        return mInitialized;
    }


    private MessageLoaderCallbacks messageLoaderCallbacks = new MessageLoaderCallbacks() {
        @Override
        public void onMessageDataLoadFinished(LocalMessage message) {
            mMessage = message;

            displayHeaderForLoadingMessage(message);
            mMessageView.setToLoadingState();
            showProgressThreshold = null;
        }

        @Override
        public void onMessageDataLoadFailed() {
            Toast.makeText(getActivity(), R.string.status_loading_error, Toast.LENGTH_LONG).show();
            showProgressThreshold = null;
        }

        /**
         * 下载全部邮件成功的时候
         * @param messageViewInfo
         */
        @Override
        public void onMessageViewInfoLoadFinished(MessageViewInfo messageViewInfo) {
            if (dialog1 != null) {
//                currentAttachmentViewInfo=messageViewInfo.attachments.get(0);
                dialog1.dismiss();
                Log.i("tag", "sssssssssssssssssss9999999999999999999999999");
                Message message = new Message();
                message.what = 1;
                handler1.sendMessage(message);


            }
            showMessage(messageViewInfo);
            showProgressThreshold = null;
            dialog1 = null;
        }

        /**
         * 下载全部邮件失败的时候
         * @param messageViewInfo
         */
        @Override
        public void onMessageViewInfoLoadFailed(MessageViewInfo messageViewInfo) {
            if (dialog1 != null) {
                dialog1.dismiss();
                Log.i("tag", "sssssssssssssssssss10101010101010101010101010");
                Message message = new Message();
                message.what = 2;
                handler1.sendMessage(message);
            }
            showMessage(messageViewInfo);
            showProgressThreshold = null;
            dialog1 = null;
        }

        @Override
        public void setLoadingProgress(int current, int max) {
            if (showProgressThreshold == null) {
                showProgressThreshold = SystemClock.elapsedRealtime() + PROGRESS_THRESHOLD_MILLIS;
            } else if (showProgressThreshold == 0L || SystemClock.elapsedRealtime() > showProgressThreshold) {
                showProgressThreshold = 0L;
                mMessageView.setLoadingProgress(current, max);
            }
        }

        @Override
        public void onDownloadErrorMessageNotFound() {
            mMessageView.enableDownloadButton();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.status_invalid_id_error, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDownloadErrorNetworkError() {
            mMessageView.enableDownloadButton();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.status_network_error, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void startIntentSenderForMessageLoaderHelper(IntentSender si, int requestCode, Intent fillIntent,
                                                            int flagsMask, int flagValues, int extraFlags) {
            showProgressThreshold = null;
            try {
                requestCode |= REQUEST_MASK_LOADER_HELPER;
                getActivity().startIntentSenderForResult(
                        si, requestCode, fillIntent, flagsMask, flagValues, extraFlags);
            } catch (SendIntentException e) {
                Timber.e(e, "Irrecoverable error calling PendingIntent!");
            }
        }
    };

    //TODO 打开附件
    @SuppressLint("CheckResult")
    @Override
    public void onViewAttachment(AttachmentViewInfo attachment) {
        currentAttachmentViewInfo = attachment;
//        getAttachmentController(attachment).viewAttachment();
        /**
         * save_name_path：拼凑的本地路径
         * 查看数据库中时候有该文件，有的话说明保存过
         * 没有的话，调用下载然后保存，最后打开
         */

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new RxPermissions(getActivity()).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {
                if (permission.granted) { //用户已经同意权限
                    String save_name_path = filePath() + File.separator + attachment.displayName;
                    Cursor cursor = MyApplication.getInstance().getDb().query(FujianBeanDB.BIAO_NAME, null, FujianBeanDB.RETURNURI + "=?", new String[]{save_name_path}, null, null, null, null);
                    int count = cursor.getCount();
                    cursor.close();
                    currentAttachmentViewInfo = attachment;
                    if (count > 0) {
                        if (save_name_path.endsWith(".doc") || save_name_path.endsWith(".docx") || save_name_path.endsWith(".ppt")
                                || save_name_path.endsWith(".pptx") || save_name_path.endsWith(".xls") || save_name_path.endsWith(".xlsx")
                                || save_name_path.endsWith(".txt") || save_name_path.endsWith(".htxt") || save_name_path.endsWith(".pdf")
                                || save_name_path.endsWith(".epub") || save_name_path.endsWith(".chm") || save_name_path.endsWith(".hveb")
                                || save_name_path.endsWith(".heb") || save_name_path.endsWith(".mobi") || save_name_path.endsWith(".fb2")
                                || save_name_path.endsWith(".htm") || save_name_path.endsWith(".html") || save_name_path.endsWith(".php")
                                || save_name_path.endsWith(".apk") || save_name_path.endsWith(".ofdx") || save_name_path.endsWith(".cebx")
                                || save_name_path.endsWith(".ofd") || save_name_path.endsWith(".png") || save_name_path.endsWith(".gif") || save_name_path.endsWith(".jpg") || save_name_path.endsWith(".jpeg") || save_name_path.endsWith(".bmp")) {
                            new OpenFile(mContext).openFile(new File(save_name_path));
                        } else {
                            Toast.makeText(getActivity(), "不支持打开本文件", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (attachment.displayName.endsWith(".apk")) {
                            if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                                /**
                                 *   下载完整邮件
                                 */
                                DownloadEmail();
                                Log.i("tag", "sssssssssssssssssss44444444444444444444444");
                            } else {
                                Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                            }
                        } else
//                if (attachment.size > FileSize)
                        {
                            if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                                /**
                                 *   下载完整邮件
                                 */
                                DownloadEmail();
                                Log.i("tag", "sssssssssssssssssss55555555555555555555555");
                            } else {
                                Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                            }
                        }

//            else {
//                if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                    ShowDialog();
//                    SaveDateBase(attachment.displayName, save_name_path);
//                    if (dialog1 != null) {
//                        dialog1.dismiss();
//                    }
//                    Log.i("tag", "sssssssssssssssssss6666666666666666666666");
//                    new OpenFile(mContext).openFile(new File(save_name_path));
//                } else {
//                    Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
//                }
//            }
                        is_Open_Save = 2;
//            if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                /**
//                 *   下载完整邮件
//                 */
//                DownloadEmail();
//
//            } else {
//                Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
//            }
//            if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                SaveDateBase(attachment.displayName, save_name_path);
//                new OpenFile(mContext).openFile(new File(save_name_path));
//            } else {
//                Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
//            }
                    }
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    Toast.makeText(getActivity(), "请问是否确定到本地设备中打开存储空间的权限", Toast.LENGTH_LONG).show();
                } else {
                    final CommonDialog dialog = new CommonDialog(getActivity());
                    dialog.setDialog_title("权限设置");
                    dialog.setSave_path1("");
                    dialog.setFile_size("请到设置中打开访问权限");
                    dialog.setFujian_names("");
                    dialog.setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            PermissionPageUtils permissionPageUtils = new PermissionPageUtils(getActivity());
                            permissionPageUtils.OpenPermissionPage();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        } else {
            String save_name_path = filePath() + File.separator + attachment.displayName;
            Cursor cursor = MyApplication.getInstance().getDb().query(FujianBeanDB.BIAO_NAME, null, FujianBeanDB.RETURNURI + "=?", new String[]{save_name_path}, null, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            if (count > 0) {
                if (save_name_path.endsWith(".doc") || save_name_path.endsWith(".docx") || save_name_path.endsWith(".ppt")
                        || save_name_path.endsWith(".pptx") || save_name_path.endsWith(".xls") || save_name_path.endsWith(".xlsx")
                        || save_name_path.endsWith(".txt") || save_name_path.endsWith(".htxt") || save_name_path.endsWith(".pdf")
                        || save_name_path.endsWith(".epub") || save_name_path.endsWith(".chm") || save_name_path.endsWith(".hveb")
                        || save_name_path.endsWith(".heb") || save_name_path.endsWith(".mobi") || save_name_path.endsWith(".fb2")
                        || save_name_path.endsWith(".htm") || save_name_path.endsWith(".html") || save_name_path.endsWith(".php")
                        || save_name_path.endsWith(".apk") || save_name_path.endsWith(".ofdx") || save_name_path.endsWith(".cebx")
                        || save_name_path.endsWith(".ofd") || save_name_path.endsWith(".png") || save_name_path.endsWith(".gif") || save_name_path.endsWith(".jpg") || save_name_path.endsWith(".jpeg") || save_name_path.endsWith(".bmp")) {
                    new OpenFile(mContext).openFile(new File(save_name_path));
                } else {
                    Toast.makeText(getActivity(), "不支持打开本文件", Toast.LENGTH_LONG).show();
                }
            } else {
                if (attachment.displayName.endsWith(".apk")) {
                    if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                        /**
                         *   下载完整邮件
                         */
                        DownloadEmail();
                        Log.i("tag", "sssssssssssssssssss44444444444444444444444");
                    } else {
                        Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                    }
                } else
//                if (attachment.size > FileSize)
                {
                    if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                        /**
                         *   下载完整邮件
                         */
                        DownloadEmail();
                        Log.i("tag", "sssssssssssssssssss55555555555555555555555");
                    } else {
                        Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                    }
                }

//            else {
//                if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                    ShowDialog();
//                    SaveDateBase(attachment.displayName, save_name_path);
//                    if (dialog1 != null) {
//                        dialog1.dismiss();
//                    }
//                    Log.i("tag", "sssssssssssssssssss6666666666666666666666");
//                    new OpenFile(mContext).openFile(new File(save_name_path));
//                } else {
//                    Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
//                }
//            }
                is_Open_Save = 2;
//            if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                /**
//                 *   下载完整邮件
//                 */
//                DownloadEmail();
//
//            } else {
//                Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
//            }
//            if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                SaveDateBase(attachment.displayName, save_name_path);
//                new OpenFile(mContext).openFile(new File(save_name_path));
//            } else {
//                Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
//            }
            }
        }
    }

    //TODO 保存附件
    @SuppressLint("CheckResult")
    @Override
    public void onSaveAttachment(final AttachmentViewInfo attachment) {

        currentAttachmentViewInfo = attachment;
//        Log.i("tag", "tag+111111111111");
//        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//        intent.setType(attachment.mimeType);
//        intent.putExtra(Intent.EXTRA_TITLE, attachment.displayName);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, REQUEST_CODE_CREATE_DOCUMENT);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new RxPermissions(getActivity()).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {
                if (permission.granted) { //用户已经同意权限
                    filePath();
                    String displayName = setFileReleaseNames(Fujian_path, attachment.displayName, attachment.displayName);
                    String save_name_path = filePath() + File.separator + displayName;
                    if (NetworkUtils.isNetWorkAvailable(mContext)) {
                        initDialog(attachment, displayName, save_name_path);
                    } else {
                        Toast.makeText(mContext, "请连接网络", Toast.LENGTH_SHORT).show();
                    }
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    Toast.makeText(getActivity(), "请问是否确定到本地设备中打开存储空间的权限", Toast.LENGTH_LONG).show();
                } else {
                    final CommonDialog dialog = new CommonDialog(getActivity());
                    dialog.setDialog_title("权限设置");
                    dialog.setSave_path1("");
                    dialog.setFile_size("请到设置中打开访问权限");
                    dialog.setFujian_names("");
                    dialog.setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            PermissionPageUtils permissionPageUtils = new PermissionPageUtils(getActivity());
                            permissionPageUtils.OpenPermissionPage();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        } else {
            filePath();
            String displayName = setFileReleaseNames(Fujian_path, attachment.displayName, attachment.displayName);
            String save_name_path = filePath() + File.separator + displayName;
            if (NetworkUtils.isNetWorkAvailable(mContext)) {
                initDialog(attachment, displayName, save_name_path);
            } else {
                Toast.makeText(mContext, "请连接网络", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //ToDO 下载完整邮件
    private void DownloadEmail() {
        mMessageView.disableDownloadButton();
        ShowDialog();
        messageLoaderHelper.downloadCompleteMessage();
    }


    public static void notifySystemToScan(Context context, File file) {
        if (file.isDirectory()) {
            /***
             *创建目录后刷新  新创建的目录可以在电脑同步显示
             * @param context
             * @param file
             */
            notifyDirUpdate(context, file);
        } else {
            /***
             *创建文件后刷新  新创建的目录可以在电脑同步显示
             * @param context
             * @param file
             */
            notifyFileUpdate(context, file);
        }
    }

    public static void notifyFileUpdate(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    private void SaveDateBase(String displayName, String save_name_path) {
        if (!save_name_path.isEmpty()) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.fsck.k9.ui.fileprovider", new File(save_name_path));
            notifySystemToScan(mContext, new File(save_name_path));
            getAttachmentController(currentAttachmentViewInfo).saveAttachmentTo(uri);
            ContentValues values = new ContentValues();
            values.put(FujianBeanDB.DISPLAYNAME, displayName);
            values.put(FujianBeanDB.RETURNURI, save_name_path);
            values.put(FujianBeanDB.INTERNALURI, currentAttachmentViewInfo.internalUri.toString());
            values.put(FujianBeanDB.FILE_SIZE, String.valueOf(currentAttachmentViewInfo.size));
            values.put(FujianBeanDB.MIMETYPE, currentAttachmentViewInfo.mimeType);
            values.put(FujianBeanDB.DATE, String.valueOf(System.currentTimeMillis()));
            MyApplication.getInstance().getDb().insert(FujianBeanDB.BIAO_NAME, null, values);
            values.clear();
        }
    }


    private AttachmentController getAttachmentController(AttachmentViewInfo attachment) {
        return new AttachmentController(mController, this, attachment);
    }


    private void initDialog(AttachmentViewInfo attachment, String displayName, String
            save_name_path) {
        final CommonDialog dialog = new CommonDialog(getActivity());
        dialog.setFujian_names(displayName);
        dialog.setDialog_title("保存文件");
        dialog.setSave_path1("保存位置:  附件管理");
        dialog.setFile_size("文件大小:  " + new SizeFormatter(mContext.getResources()).formatSize(attachment.size));
        dialog.setFujian_names("文件名称:  " + displayName);
        dialog.setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                currentAttachmentViewInfo = attachment;
                if (attachment.displayName.endsWith(".apk")) {
                    if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                        /**
                         *   下载完整邮件
                         */
                        DownloadEmail();
//                        SharedPreferences sp = getActivity().getSharedPreferences("fj", Context.MODE_PRIVATE);
//                        sp.edit().
                        Log.i("tag", "sssssssssssssssssss1111111111111111111");
                    } else {
                        Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                    }
                    /**
                     * 大于2.2Mb下载全部
                     */
                } else
//                    if (attachment.size > FileSize)
                {
                    if (NetworkUtils.isNetWorkAvailable(getActivity())) {
                        /**
                         *   下载完整邮件
                         */
                        DownloadEmail();
                        Log.i("tag", "sssssssssssssssssss222222222222222222");
                    } else {
                        Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
                    }
                }

//                else {
//                    if (NetworkUtils.isNetWorkAvailable(getActivity())) {
//                        ShowDialog();
//                        SaveDateBase(displayName, save_name_path);
//                        if (dialog1 != null) {
//                            dialog1.dismiss();
//                        }
//                        Log.i("tag", "sssssssssssssssssss33333333333333333333333333333");
//                    } else {
//                        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
//                    }
//                }

                dialog.dismiss();
                is_Open_Save = 1;
            }

            @Override
            public void onNegtiveClick() {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 传入文件默认名
     *
     * @return
     */
    private String fileReleaseName;//文件最终名字

    private String setFileReleaseNames(String fujianPath, String mFileName, String mFileName1) {
        File f = new File(fujianPath);
        if (f.exists()) { //判断路径是否存在
            File[] files = f.listFiles();
            HashSet<String> hashSet = new HashSet<>();
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    hashSet.add(name);
                }
            }
            int size = hashSet.size();
            if (size > 0) {
                int a = 1;
                while (true) {
                    if (a != 1) {
                        String[] split = mFileName1.split("\\.");
                        mFileName = split[0] + "(" + a + ")." + split[1];
                    }
                    if (!hashSet.contains(mFileName)) {
                        fileReleaseName = mFileName;
                        break;
                    } else {
                        a++;
                    }
                }
            } else {
                fileReleaseName = mFileName;
            }
        }
        return fileReleaseName;
    }
}
