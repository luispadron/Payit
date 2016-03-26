package lpadron.me.project1_payit.controllers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lpadron.me.project1_payit.helpers.NotificationListener;
import lpadron.me.project1_payit.helpers.OnCardRemindersDataChanged;
import lpadron.me.project1_payit.helpers.OnNewCardAnimateOut;
import lpadron.me.project1_payit.helpers.OnPassCardReminder;
import lpadron.me.project1_payit.R;
import lpadron.me.project1_payit.helpers.RecyclerViewUpdater;
import lpadron.me.project1_payit.models.CardReminder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnPassCardReminder,
        OnNewCardAnimateOut, OnCardRemindersDataChanged  {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.mainFab) FloatingActionButton mainFab;
    @Bind(R.id.frame_layout_container) FrameLayout mainFrameLayout;

    private ArrayList<CardReminder> cardReminders = new ArrayList<>();
    private NewCardFragment newCardFragment;
    private MainFragment mainFragment;

    public static final String CARD_REMINDERS = "CARD_REMINDERS";
    public static final String CARD_REMINDER_PARCELABLE = "CARD_REMINDERS_PARCALABLE";
    private static final String MAIN_ACTIVITY_CONTEXT = "MAIN_ACTIVITY_CONTEXT";
    public static  boolean FIRST_TIME_RAN_NO_CARDS;
    private static final String FIRST_TIME_RAN_NO_CARDS_PREF = "FIRST_TIME_RAN_NO_CARDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the views
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Set the main fragment
        mainFragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_container, mainFragment);
        fragmentTransaction.commit();
        // Remove dim background color
        mainFrameLayout.getForeground().setAlpha(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCardReminders();
    }

    private void loadCardReminders() {
        // Retrieve CardReminders from SP
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String remindersJSON = sp.getString(CARD_REMINDERS, null);

        // Load if first time running from shared pref boolean
        FIRST_TIME_RAN_NO_CARDS = sp.getBoolean(FIRST_TIME_RAN_NO_CARDS_PREF, true);


        if (remindersJSON != null) {
            Type type = new TypeToken<List<CardReminder>>() {}.getType();
            cardReminders = gson.fromJson(remindersJSON, type);
            if (cardReminders.size() < 1) {
                // User has deleted all cards
                beginNewCardFragment();
            }
        } else {
            // First time app has been started
            beginNewCardFragment();
        }
    }

    private void beginNewCardFragment() {
        // Create transaction with animation
        newCardFragment = new NewCardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);
        fragmentTransaction.replace(R.id.card_fragment_container, newCardFragment);
        fragmentTransaction.commit();

        // Hide FAB
        mainFab.setVisibility(View.GONE);
        // Dim the background
        mainFrameLayout.getForeground().setAlpha(220);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_reminders) {
            // Show main view again
            MainFragment mainFragment = new MainFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_container, mainFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_about) {
            // Replace main fragment with about fragment
            AboutFragment aboutFragment = new AboutFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_container, aboutFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    
    @OnClick(R.id.mainFab)
    public void mainFabOnClick(View v) {
        beginNewCardFragment();
    }

    @Override
    public void onNewCardAnimatedOut() {
        // Card was animated out, restore the views
        mainFab.setVisibility(View.VISIBLE);
        mainFrameLayout.getForeground().setAlpha(0);
    }

    @Override
    public void onPassCardReminder(CardReminder cardReminder) {
        // Add the new reminder to the list
        cardReminders.add(0, cardReminder);
        // Return background color to normal
        mainFrameLayout.getForeground().setAlpha(0);
        // Reshow the FAB
        mainFab.setVisibility(View.VISIBLE);

        // Save CardReminder list into SP
        saveCardReminders();

        // Set the notification based on the due date
        //createNotificationForCard(cardReminder);

        // Notify change of data to recycler view inside MainFragment
        RecyclerViewUpdater recyclerViewUpdater = mainFragment;
        recyclerViewUpdater.onUpdateRecyclerViewNeeded(cardReminders);
        saveFirstTimeRanState();
    }

    private void createNotificationForCard(CardReminder cardReminder) {
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

        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        intent.putExtra(CARD_REMINDER_PARCELABLE, cardReminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

    }

    private void saveCardReminders() {
        // Save CardReminders list to SP using GSON
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spEditor = sp.edit();

        String remindersJSON = gson.toJson(cardReminders);
        spEditor.putString(CARD_REMINDERS, remindersJSON);

        spEditor.apply();
    }

    public void saveFirstTimeRanState() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spEditor = sp.edit();
        FIRST_TIME_RAN_NO_CARDS = false;
        spEditor.putBoolean(FIRST_TIME_RAN_NO_CARDS_PREF, FIRST_TIME_RAN_NO_CARDS);

        spEditor.apply();
    }

    @Override
    public void onUpdateCardRemindersArray(ArrayList<CardReminder> updatedReminders) {
        // load updated CardReminders
        cardReminders.clear();
        cardReminders.addAll(updatedReminders);
    }
}
