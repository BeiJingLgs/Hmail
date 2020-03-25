package com.fsck.k9.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MotionEvent;

import android.view.View;

import com.fsck.k9.ui.R;
import com.fsck.k9.ui.ThemeManager;
import com.fsck.k9.ui.permissions.PermissionRationaleDialogFragment;


import timber.log.Timber;


public abstract class K9Activity extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS  = 1;
    public static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;
    public static final int PERMISSIONS_MOUNT_UNMOUNT_FILESYSTEMS = 3;
    public static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 4;
    public static final int PERMISSIONS_INTERNET = 5;
    private static final String FRAGMENT_TAG_RATIONALE = "rationale";


    private final K9ActivityCommon base = new K9ActivityCommon(this, ThemeType.DEFAULT);

    public ThemeManager getThemeManager() {
        return base.getThemeManager();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        base.preOnCreate();
        super.onCreate(savedInstanceState);

    }



    @Override
    protected void onResume() {
        base.preOnResume();
        super.onResume();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        base.preDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    protected void setLayout(@LayoutRes int layoutResId) {
        setContentView(layoutResId);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new IllegalArgumentException("K9 layouts must provide a toolbar with id='toolbar'.");
        }
        setSupportActionBar(toolbar);
    }

    protected void setLayout(View view) {
        setContentView(view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new IllegalArgumentException("K9 layouts must provide a toolbar with id='toolbar'.");
        }
        setSupportActionBar(toolbar);
    }



    public boolean hasPermission(Permission permission) {
        return ContextCompat.checkSelfPermission(this, permission.permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionOrShowRationale(Permission permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permission)) {
            PermissionRationaleDialogFragment dialogFragment =
                    PermissionRationaleDialogFragment.newInstance(permission);
            Log.i("tag","bbbbbbbbbbb11"+permission.permission);
            dialogFragment.show(getSupportFragmentManager(), FRAGMENT_TAG_RATIONALE);
        } else {
            requestPermission(permission);
            Log.i("tag","bbbbbbbbbbb22"+permission.permission);
        }
    }

    public void requestPermission(Permission permission) {
        Timber.i("Requesting permission: " + permission.permission);
        ActivityCompat.requestPermissions(this, new String[] { permission.permission }, permission.requestCode);
    }

    public enum Permission {
        READ_CONTACTS(
                Manifest.permission.READ_CONTACTS,
                PERMISSIONS_REQUEST_READ_CONTACTS,
                R.string.permission_contacts_rationale_title,
                R.string.permission_contacts_rationale_message
        ),
        WRITE_CONTACTS(
                Manifest.permission.WRITE_CONTACTS,
                PERMISSIONS_REQUEST_WRITE_CONTACTS,
                R.string.permission_contacts_rationale_title,
                R.string.permission_contacts_rationale_message
        ),
        MOUNT_UNMOUNT_FILESYSTEMS(
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                PERMISSIONS_MOUNT_UNMOUNT_FILESYSTEMS,
                R.string.permission_contacts_rationale_title,
                R.string.permission_contacts_rationale_message
        ),
        WRITE_EXTERNAL_STORAGE(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PERMISSIONS_WRITE_EXTERNAL_STORAGE,
                R.string.permission_contacts_rationale_title,
                R.string.permission_contacts_rationale_message
        ),
        INTERNET(
                Manifest.permission.INTERNET,
                PERMISSIONS_INTERNET,
                R.string.permission_contacts_rationale_title,
                R.string.permission_contacts_rationale_message
        );


        public final String permission;
        public final int requestCode;
        public final int rationaleTitle;
        public final int rationaleMessage;

        Permission(String permission, int requestCode, @StringRes int rationaleTitle, @StringRes int rationaleMessage) {
            this.permission = permission;
            this.requestCode = requestCode;
            this.rationaleTitle = rationaleTitle;
            this.rationaleMessage = rationaleMessage;
        }
    }
}
