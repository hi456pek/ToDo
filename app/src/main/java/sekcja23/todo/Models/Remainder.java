package sekcja23.todo.Models;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Remainder {

    private String journalId;
    private long date;


    public Remainder(Date date, String journalId) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        this.date = calendar.getTimeInMillis();
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
}
