package com.letmesleep.thenewera.willrenamelater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static java.util.Calendar.HOUR_OF_DAY;

public class MainActivity extends AppCompatActivity {

    //Variables Decalration
    static DevicePolicyManager devicePolicyManager;
    static ComponentName adminCallReceiverComponent;
    boolean isAdminActive;
    TextView startTimeTextView;
    TextView endTimeTextView;
    CheckBox toggleAdmin;
    static Calendar serviceStartTime;
    static Calendar serviceEndTime;
    static private int startHour =20, startMinute=00, endHour=06, endMinute=00;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isAppEnabled = false;
    Switch isAppEnabledToggle;
    LinearLayout disabledLayer;
    Intent intent;
    ImageView disabledImage;
    boolean alarmSetOrNot;
    //Variables Decalration End


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Action Bar Icon
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher_self_round);
        //Set Action Bar Icon End

        //Initializations
        startTimeTextView = (TextView) findViewById(R.id.startTime);
        endTimeTextView = (TextView) findViewById(R.id.endTime);
        disabledLayer = (LinearLayout) findViewById(R.id.disabledLayer);
        disabledImage = (ImageView) findViewById(R.id.disabledFP);
        sharedPreferences = getSharedPreferences("com.letmesleep.thenewera.willrenamelater",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        toggleAdmin = (CheckBox) findViewById(R.id.toggleAdmin);
        isAppEnabledToggle = (Switch) findViewById(R.id.toggleService);
        intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        ///DPM Setup
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminCallReceiverComponent = new ComponentName(this, adminCallReceiver.class);
        isAdminActive = isAdminActive();
        ///DPM Setup
        //Initializations End

        toggleAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked!=isAdminActive){
                    if(isChecked){
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminCallReceiverComponent);
                        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                        disabledLayer.setVisibility(View.GONE);
                    }
                    else
                    {
                        devicePolicyManager.removeActiveAdmin(adminCallReceiverComponent);
                        isAdminActive= false;
                        disabledLayer.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        alarmSetOrNot= sharedPreferences.getBoolean("alarmSetOrNot",false);
        Long startTimeFromSharedPreference = sharedPreferences.getLong("StartTime",-1);
        Long endTimeFromSharedPreference = sharedPreferences.getLong("EndTime",-1);
        boolean appEnabledFlag = sharedPreferences.getBoolean("isAppEnabled",false);
        if(startTimeFromSharedPreference != -1 && endTimeFromSharedPreference != -1){
            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(startTimeFromSharedPreference);
            startHour = temp.get(HOUR_OF_DAY);
            startMinute = temp.get(Calendar.MINUTE);
            temp.setTimeInMillis(endTimeFromSharedPreference);
            endHour = temp.get(HOUR_OF_DAY);
            endMinute = temp.get(Calendar.MINUTE);
        }
        if(appEnabledFlag){

        isAppEnabled = true;
        isAppEnabledToggle.setChecked(isAppEnabled);
            disabledImage.animate().alpha(1f).setDuration(1000);
           }

            setServiceSchedule();



    }
    public void  setAlarmForService(){
        Toast.makeText(this, "In Alarm", Toast.LENGTH_SHORT).show();
        intent.putExtra("finger",0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent pintentStart = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pintentStart);
        intent.putExtra("finger",1);
        PendingIntent pintentEnd = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pintentEnd);
        Log.i("time in millis",String.valueOf(serviceStartTime.getTime()));
        if(isAppEnabled && isAdminActive && !alarmSetOrNot){
            enableFingerPrint(this.endTimeTextView);
            Toast.makeText(this, "In Alarm: Start", Toast.LENGTH_SHORT).show();

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                serviceStartTime.getTimeInMillis(),
                pintentStart);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                serviceEndTime.getTimeInMillis(),
                pintentEnd);
            editor.putBoolean("alarmSetOrNot",true);
            editor.apply();
        }
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
            serviceStartTime.add(Calendar.DATE,-1);
        }
        String startTimedisplay = String.format("%02d",startHour) + ":" + String.format("%02d",startMinute);
        String endTimedisplay = String.format("%02d",endHour) + ":" + String.format("%02d",endMinute);
        startTimeTextView.setText(startTimedisplay);
        endTimeTextView.setText(endTimedisplay);
        Log.i("myapp Start time",String.valueOf(serviceStartTime.getTime()));
        Log.i("myapp Start time",String.valueOf(serviceEndTime.getTime()));
        setAlarmForService();
        Long sharedPreferencesSavedStartTime= serviceStartTime.getTimeInMillis();
        Long sharedPreferencesSavedEndTime= serviceEndTime.getTimeInMillis();
        editor.putLong("StartTime",sharedPreferencesSavedStartTime);
        editor.putLong("EndTime",sharedPreferencesSavedEndTime);
        editor.apply();
    }
    public void onResume() {
        super.onResume();
        isAdminActive = isAdminActive();
        toggleAdmin.setChecked(isAdminActive);
        if(isAdminActive){
            disabledLayer.setVisibility(View.GONE);}
        else {
            disabledLayer.setVisibility(View.VISIBLE);
        }
    }
    private boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(adminCallReceiverComponent);
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
                alarmSetOrNot = false;

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
isAppEnabled = !isAppEnabled;
if(isAppEnabled){
    setAlarmForService();
        editor.putBoolean("isAppEnabled",true);
        editor.apply();
    disabledImage.animate().alpha(1f).setDuration(1000);
    }else{
    enableFingerPrint(view);
    editor.putBoolean("isAppEnabled",false);
    editor.putBoolean("alarmSetOrNot",false);
    editor.apply();
    disabledImage.animate().alpha(0f).setDuration(1000);
}
        setServiceSchedule();
    }

    public void enableFingerPrint(View view) {
        devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE);
        Toast.makeText(this, "Enabled",
                Toast.LENGTH_SHORT).show();
    }
    public void disableFingerPrint(View view) {
        devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
        Toast.makeText(this, "Disabled",
                Toast.LENGTH_SHORT).show();
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
    public static class AlarmReceiver extends BroadcastReceiver
    {

        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;
        Intent intent;
        @Override
        public void onReceive(Context context, Intent intent) {
            sharedPreferences = context.getSharedPreferences("com.letmesleep.thenewera.willrenamelater",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Boolean alarmAlreadySet= sharedPreferences.getBoolean("alarmSetOrNot",false);
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminCallReceiverComponent = new ComponentName(context, adminCallReceiver.class);
            int whatToDo = intent.getIntExtra("finger",2);
            if(whatToDo == 0) {
                Toast.makeText(context, "Start Alarm Set",
                        Toast.LENGTH_SHORT).show();
                Log.i("Fingerprint Disabled", "Yes");
                devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent, DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
            }
            else if(whatToDo == 1){
                Log.i("Fingerprint Disabled", "No");
                devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent, DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE);
                setAlarmForService(context);
            }
            else {
                Log.i("Fingerprint Disabled", "Dont Know");


            }
        }
        public void  setAlarmForService(Context context){
            intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("finger",0);
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            PendingIntent pintentStart = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pintentStart);
            intent.putExtra("finger",1);
            PendingIntent pintentEnd = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pintentEnd);
           // Log.i("time in millis",String.valueOf(serviceStartTime.getTime()));
            Long startTimeFromSharedPreference = sharedPreferences.getLong("StartTime",-1) + 86400000 ;
            Long endTimeFromSharedPreference = sharedPreferences.getLong("EndTime",-1) + 86400000 ;
//            Calendar serviceStartime = Calendar.getInstance();
//            serviceStartime.setTimeInMillis(startTimeFromSharedPreference);
//            Calendar serviceEndTime = Calendar.getInstance();
//            serviceEndTime.setTimeInMillis(endTimeFromSharedPreference);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        startTimeFromSharedPreference,
                        pintentStart);

                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        endTimeFromSharedPreference,
                        pintentEnd);
            editor.putLong("StartTime",startTimeFromSharedPreference);
            editor.putLong("EndTime",endTimeFromSharedPreference);
                editor.apply();

        }
    }


}
