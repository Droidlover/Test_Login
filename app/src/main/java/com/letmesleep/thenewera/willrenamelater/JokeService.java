package com.letmesleep.thenewera.willrenamelater;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class JokeService extends Service {


    public JokeService() {// Log.v("SERVICE", "Service killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

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

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, JokeService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "345")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker("TICKER")
                .setContentIntent(pendingIntent).setShowWhen(true).setVisibility(0).setLights(0xffff0000, 100, 2000);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("345", "my app", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("my app");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(345, notification);

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

}