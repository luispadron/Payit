package lpadron.me.project1_payit.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import lpadron.me.project1_payit.R;

public class CardReminder implements Parcelable {
    private String cardName;
    private int dueDay;
    private Double amntOnCard;
    private Double interestRate;
    private int monthsToPayOff;
    private String issuingBank;
    private int generatedColor = -1;
    private int notificationID;
    public static final int HOUR_FOR_NOTIFICATION = 17;
    public static final int MINUTE_FOR_NOTIFICATION = 30;
    // Super big ugly array of colors, one will be randomly chosen when object is created
    private static final int[] COLORS = {
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color7,
            R.color.color8,
            R.color.color9,
            R.color.color11,
            R.color.color12,
            R.color.color19,
            R.color.color20,
    };

    public CardReminder(String cardName, int dueDay, Double amntOnCard, int monthsToPayOff,
                        Double interestRate, String issuingBank, int notificationID) {
        this.cardName = cardName;
        this.dueDay = dueDay;
        this.amntOnCard = amntOnCard;
        this.monthsToPayOff = monthsToPayOff;
        this.interestRate = interestRate;
        this.issuingBank = issuingBank;
        this.notificationID = notificationID;
        this.generatedColor = COLORS[generateColor()];
    }


    protected CardReminder(Parcel in) {
        cardName = in.readString();
        dueDay = in.readInt();
        monthsToPayOff = in.readInt();
        issuingBank = in.readString();
        generatedColor = in.readInt();
        notificationID = in.readInt();
    }

    public static final Creator<CardReminder> CREATOR = new Creator<CardReminder>() {
        @Override
        public CardReminder createFromParcel(Parcel in) {
            return new CardReminder(in);
        }

        @Override
        public CardReminder[] newArray(int size) {
            return new CardReminder[size];
        }
    };

    private static int generateColor() {
        // Generate random number
        Random rand = new Random();
        int max = COLORS.length;
        int min = 0;
        return rand.nextInt((max - min) + min);
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getDueDay() {
        return dueDay;
    }

    public String getFullDateFromDayAsString() {
        int month;

        Calendar phoneCalendar = Calendar.getInstance();
        month = phoneCalendar.get(Calendar.MONTH);

        Calendar appCalendar = Calendar.getInstance();


        appCalendar.set(Calendar.MONTH, month);
        appCalendar.set(Calendar.DAY_OF_MONTH, dueDay);

        Date currentDate = phoneCalendar.getTime();
        Date dueDate = appCalendar.getTime();

        if (dueDate.before(currentDate)) {
            if (month == 11) {
                month = 0;
            }
            appCalendar.set(Calendar.MONTH, month + 1);
            dueDate = appCalendar.getTime();
        }

        String dateS = dueDate.toString();


        return dateS.substring(0, 10);
    }

    public String getFullDateFromDayFormattedForNotification() {
        int month;

        Calendar phoneCalendar = Calendar.getInstance();
        month = phoneCalendar.get(Calendar.MONTH);

        Calendar appCalendar = Calendar.getInstance();


        appCalendar.set(Calendar.MONTH, month);
        appCalendar.set(Calendar.DAY_OF_MONTH, dueDay);

        Date currentDate = phoneCalendar.getTime();
        Date dueDate = appCalendar.getTime();

        if (dueDate.before(currentDate)) {
            if (month == 11) {
                month = 0;
            }
            appCalendar.set(Calendar.MONTH, month + 1);
            dueDate = appCalendar.getTime();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");

        String formattedDate = formatter.format(dueDate);

        return formattedDate;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public Double getAmntOnCard() {
        return amntOnCard;
    }

    public void setAmntOnCard(Double amntOnCard) {
        this.amntOnCard = amntOnCard;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public int getMonthsToPayOff() {
        return monthsToPayOff;
    }

    public void setMonthsToPayOff(int monthsToPayOff) {
        this.monthsToPayOff = monthsToPayOff;
    }

    public Double getDailyInterestRate() {
        return interestRate / 365;
    }

    public String getIssuingBank() {
        return issuingBank;
    }

    public void setIssuingBank(String issuingBank) {
        this.issuingBank = issuingBank;
    }

    public int getGeneratedColor() {
        return this.generatedColor;
    }

    public void setGeneratedColor(int generatedColor) {
        this.generatedColor = generatedColor;
    }

    // Do some maths, calculate payment amount based on months to pay off
    // DISCLAIMER: This math is not the best, just a rough average, I am not a bank
    // and credit card interest rates are extremely confusing to my young college mind
    public Double amountToPay() {
        Double dailyRate = getDailyInterestRate();
        Double amntOfInterestPayed = 0.0;
        Double totalAmountAfterXMonths = this.getAmntOnCard();

        for (int i = 1; i <= monthsToPayOff; i++) {
            amntOfInterestPayed = (amntOnCard * getDailyInterestRate() * 30) / 100;
            totalAmountAfterXMonths += amntOfInterestPayed;
        }

        return (totalAmountAfterXMonths / monthsToPayOff);
    }

    public int getNotificationID() {
        return notificationID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardName);
        dest.writeInt(dueDay);
        dest.writeInt(monthsToPayOff);
        dest.writeString(issuingBank);
        dest.writeInt(generatedColor);
        dest.writeInt(notificationID);
    }
}
