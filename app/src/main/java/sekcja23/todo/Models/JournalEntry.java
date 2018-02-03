package sekcja23.todo.Models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwojtowicz on 11.01.2018.
 */

//Model tabeli w bazie
public class JournalEntry {
    private String journalId;
    private String title;
    private String content;
    private long dateCreated;
    private long dateModified;
    private String userId;

    private PositionOnMap positionOnMap;

    private List<Remainder> remainderList;
    private List<FileResource> resourceList;

    //Seed prostego dziennika zadań
    public static List<JournalEntry> getSampleJournalEntries() {

        List<JournalEntry> journalEnrties = new ArrayList<>();
        JournalEntry journalEntry1 = new JournalEntry();
        journalEntry1.setTitle("DisneyLand Trip");
        journalEntry1.setContent("We went to Disneyland today and the kids had lots of fun!");
        Calendar calendar1 = GregorianCalendar.getInstance();
        journalEntry1.setDateModified(calendar1.getTimeInMillis());
        journalEnrties.add(journalEntry1);

        return journalEnrties;
    }

    //Settery
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(String userId) { this.userId = userId; }

    //Gettery
    public String getJournalId() { return this.journalId; }

    public String getTitle() { return this.title; }

    public String getContent() { return this.content; }

    public String getUserId() { return this.userId; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("journalId", journalId);
        result.put("title", title);
        result.put("content", content);
        result.put("dateModified", dateModified);
        result.put("userId", userId);

        return result;
    }
}


