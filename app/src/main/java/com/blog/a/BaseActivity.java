package com.blog.a;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.os.Build.VERSION_CODES.M;

public class BaseActivity extends BaseDensityActivity {

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        // 地图SDK 权限
        if (!hasPermissions() && Build.VERSION.SDK_INT >= M) {
            requestPermissions();
        }
    }

    private boolean hasPermissions() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        int granted = pm.checkPermission(RECORD_AUDIO, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(M)
    private void requestPermissions() {
        final String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        boolean showRationale = false;
        for (String perm : permissions) {
            showRationale |= shouldShowRequestPermissionRationale(perm);
        }
        if (!showRationale) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("使用地图sdk")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int which) ->
                        ActivityCompat.requestPermissions(BaseActivity.this,
                                permissions, REQUEST_PERMISSIONS)
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
}
