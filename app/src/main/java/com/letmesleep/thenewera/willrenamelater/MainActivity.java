package com.letmesleep.thenewera.willrenamelater;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static java.util.Calendar.HOUR_OF_DAY;

public class MainActivity extends AppCompatActivity {

    //Variables Decalration
    DevicePolicyManager devicePolicyManager;
    ComponentName adminCallReceiverComponent;
    boolean isAdminActive;
    static TextView startTime;
    static TextView endTime;
    CheckBox toggleAdmin;
    static Calendar serviceStartTime;
    static Calendar serviceEndTime;
    static private int startHour =20, startMinute=00, endHour=06, endMinute=00;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent service;
    //Variables Decalration End


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Date Time Setup
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        //Date Time Setup End
        sharedPreferences = getSharedPreferences("com.letmesleep.thenewera.willrenamelater",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        service = new Intent(getApplicationContext(), JokeService.class);
        startService(new Intent(MainActivity.this, JokeService.class)); // Lancement du service
        toggleAdmin = (CheckBox) findViewById(R.id.toggleAdmin);
        ///DPM Setup
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminCallReceiverComponent = new ComponentName(this, adminCallReceiver.class);
        isAdminActive = isAdminActive();
        devicePolicyManager.getKeyguardDisabledFeatures(null);
        devicePolicyManager.getKeyguardDisabledFeatures(adminCallReceiverComponent);
        devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE);
        devicePolicyManager.getKeyguardDisabledFeatures(null);
        devicePolicyManager.getKeyguardDisabledFeatures(adminCallReceiverComponent);
        ///DPM Setup
        toggleAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked!=isAdminActive){
                    if(isChecked==true){
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminCallReceiverComponent);
                         startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                    }
                    else
            {
                devicePolicyManager.removeActiveAdmin(adminCallReceiverComponent);
                isAdminActive= false;
            }
                }
            }
        }
        );

        //Alarm - Servivce Trigger Logic
//        this.runOnUiThread(new Runnable() {
//            public void run() {
//
//            }
//        });

        Long startTimeFromSharedPreference = sharedPreferences.getLong("Start Time",-1);
        Long endTimeFromSharedPreference = sharedPreferences.getLong("End Time",-1);
        if(startTimeFromSharedPreference != -1 && endTimeFromSharedPreference != -1){
            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(startTimeFromSharedPreference);
            startHour = temp.get(HOUR_OF_DAY);
            startMinute = temp.get(Calendar.MINUTE);
            temp.setTimeInMillis(endTimeFromSharedPreference);
            endHour = temp.get(HOUR_OF_DAY);
            endMinute = temp.get(Calendar.MINUTE);
        }
        setServiceSchedule();

//        startService(intent);
    }
    public void setAlarmForService(){
        Intent intent = new Intent(getApplicationContext(), JokeService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Log.i("time in millis",String.valueOf(serviceStartTime.getTime()));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                serviceStartTime.getTimeInMillis(), serviceStartTime.getTimeInMillis(),
                pintent);

        devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE);
//
//        Intent intent2 = new Intent(getApplicationContext(), JokeService.class);
//        PendingIntent pintent2 = PendingIntent.getBroadcast(MainActivity.this, JokeService.SERVICE_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager2 = (AlarmManager) getApplicationContext()
//                .getSystemService(Context.ALARM_SERVICE);
//        Log.i("time in millis",String.valueOf(serviceStartTime.getTime()));
//        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
//                serviceStartTime.getTimeInMillis(), serviceStartTime.getTimeInMillis(),
//                pintent);
    }
    public void setServiceSchedule(){
        serviceStartTime = Calendar.getInstance();
        serviceStartTime.set(HOUR_OF_DAY, startHour);
        serviceStartTime.set(Calendar.MINUTE, startMinute);
        serviceStartTime.set(Calendar.SECOND,00);
        serviceEndTime = Calendar.getInstance();
        serviceEndTime.set(HOUR_OF_DAY, endHour);
        serviceEndTime.set(Calendar.MINUTE, endMinute);
        serviceEndTime.set(Calendar.SECOND,00);
        if(startHour>endHour){
            serviceEndTime.add(Calendar.DATE,1);
        }
        String startTimedisplay = String.format("%02d",startHour) + ":" + String.format("%02d",startMinute);
        String endTimedisplay = String.format("%02d",endHour) + ":" + String.format("%02d",endMinute);
        startTime.setText(startTimedisplay);
        endTime.setText(endTimedisplay);
        setAlarmForService();
        Long startTimeFromSharedPreference = sharedPreferences.getLong("Start Time",-1);
        Long endTimeFromSharedPreference = sharedPreferences.getLong("End Time",-1);
        Long sharedPreferencesSavedStartTime= serviceStartTime.getTimeInMillis();
        Long sharedPreferencesSavedEndTime= serviceEndTime.getTimeInMillis();
        editor.putLong("Start Time",sharedPreferencesSavedStartTime);
        editor.putLong("End Time",sharedPreferencesSavedEndTime);
        editor.apply();

    }
    public void onResume() {
        super.onResume();
        isAdminActive = isAdminActive();
        toggleAdmin.setChecked(isAdminActive);


    }
    private boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(adminCallReceiverComponent);
    }

    public void lockScreen(View view) {
        devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
        devicePolicyManager.lockNow();
    }

    public void setStartTime(View view) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startHour = selectedHour;
                startMinute = selectedMinute;
                setServiceSchedule();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }

    public void setEndTime(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endHour = selectedHour;
                endMinute = selectedMinute;
                setServiceSchedule();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }

    public void appInServiceFlag(View view) {
        startService(service); // Lancement du service
    }
    public void appInServiceFlagTemp(View view) {
        stopService(service); // Lancement du service
    }

    //Device Admin Receiver Class
    public static class adminCallReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
                abortBroadcast();
            }
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
//    public static class TimePickerFragment
//            implements TimePickerDialog.OnTimeSetListener {
//
//       @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//            if(getFragmentManager().findFragmentByTag("StartDate")!= null)
//            {
//                startHour = hourOfDay;
//                startMinute = minute;
//               // startTime.setText(displayTime);
//                setServiceSchedule();
//            }
//            else if(getFragmentManager().findFragmentByTag("EndDate")!= null)
//            {
//                endHour = hourOfDay;
//                endMinute = minute;
//               // endTime.setText(displayTime);
//                setServiceSchedule();
//            }
//        }
//
//
//    }

}
