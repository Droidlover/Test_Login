package com.letmesleep.thenewera.willrenamelater;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class JokeService2 extends IntentService {


    JokeService2() {
        super("a");
        Log.v("SERVICE", "Service killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Log.v("SERVICE", "Service killed");
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            BroadcastReceiver mReceiver = new receiverScreen();

            registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            Log.v("SERVICE", "Service killed");
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        Log.v("SERVICE", "Service killed");

        // super.onDestroy();
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