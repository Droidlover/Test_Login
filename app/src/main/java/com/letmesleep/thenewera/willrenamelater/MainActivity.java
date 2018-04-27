package com.letmesleep.thenewera.willrenamelater;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Variables Decalration
    DevicePolicyManager devicePolicyManager;
    ComponentName adminCallReceiverComponent;
    boolean isAdminActive;
    static TextView startTime;
    static TextView endTime;
    private int startHour, startMinute, endHour, endMinute;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    //Variables Decalration End


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Date Time Setup
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        //Date Time Setup End

        //Intent service = new Intent(getApplicationContext(), JokeService.class);
        //startService(new Intent(MainActivity.this, JokeService.class)); // Lancement du service
        //finish();`

        ///DPM Setup
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminCallReceiverComponent = new ComponentName(this, adminCallReceiver.class);
        isAdminActive = isAdminActive();

        ///DPM Setup

    }

    private boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(adminCallReceiverComponent);
    }

    public void lockScreen(View view) {
        devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
        devicePolicyManager.lockNow();
    }

    public void setStartTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "StartDate");
    }

    public void setEndTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
      newFragment.show(getSupportFragmentManager(), "EndDate");

    }

    public void toggleAdminAccess(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if(view.getId() == R.id.toggleAdmin){
            if (checked)
            {
                if(!isAdminActive) {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminCallReceiverComponent);
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                }
            }
            else
            {
                devicePolicyManager.removeActiveAdmin(adminCallReceiverComponent);
                isAdminActive= false;
            }
         if(isAdminActive()){
             ((CheckBox) view).setChecked(true);
         }   else{
             ((CheckBox) view).setChecked(false);
         }
        }
    }

    //Device Admin Receiver Class
    public static class adminCallReceiver extends DeviceAdminReceiver {
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


    }
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String displayTime = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
            if(getFragmentManager().findFragmentByTag("StartDate")!= null)
            {

                startTime.setText(displayTime);
            }
            else if(getFragmentManager().findFragmentByTag("EndDate")!= null)
            {

                endTime.setText(displayTime);
            }
        }


    }

}
