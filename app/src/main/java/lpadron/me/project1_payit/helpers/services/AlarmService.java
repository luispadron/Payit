package lpadron.me.project1_payit.helpers.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.text.NumberFormat;
import java.util.Locale;

import lpadron.me.project1_payit.R;
import lpadron.me.project1_payit.controllers.MainActivity;
import lpadron.me.project1_payit.models.CardReminder;
import lpadron.me.project1_payit.helpers.NotificationCreator;

/**
 * Project1-Payit
 * lpadron.me.project1_payit.helpers
 * Created by Luis Padron on 3/20/16, at 8:50 PM
 */

public class AlarmService extends Service {

    private CardReminder cardReminder;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cardReminder = intent.getParcelableExtra(MainActivity.CARD_REMINDER_PARCELABLE);
        displayNotification();
        return Service.START_NOT_STICKY;
    }

    private void displayNotification() {
        Intent resultIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setGroupingUsed(true);
        String contentText = String.format("Recommended payment amount $%s", formatter.format(cardReminder.amountToPay()));
        // Create the notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bank_cards_100)
                .setContentTitle("Payment due for: " + cardReminder.getCardName())
                .setContentText(contentText)
                .setTicker("Cool!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setCategory("Notification");

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        int notificationId = cardReminder.getNotificationID();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, builder.build());

        // After displaying notification, create a new notification
        NotificationCreator.createNotification(this, cardReminder);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
