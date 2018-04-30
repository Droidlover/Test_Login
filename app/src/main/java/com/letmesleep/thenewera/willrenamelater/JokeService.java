package com.letmesleep.thenewera.willrenamelater;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

public class JokeService extends Service {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DevicePolicyManager devicePolicyManager;
    ComponentName adminCallReceiverComponent;
    static Calendar serviceStartTime;
    static Calendar serviceEndTime;
    public JokeService() {// Log.v("SERVICE", "Service killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
            sharedPreferences = getSharedPreferences("com.letmesleep.thenewera.willrenamelater",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            serviceStartTime = Calendar.getInstance();
            serviceEndTime= Calendar.getInstance();
            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            adminCallReceiverComponent = new ComponentName(this, MainActivity.adminCallReceiver.class);
        Log.i("is Admin",String.valueOf(devicePolicyManager.isAdminActive(adminCallReceiverComponent)));
        Long startTimeFromSharedPreference = sharedPreferences.getLong("Start Time",-1);
        Long endTimeFromSharedPreference = sharedPreferences.getLong("End Time",-1);
        serviceStartTime.setTimeInMillis(startTimeFromSharedPreference);
        serviceEndTime.setTimeInMillis(endTimeFromSharedPreference);
        Calendar currentTime = Calendar.getInstance();

        if(currentTime.after(serviceStartTime) && currentTime.before(serviceEndTime)){
            Log.i("perfect time","perfect");
            //devicePolicyManager.setKeyguardDisabledFeatures(adminCallReceiverComponent,DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
        }
        else{
            Log.i("perfect time","ended");

            stopSelf();
        }
        try {
            Log.v("SERVICE", "Service Started");
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            BroadcastReceiver mReceiver = new receiverScreen();

            registerReceiver(mReceiver, filter);

        } catch (Exception e) {
            Log.v("SERVICE", "Service Stack");
            e.printStackTrace();
        }
        PendingIntent pintent1 = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Log.i("time in millis",String.valueOf(serviceStartTime.getTime())+" service");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                serviceStartTime.getTimeInMillis(), serviceEndTime.getTimeInMillis(),
                pintent1);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Intent notificationIntent = new Intent(this, JokeService.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "345")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setTicker("TICKER")
//                .setContentIntent(pendingIntent).setShowWhen(true).setVisibility(0).setLights(0xffff0000, 100, 2000);
//        Notification notification = builder.build();
//        if (Build.VERSION.SDK_INT >= 26) {
//            NotificationChannel channel = new NotificationChannel("345", "my app", NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("my app");
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//        }
//        startForeground(345, notification);

    }

    //    @Override
//    public void onTaskRemoved(Intent rootIntent){
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent =  PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 3000,
//                restartServicePendingIntent);
//        Log.v("SERVICE", "Service Resarting...");
//        super.onTaskRemoved(rootIntent);
//    }
    @Override
    public void onDestroy() {
        Log.v("SERVICE", "Service Destroyed");

        super.onDestroy();
    }

    public class receiverScreen extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.v("SERVICE", "Screen off"); //Affichage de l'image de l'écran cassé
            }
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.v("SERVICE", "User action present"); //On supprime l'image et on détruit le service.
            }
        }

    }
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
}