package lpadron.me.project1_payit.controllers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import lpadron.me.project1_payit.R;
import lpadron.me.project1_payit.helpers.OnNewCardAnimateOut;
import lpadron.me.project1_payit.helpers.OnPassCardReminder;
import lpadron.me.project1_payit.models.CardReminder;
import lpadron.me.project1_payit.helpers.OnSwipeTouchListener;

import static android.widget.AdapterView.OnItemSelectedListener;

public class NewCardFragment extends Fragment
        implements OnItemSelectedListener, OnTouchListener {

    @Bind(R.id.new_card_icon) ImageView icon;
    @Bind(R.id.new_card_title) TextView title;
    @Bind(R.id.card_name) EditText cardNameEditText;
    @Bind(R.id.spinner_prompt) TextView spinnerPrompt;
    @Bind(R.id.due_day_spinner) Spinner daySpinner;
    @Bind(R.id.card_amount) EditText cardAmountEditText;
    @Bind(R.id.months_to_pay_off_edit_text) EditText monthsToPayEdit;
    @Bind(R.id.card_interest) EditText cardInterestEditText;
    @Bind(R.id.spinner_prompt_company) TextView spinnerPromptBank;
    @Bind(R.id.card_company_spinner) Spinner cardCompanySpinner;
    @Bind(R.id.final_confirm_card_layout) RelativeLayout finalCardLayout;

    private ImageButton confirmButton;

    private String cardName;
    private int dueDay = 0;
    private Double amntOnCard;
    private int monthsToPayOff;
    private String cardBank;
    private Double interestRate;

    private static final int ANIMATE_RIGHT = 1;
    private static final int ANIMATE_LEFT = 2;
    private static final int ANIMATE_MODAL_OUT = 3;
    private static final int ANIMATE_MODAL_OUT_WITH_DATA = 4;

    private static boolean CURRENTLY_ANIMATING_OUT = false;
    private static boolean FRAGMENT_WILL_BE_REMOVED = false;
    private final static String LAST_ID_USED = "LAST_ID_USED";


    public NewCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_card, container, false);

        // Set back to false, since currently, fragment will not be removed
        FRAGMENT_WILL_BE_REMOVED = false;

        getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Bind the views
        ButterKnife.bind(this, view);

        // Set items for day spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.card_days, android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        daySpinner.setOnItemSelectedListener(this);

        // Set items for company spinner
        ArrayAdapter<CharSequence> companyAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.card_companies, android.R.layout.simple_spinner_dropdown_item);
        cardCompanySpinner.setAdapter(companyAdapter);
        cardCompanySpinner.setOnItemSelectedListener(this);

        // Get the parent FrameLayout to setup the confirm button from it
        View frameLayout = getActivity().findViewById(R.id.card_fragment_container);
        confirmButton = (ImageButton) frameLayout.findViewById(R.id.final_confirm_card_button);


        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.card_recycler_view);
        recyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // If clicked outside the fragment
                // On release of touch, remove this fragment
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!CURRENTLY_ANIMATING_OUT && !FRAGMENT_WILL_BE_REMOVED) {
                        animateOut(ANIMATE_MODAL_OUT);
                        return true;
                    }
                }
                return false;
            }
        });

        // Set the swipe listener for entire view
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity().getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                // Animate out to the left
                animateOut(ANIMATE_LEFT);
            }

            @Override
            public void onSwipeRight() {
                // Animate out to the right
                animateOut(ANIMATE_RIGHT);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0) {

        } else {
            switch (parent.getId()) {
                case R.id.due_day_spinner:
                    // Save selected day
                    dueDay = position;
                    // Hide the views, and display the next ones
                    spinnerPrompt.setVisibility(View.GONE);
                    daySpinner.setVisibility(View.GONE);

                    cardAmountEditText.setVisibility(View.VISIBLE);
                    break;
                case R.id.card_company_spinner:
                    // Save selected bank
                    cardBank = (String) parent.getItemAtPosition(position);

                    // Hide this entire view
                    title.setVisibility(View.GONE);
                    icon.setVisibility(View.GONE);
                    spinnerPromptBank.setVisibility(View.GONE);

                    // Display final card layout
                    setupFinalConfirmCardView();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private void setupFinalConfirmCardView() {
        finalCardLayout.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.VISIBLE);

        final TextView finalCardName = (TextView) finalCardLayout.findViewById(R.id.final_card_name);
        finalCardName.setText("Card name: " + cardName);

        final TextView finalDueDay = (TextView) finalCardLayout.findViewById(R.id.final_due_day);
        finalDueDay.setText("Due day: " + dueDay);

        final TextView finalAmntOnCard = (TextView) finalCardLayout.findViewById(R.id.final_amnt_on_card);
        finalAmntOnCard.setText("Amount on card: $" + amntOnCard);

        final TextView finalInterestRate = (TextView) finalCardLayout.findViewById(R.id.final_interest_rate);
        finalInterestRate.setText("Interest rate: " + interestRate + "%");

        final TextView finalBank = (TextView) finalCardLayout.findViewById(R.id.final_issuing_bank);
        finalBank.setText("Issuing bank: " + cardBank);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate out WITH data
                confirmButton.setVisibility(View.GONE);
                animateOut(ANIMATE_MODAL_OUT_WITH_DATA);
            }
        });
    }

    @OnEditorAction(R.id.card_name)
    boolean onEditorActionName(KeyEvent key, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                key.getAction() == KeyEvent.ACTION_DOWN &&
                        key.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // User done typing the card name, hide view, prepare to show next view
            cardNameEditText.setVisibility(View.GONE);

            // Set card name
            cardName = cardNameEditText.getText().toString();

            // Hide the keyboard
            hideKeyboard();

            // Make other view visible
            spinnerPrompt.setVisibility(View.VISIBLE);
            daySpinner.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @OnEditorAction(R.id.card_amount)
    boolean onEditorActionCardAmount(KeyEvent key, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                key.getAction() == KeyEvent.ACTION_DOWN &&
                        key.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // Set the card amount rate
            amntOnCard = Double.parseDouble(cardAmountEditText.getText().toString());

            // Hide the keyboard and current view
            hideKeyboard();
            cardAmountEditText.setVisibility(View.GONE);

            // Make next view visible
            monthsToPayEdit.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @OnEditorAction(R.id.months_to_pay_off_edit_text)
    boolean onEditorActionMonthsToPay(KeyEvent keyEvent, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // Set the months to pay off amount
            monthsToPayOff = Integer.parseInt(monthsToPayEdit.getText().toString());

            // Hide the keyboard and current view
            hideKeyboard();
            monthsToPayEdit.setVisibility(View.GONE);

            // Make next view visible
            cardInterestEditText.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @OnEditorAction(R.id.card_interest)
    boolean onEditorActionInterest(KeyEvent key, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                key.getAction() == KeyEvent.ACTION_DOWN &&
                        key.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // Set interest rate
            interestRate = Double.parseDouble(cardInterestEditText.getText().toString());

            // Hide the keyboard and current view
            hideKeyboard();
            cardInterestEditText.setVisibility(View.GONE);

            // Make next view visible
            spinnerPromptBank.setVisibility(View.VISIBLE);
            cardCompanySpinner.setVisibility(View.VISIBLE);
        }
        return true;
    }


    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void animateOut(final int animateDirection) {

        int animResourceID = -1;

        if (animateDirection == ANIMATE_LEFT) {
            animResourceID = R.anim.slide_out_left;
        } else if (animateDirection == ANIMATE_RIGHT) {
            animResourceID = R.anim.slide_out_right;
        } else if (animateDirection == ANIMATE_MODAL_OUT || animateDirection == ANIMATE_MODAL_OUT_WITH_DATA) {
            animResourceID = R.anim.modal_out;
        }

        Animation animation = AnimationUtils.loadAnimation(getContext(), animResourceID);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Currently animating
                CURRENTLY_ANIMATING_OUT = true;

                // Hide confirm button from view if visible
                if (confirmButton.isShown()) {
                    confirmButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation done, set fields accordingly
                CURRENTLY_ANIMATING_OUT = false;
                // Animation done, fragment will be removed
                FRAGMENT_WILL_BE_REMOVED = true;

                // Create new card reminder and pass it to MainActivity
                if (animateDirection == ANIMATE_MODAL_OUT_WITH_DATA) {
                    int notificationID = 0;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    if (!MainActivity.FIRST_TIME_RAN_NO_CARDS) {
                        // Retrieve last ID used
                        notificationID = preferences.getInt(LAST_ID_USED, -1);
                        notificationID = notificationID + 1;
                    }

                    // Write the last ID used
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(LAST_ID_USED, notificationID);
                    editor.apply();

                    CardReminder reminder = new CardReminder(cardName, dueDay, amntOnCard,
                            monthsToPayOff, interestRate, cardBank, notificationID);
                    OnPassCardReminder reminderPasser = (OnPassCardReminder) getActivity();
                    reminderPasser.onPassCardReminder(reminder);
                }

                // Remove fragment with animation
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.remove(getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.card_fragment_container)).commit();

                // Notify main activity, card was swiped away
                OnNewCardAnimateOut newCardSwipeAway = (OnNewCardAnimateOut) getActivity();
                newCardSwipeAway.onNewCardAnimatedOut();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
        getView().startAnimation(animation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(getActivity().getApplicationContext(), "Touched", Toast.LENGTH_SHORT).show();
        return false;
    }
}
