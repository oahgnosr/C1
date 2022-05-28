package com.example.c1.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.c1.MainActivity;
import com.example.c1.R;

/**
 * https://developer.android.com/training/notify-user/channels?hl=zh-cn
 * https://developer.android.com/guide/components/foreground-services
 */
public class MyForegroundService extends Service {
    // 通知渠道 ID
    public static final String MY_CHANNEL_01 = "my_channel_01";
    public static final int ONGOING_NOTIFICATION_ID = 10;

    public MyForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Inside the service, usually in onStartCommand(),
        // you can request that your service run in the foreground.
        doStartForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void doStartForeground() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // If the notification supports a direct reply action, use
            // PendingIntent.FLAG_MUTABLE instead.
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE);

            // android.app.RemoteServiceException: Bad notification for startForeground
            createNotificationChannel();

            // The constructed Notification will be posted on this NotificationChannel.
            // To use a NotificationChannel,
            // it must first be created using NotificationManager.createNotificationChannel().
            Notification notification = new Notification.Builder(this, MY_CHANNEL_01)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_message))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .setTicker(getText(R.string.ticker_text))
                    .build();

            // This method takes two parameters:
            // id - A positive integer that uniquely identifies the notification in the status bar.
            //      Notification ID cannot be 0.
            // notification -  The Notification object itself.
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }
    }

    /**
     * 创建通知渠道：
     * 1. 构建一个具有唯一渠道 ID、用户可见名称和重要性级别的 NotificationChannel 对象。
     * 2.（可选）使用 setDescription() 指定用户在系统设置中看到的说明。
     * 3. 注册通知渠道，方法是将该渠道传递给 createNotificationChannel()。
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(MY_CHANNEL_01, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            // 创建渠道后，您将无法更改这些设置，而且对于是否启用相应行为，用户拥有最终控制权。
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}