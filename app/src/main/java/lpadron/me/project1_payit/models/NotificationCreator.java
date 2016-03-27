package lpadron.me.project1_payit.models;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import lpadron.me.project1_payit.controllers.MainActivity;
import lpadron.me.project1_payit.helpers.services.AlarmBroadcastReceiver;

/**
 * Payit
 * lpadron.me.project1_payit.models
 * Created by Luis Padron on 3/26/16, at 10:50 PM
 */
public class NotificationCreator {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void createNotification(Context context, CardReminder cardReminder) {
        Calendar calendar = Calendar.getInstance();

        //Split string
        String[] split = cardReminder.getFullDateFromDayFormattedForNotification().split("/");

        // Set the date for the notification
        calendar.set(Calendar.MONTH, Integer.parseInt(split[0]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[1]));
        calendar.set(Calendar.YEAR, Integer.parseInt(split[2]));
        calendar.set(Calendar.HOUR_OF_DAY, CardReminder.HOUR_FOR_NOTIFICATION);
        calendar.set(Calendar.MINUTE, CardReminder.MINUTE_FOR_NOTIFICATION);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(MainActivity.CARD_REMINDER_PARCELABLE, cardReminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(MainActivity.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

}
