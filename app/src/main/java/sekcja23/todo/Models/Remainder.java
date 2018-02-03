package sekcja23.todo.Models;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Remainder {
    protected static final String REMAINDER_DATABASE_TABLE = "remainders";

    private String remainderId;
    private String journalId;
    private long date;

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

    public void save() {
        DatabaseReference remainderCloudEndPoint = FirebaseDatabase.getInstance().getReference().child(REMAINDER_DATABASE_TABLE);
        this.remainderId = remainderCloudEndPoint.push().getKey();
        remainderCloudEndPoint.child(this.remainderId).setValue(this);
    }
}
