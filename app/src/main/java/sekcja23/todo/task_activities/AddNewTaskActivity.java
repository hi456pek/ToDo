package sekcja23.todo.task_activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sekcja23.todo.HomeActivity;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.R;

public class AddNewTaskActivity extends AppCompatActivity {
    // Layout
    protected static final int ADD_TASK_LAYOUT = R.layout.activity_add_new_task;
    protected static final int ADD_REMAINDER_DIALOG_LAYOUT = R.layout.remainder_dialog;

    // Controls Names
    protected static final int ADD_BUTTON_CONTROL = R.id.addButton;
    protected static final int TITLE_FIELD_CONTROL = R.id.titleField;
    protected static final int COMMENT_FIELD_CONTROL = R.id.commentField;
    protected static final int ADD_LOCATION_CONTROL = R.id.addPlace;
    protected static final int ADD_REMAINDER_CONTROL = R.id.addRemainder;
    protected static final int ADD_PHOTONOTE_CONTROL = R.id.addPhotoNote;
    protected static final int ADD_VOICENOTE_CONTROL = R.id.addVoiceNote;


    // Strings
    protected static final String JOURNAL_DATABASE_TABLE = "journalentris";
    protected static final int REMAINDER_DIALOG_TITLE = R.string.remainder_dialog_title;
    protected static final int ADD_BUTTON_TEXT = R.string.add_button;
    protected static final int CANCEL_BUTTON_TEXT = R.string.cancel_button;

    // Controls
    protected Button addButton;
    protected ImageButton addLocationButton;
    protected ImageButton addReminderButton;
    protected ImageButton addPhotoNoteButton;
    protected ImageButton addVoiceNoteButton;

    protected EditText titleField;
    protected EditText commentField;

    protected TimePicker reminderTimeField;
    protected DatePicker reminderDateField;

    //Dialogs
    protected AlertDialog.Builder dialogBuilder;
    protected AlertDialog addReminderDialog;

    //Referencja do bazy
    protected DatabaseReference mDatabase;

    //Referencja do czegoś w rodzaju tabeli w bazie
    protected DatabaseReference journalCloudEndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ADD_TASK_LAYOUT);

        this.dialogBuilder = new AlertDialog.Builder(this);

        this.initDataBase();
        this.initControls();
        this.initAddButtonOnClickFunction();
        this.prepareDialogs();
        this.upBarButtonsFunctions();
    }

    protected void initControls() {
        this.addButton = findViewById(ADD_BUTTON_CONTROL);
        this.addLocationButton = findViewById(ADD_LOCATION_CONTROL);
        this.addReminderButton = findViewById(ADD_REMAINDER_CONTROL);
        this.addPhotoNoteButton = findViewById(ADD_PHOTONOTE_CONTROL);
        this.addVoiceNoteButton = findViewById(ADD_VOICENOTE_CONTROL);


        this.titleField = findViewById(TITLE_FIELD_CONTROL);
        this.commentField = findViewById(COMMENT_FIELD_CONTROL);
    }

    protected void initDataBase() {
        //Instancja bazy
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Instancja czegoś w rodzaju tabeli w bazie
        journalCloudEndPoint = mDatabase.child(JOURNAL_DATABASE_TABLE);
    }

    protected void initAddButtonOnClickFunction() {
        this.addButton.setOnClickListener((View v) -> {
            //Utworzenie klucza dla nowego wpisu w bazie
            String key = journalCloudEndPoint.push().getKey();

            //Nowy obiekt do dodania do bazy
            JournalEntry journalEntry = new JournalEntry();

            //Inicjalizacja obiektu poprzez settery
            journalEntry.setTitle(this.titleField.getText().toString());
            journalEntry.setContent(this.commentField.getText().toString());
            journalEntry.setJournalId(key);

            //Dodanie obiektu do bazy
            journalCloudEndPoint.child(key).setValue(journalEntry);

            //Powrót do widoku listy
            this.finishActivity();
        });
    }

    protected void prepareDialogs() {

        this.dialogBuilder.setTitle(REMAINDER_DIALOG_TITLE);
        this.dialogBuilder.setView(getLayoutInflater().inflate(ADD_REMAINDER_DIALOG_LAYOUT, null))
                .setPositiveButton(ADD_BUTTON_TEXT, (DialogInterface dialog, int id) -> {
                    this.addNewReminder();
                    dialog.dismiss();
                })
                .setNegativeButton(CANCEL_BUTTON_TEXT, (DialogInterface dialog, int id) -> dialog.cancel());
        this.addReminderDialog = this.dialogBuilder.create();
    }

    protected void upBarButtonsFunctions() {
        this.addReminderButton.setOnClickListener((View v) -> this.addReminderDialog.show());
    }

    protected void addNewReminder() {
        //TODO add new reminder into database!
    }

    protected void finishActivity() {
        Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(nextScreen, 100);
    }

}
