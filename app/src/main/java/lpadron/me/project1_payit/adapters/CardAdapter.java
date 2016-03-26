package lpadron.me.project1_payit.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.jar.Manifest;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lpadron.me.project1_payit.R;
import lpadron.me.project1_payit.controllers.MainActivity;
import lpadron.me.project1_payit.helpers.ItemTouchHelperAdapter;
import lpadron.me.project1_payit.helpers.OnCardRemindersDataChanged;
import lpadron.me.project1_payit.models.CardReminder;

/**
 * Project1-Payit
 * lpadron.me.project1_payit.adapters
 * Created by Luis Padron on 3/6/16, at 8:54 PM
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>
        implements ItemTouchHelperAdapter{

    private Context context;
    private ArrayList<CardReminder> cardReminders;

    public CardAdapter(Context context, ArrayList<CardReminder> cardReminders) {
        this.context = context;
        this.cardReminders = cardReminders;
    }

    /* Implemented methods from ItemTouchHelperAdapter */

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(cardReminders, i, i + 1);
            }
        } else {
            for (int i= fromPosition; i > toPosition; i--) {
                Collections.swap(cardReminders, i , i - 1);
            }
        }
        saveRemindersState();
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        // Confirm deletion with dialog
        final SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete this card?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        // User said yes to delete
                        // Remove reminder
                        cardReminders.remove(position);
                        notifyItemRemoved(position);
                        notifyItemChanged(position);

                        // Save the state of the card reminders array
                        saveRemindersState();
                        sweetAlertDialog.setTitleText("Deleted");
                        sweetAlertDialog.setContentText("Card deleted");
                        sweetAlertDialog.setConfirmText("OK");
                        sweetAlertDialog.showCancelButton(false);
                        sweetAlertDialog.setConfirmClickListener(null);
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        // Canceled, return view to normal position
                        notifyItemChanged(position);
                        sweetAlertDialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void saveRemindersState() {
        // Save CardReminders list to SP using GSON
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEditor = sp.edit();

        String remindersJSON = gson.toJson(cardReminders);
        spEditor.putString(MainActivity.CARD_REMINDERS, remindersJSON);

        spEditor.apply();

        // Notify that card reminders data has changed
        OnCardRemindersDataChanged onCardRemindersDataChanged = (OnCardRemindersDataChanged) context;
        onCardRemindersDataChanged.onUpdateCardRemindersArray(cardReminders);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardBanner;
        public TextView cardName;
        public TextView dueDate;
        public TextView amntOnCard;
        public TextView monthsToPay;
        public TextView amntToPay;
        public TextView cardBannerBankName;


        public CardViewHolder(View itemView) {
            super(itemView);

            // Instantiate the views
            cardBanner = (ImageView) itemView.findViewById(R.id.display_card_banner);
            cardBannerBankName = (TextView) itemView.findViewById(R.id.display_card_banner_bank);
            cardName = (TextView) itemView.findViewById(R.id.display_card_name);
            dueDate = (TextView) itemView.findViewById(R.id.display_due_date);
            monthsToPay = (TextView) itemView.findViewById(R.id.display_months_to_pay);
            amntOnCard = (TextView) itemView.findViewById(R.id.display_amnt_on_card);
            amntToPay = (TextView) itemView.findViewById(R.id.display_amount_to_pay);
        }

        public void setData(CardReminder reminder) {
            cardBannerBankName.setText(reminder.getIssuingBank());
            cardName.setText(reminder.getCardName());
            dueDate.setText("Due on: " + reminder.getFullDateFromDayAsString());
            monthsToPay.setText("Months left to pay: " + reminder.getMonthsToPayOff());
            // Format the number to include commas
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setGroupingUsed(true);

            amntOnCard.setText(String.format(Locale.US, "Amount on card: $%s", formatter.format(reminder.getAmntOnCard())));
            if (reminder.getMonthsToPayOff() == 1) {
                amntToPay.setText("Pay it all off this month!");
            } else {
                amntToPay.setText(String.format(Locale.US, "Pay $%.2f this month!", reminder.amountToPay()));
            }
            // Set the colors for the views, from randomly generated colors
            cardBanner.setBackgroundColor(ContextCompat.getColor(context, reminder.getGeneratedColor()));
            amntToPay.setBackgroundColor(ContextCompat.getColor(context, reminder.getGeneratedColor()));
        }
    }

    // Handle array swapping/updating
    public void swap(ArrayList<CardReminder> newCardReminders) {
        // Clear and update
        cardReminders.clear();
        cardReminders.addAll(newCardReminders);
    }

    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);

        CardViewHolder viewHolder = new CardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardAdapter.CardViewHolder holder, int position) {
        holder.setData(cardReminders.get(position));
    }

    @Override
    public int getItemCount() {
        return cardReminders.size();
    }
}
