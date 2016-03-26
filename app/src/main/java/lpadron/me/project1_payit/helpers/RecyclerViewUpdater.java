package lpadron.me.project1_payit.helpers;

import java.util.ArrayList;

import lpadron.me.project1_payit.models.CardReminder;

/**
 * Project1-Payit
 * lpadron.me.project1_payit.helpers
 * Created by Luis Padron on 3/17/16, at 7:01 PM
 */
public interface RecyclerViewUpdater {

    void onUpdateRecyclerViewNeeded(ArrayList<CardReminder> updatedReminders);
}
