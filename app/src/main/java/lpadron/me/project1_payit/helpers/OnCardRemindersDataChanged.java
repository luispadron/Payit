package lpadron.me.project1_payit.helpers;

import java.util.ArrayList;

import lpadron.me.project1_payit.models.CardReminder;

/**
 * Project1-Payit
 * lpadron.me.project1_payit.helpers
 * Created by Luis Padron on 3/20/16, at 3:39 PM
 */
public interface OnCardRemindersDataChanged {

    // Called whenever the card reminders data is changed
    // Usually when deleted or moved inside CardAdapter
    // Classes implementing this should reload the cardReminders array from
    // Shared Preferences
    void onUpdateCardRemindersArray(ArrayList<CardReminder> updatedReminders);
}
