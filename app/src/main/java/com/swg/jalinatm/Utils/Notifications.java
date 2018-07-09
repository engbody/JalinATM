package com.swg.jalinatm.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.swg.jalinatm.MainActivity;
import com.swg.jalinatm.R;

/**
 * Created by user on 7/6/2018.
 */

public class Notifications extends ContextWrapper {

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "com.swg.jalinatm";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    private Intent intent;
    private PendingIntent pendingIntent;
    private Uri soundUri;
    private AudioAttributes.Builder attrs;

    public Notifications(Context base) {
        super(base);
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        attrs = new AudioAttributes.Builder();
        attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        attrs.setUsage(AudioAttributes.USAGE_NOTIFICATION);
        createChannels();
    }

    public void createChannels() {
        // create android channel
        NotificationChannel androidChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.RED);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            androidChannel.setVibrationPattern(new long[] {0, 1000, 200,1000 });

            androidChannel.setSound(soundUri, attrs.build());

            getManager().createNotificationChannel(androidChannel);
        }
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getAndroidChannelNotification(String title, String body) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                    .setSmallIcon(R.drawable.atm_logo_blue)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        } else {
            return new NotificationCompat.Builder(getApplicationContext(), "default")
                    .setSmallIcon(R.drawable.atm_logo_blue)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }
    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
