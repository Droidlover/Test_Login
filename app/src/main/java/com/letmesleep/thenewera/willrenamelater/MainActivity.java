package com.letmesleep.thenewera.willrenamelater;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;
    boolean mAdminActive;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent service = new Intent(getApplicationContext(), JokeService.class);
        //startService(new Intent(MainActivity.this, JokeService.class)); // Lancement du service
        //finish();`
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, DeviceAdminSampleReceiver.class);
        mAdminActive = isActiveAdmin();
        //MainActivity mActivity= (MainActivity) getCallingActivity();
        Toast.makeText(this, String.valueOf(mAdminActive), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
//        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                mActivity.getString(R.string.add_admin_extra_app_text));
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        // return false - don't update checkbox until we're really active


    }

    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    public void lockScreen(View view) {
        Toast.makeText(this, "locked", Toast.LENGTH_SHORT).show();
        mDPM.lockNow();
    }

    //Device Admin Receiver Class
    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) { abortBroadcast();Toast.makeText(context, "abortee", Toast.LENGTH_SHORT).show();
                abortBroadcast();
            } abortBroadcast();Toast.makeText(context, " no abortee", Toast.LENGTH_SHORT).show();
            super.onReceive(context, intent);
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_disabled));
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_failed));
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
        }


    }
}
