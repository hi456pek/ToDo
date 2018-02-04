package sekcja23.todo.Models;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import sekcja23.todo.R;

public class Remainder implements Addon {
    protected static final String REMAINDER_DATABASE_TABLE = "remainders";
    protected static final int REMAINDER_ICON = R.drawable.ic_alarm_green_24dp;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.US);
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.US);

    private String remainderId;
    private String journalId;
    private long date;

    public Remainder() {

    }

    public Remainder(Calendar date, String journalId) {
        this.date = date.getTimeInMillis();
        this.journalId = journalId;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public void save() {
        DatabaseReference remainderCloudEndPoint = FirebaseDatabase.getInstance().getReference().child(REMAINDER_DATABASE_TABLE);
        this.remainderId = remainderCloudEndPoint.push().getKey();
        remainderCloudEndPoint.child(this.remainderId).setValue(this);
    }

    @Override
    public int getIcon() {
        return REMAINDER_ICON;
    }

    @Override
    public String getText() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(this.date);
        return Remainder.dateTimeFormat.format(calendar.getTime());
    }
}
