package com.workforce.app.ec.servicies;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.workforce.app.ec.R;
import com.workforce.app.ec.config.AppPreferences;

import org.json.JSONObject;

import java.util.Random;




public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private AppPreferences app;
    private NotificationManager notifyMgr;
    public static final String ANDROID_CHANNEL_ID = "om.workforce.app.ec";
    public static final String ANDROID_CHANNEL_NAME = "Wego";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        //Log.d(TAG, "From: " + remoteMessage.getFrom());


        try {
            app = new AppPreferences(getApplicationContext());


            if(remoteMessage.getData()!=null) {

                JSONObject res;
                res = new JSONObject(remoteMessage.getData());

                String title =  res.getString("title");
                String body = res.getString("body");
                String msg = res.getString("msg");
                // String device = res.getString("device");

                String firebaseid= "";
                if(res.getString("firebaseId")!=null)
                {
                    firebaseid=res.getString("firebaseId");
                }

                notificacion (title, body);

            }


        } catch (Exception e) {
            e.printStackTrace();
            notificacion (remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }


        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


    }

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        try {
            app = new AppPreferences(getApplicationContext());
            app.setFirebasetoken(token);
            app.setFlag("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // [END on_new_token]

    private void notificacion (String title, String msg)
    {


        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Creación del builder
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setColor(getApplicationContext().getResources().getColor(R.color.colorAccent));




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel androidChannel = null;
            androidChannel = new android.app.NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            androidChannel.setDescription(ANDROID_CHANNEL_NAME);
            notifyMgr.createNotificationChannel(androidChannel);

            builder.setChannelId(ANDROID_CHANNEL_ID);
        }




        // Crear pila
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

        // Añadir actividad padre
        //stackBuilder.addParentStack(com.payments.administrador.payments.formularios.frm_ewallet_pendingtransactions.class);
        //stackBuilder.addParentStack( ca.paysocial.paysocial.activitys.frm_login.class);

        // Referenciar Intent para la notificación
        //stackBuilder.addNextIntent(vin_intent);

        // Obtener PendingIntent resultante de la pila
        //PendingIntent resultPendingIntent =
        //  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asignación del pending intent
        //builder.setContentIntent(resultPendingIntent);

        // Remover notificacion al interactuar con ella
        builder.setAutoCancel(true);

        // Construir la notificación y emitirla



        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if(Build.VERSION.SDK_INT > 21)
        {
            builder.setCategory(Notification.CATEGORY_EVENT);
        }


        int count = 0;
        count = new Random().nextInt(5000 - 5 + 1) + 5;


        //notifyMgr.notify(count, builder.build());


        final int finalCount = count;




        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do Work
                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();

                        builder.setFullScreenIntent(null,false);
                        notifyMgr.notify(finalCount,builder.build());

                    }
                }, 3000);

                Looper.loop();
            }
        };
        thread.start();
    }

}


