package lpadron.me.project1_payit.controllers;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lpadron.me.project1_payit.R;
import lpadron.me.project1_payit.adapters.CardAdapter;
import lpadron.me.project1_payit.helpers.ItemTouchHelperCallback;
import lpadron.me.project1_payit.helpers.interfaces.RecyclerViewUpdater;
import lpadron.me.project1_payit.models.CardReminder;

public class MainFragment extends Fragment implements RecyclerViewUpdater {

    protected @Bind(R.id.card_recycler_view) RecyclerView cardRecylcerView;

    private ArrayList<CardReminder> cardReminders;
    private CardAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Bind the views
        ButterKnife.bind(this, view);

        setupAdapterAndList();

        return view;
    }

    private void setupAdapterAndList() {
        // Set adapter and recycler view
        // Retrieve CardReminders from SP
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String remindersJSON = sp.getString(MainActivity.CARD_REMINDERS, null);

        if (remindersJSON != null) {
            Type type = new TypeToken<List<CardReminder>>() {}.getType();
            cardReminders = gson.fromJson(remindersJSON, type);

            adapter = new CardAdapter(getActivity(), cardReminders);
            cardRecylcerView.setAdapter(adapter);
            cardRecylcerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            cardRecylcerView.setLayoutManager(layoutManager);

            // Set the RecyclerView for ItemTouchHelper
            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(cardRecylcerView);
        }
    }

    @Override
    public void onUpdateRecyclerViewNeeded(ArrayList<CardReminder> updatedReminders) {
        // Swap old list in adapter with new one.
        if (MainActivity.FIRST_TIME_RAN_NO_CARDS) {
            // This will only happen the first time the app is run
            setupAdapterAndList();
        } else {
            adapter.swap(updatedReminders);
            // Notify adapter that list of content has changed.
            adapter.notifyDataSetChanged();
        }
    }
}
