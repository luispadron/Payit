package lpadron.me.project1_payit.helpers.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lpadron.me.project1_payit.controllers.MainActivity;
import lpadron.me.project1_payit.models.CardReminder;

/**
 * Project1-Payit
 * lpadron.me.project1_payit.controllers
 * Created by Luis Padron on 3/20/16, at 8:48 PM
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private CardReminder cardReminder;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve passed in reminder
        cardReminder = intent.getParcelableExtra(MainActivity.CARD_REMINDER_PARCELABLE);
        // Send that reminder to alarm service
        Intent startIntent = new Intent(context, AlarmService.class);
        startIntent.putExtra(MainActivity.CARD_REMINDER_PARCELABLE, cardReminder);
        context.startService(startIntent);
    }


}
