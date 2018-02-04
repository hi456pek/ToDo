package sekcja23.todo.task_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import sekcja23.todo.Adapters.AddonAdapter;
import sekcja23.todo.HomeActivity;
import sekcja23.todo.Models.Addon;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.Models.Remainder;
import sekcja23.todo.R;
import sekcja23.todo.Utils.LocationDialog;
import sekcja23.todo.Utils.RemainderDialog;

public class AddNewTaskActivity extends AppCompatActivity {
    // Layout
    protected static final int ADD_TASK_LAYOUT = R.layout.activity_add_new_task;

    // Controls Names
    protected static final int ADD_BUTTON_CONTROL = R.id.addButton;
    protected static final int TITLE_FIELD_CONTROL = R.id.titleField;
    protected static final int COMMENT_FIELD_CONTROL = R.id.commentField;
    protected static final int ADDONS_LIST = R.id.addonsList;
    protected static final int BUTTONS_BAR = R.id.buttonsBar;
    protected static final int ADD_LOCATION_CONTROL = R.id.addPlace;
    protected static final int ADD_REMAINDER_CONTROL = R.id.addRemainder;
    protected static final int ADD_PHOTONOTE_CONTROL = R.id.addPhotoNote;
    protected static final int ADD_VOICENOTE_CONTROL = R.id.addVoiceNote;

    // Strings
    protected static final String JOURNAL_DATABASE_TABLE = "journalentris";
    protected static final String REMAINDER_DATABASE_TABLE = "remainders";

    // Controls
    protected Button addButton;
    protected ImageButton addLocationButton;
    protected ImageButton addReminderButton;
    protected ImageButton addPhotoNoteButton;
    protected ImageButton addVoiceNoteButton;
    protected ListView addonsList;

    protected EditText titleField;
    protected EditText commentField;

    protected LinearLayout buttonsBar;

    //Dialogs
    protected RemainderDialog addReminderDialog;
    protected LocationDialog locationDialog;

    //Referencja do bazy
    protected DatabaseReference mDatabase;

    //Referencja do czegoś w rodzaju tabeli w bazie
    protected DatabaseReference journalCloudEndPoint;
    protected DatabaseReference remainderCloudEndPoint;

    protected String newTaskKey;
    protected ArrayList<Addon> addonsEntries;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ADD_TASK_LAYOUT);
        this.context = this;
        this.addonsEntries = new ArrayList<>();

        this.initDataBase();
        this.initControls();
        this.initAddButtonOnClickFunction();
        this.prepareDialogs();
        this.upBarButtonsFunctions();
    }

    protected void initControls() {
        this.addButton = findViewById(ADD_BUTTON_CONTROL);
        this.buttonsBar = findViewById(BUTTONS_BAR);
        this.addLocationButton = findViewById(ADD_LOCATION_CONTROL);
        this.addReminderButton = findViewById(ADD_REMAINDER_CONTROL);
        this.addPhotoNoteButton = findViewById(ADD_PHOTONOTE_CONTROL);
        this.addVoiceNoteButton = findViewById(ADD_VOICENOTE_CONTROL);
        this.addonsList = findViewById(ADDONS_LIST);

        this.titleField = findViewById(TITLE_FIELD_CONTROL);
        this.commentField = findViewById(COMMENT_FIELD_CONTROL);
    }

    protected void initDataBase() {
        //Instancja bazy
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Instancja czegoś w rodzaju tabeli w bazie
        journalCloudEndPoint = mDatabase.child(JOURNAL_DATABASE_TABLE);
        remainderCloudEndPoint = mDatabase.child(REMAINDER_DATABASE_TABLE);
        //remainderCloudEndPoint.removeValue();

        this.newTaskKey = journalCloudEndPoint.push().getKey();
    }

    protected void initAddButtonOnClickFunction() {
        this.addButton.setOnClickListener((View v) -> {
            //Utworzenie klucza dla nowego wpisu w bazie

            //Nowy obiekt do dodania do bazy
            JournalEntry journalEntry = new JournalEntry();

            //Inicjalizacja obiektu poprzez settery
            journalEntry.setTitle(this.titleField.getText().toString());
            journalEntry.setContent(this.commentField.getText().toString());
            journalEntry.setJournalId(this.newTaskKey);

            SharedPreferences settings = getApplicationContext().getSharedPreferences("ToDoPreferences", 0);
            String currentUserId = settings.getString("CurrentUser", "");

            journalEntry.setUserId(currentUserId);

            //Dodanie obiektu do bazy
            journalCloudEndPoint.child(this.newTaskKey).setValue(journalEntry);

            for (Addon item : this.addonsEntries) {
                item.save();
            }

            //Powrót do widoku listy
            this.finishActivity();
        });
    }

    protected void prepareDialogs() {
        this.addReminderDialog = new RemainderDialog(this, getLayoutInflater(), this.newTaskKey);
        this.locationDialog = new LocationDialog(this, getLayoutInflater(), this.newTaskKey);

    }

    protected void upBarButtonsFunctions() {
        this.addReminderButton.setOnClickListener((View v) -> {
            this.addReminderDialog.show();
            this.addReminderDialog.setOnDismissListener(dialogInterface -> {
                Remainder remainder = this.addReminderDialog.getRemainder();
                if (remainder != null) {
                    addonsEntries.add(remainder);
                    ArrayAdapter adapter = new AddonAdapter(context, R.layout.addon_item, addonsEntries);
                    addonsList.setAdapter(adapter);
                }
            });
        });

        this.addLocationButton.setOnClickListener((View v) -> {
            this.locationDialog.show();
            this.locationDialog.setOnDismissListener(dialogInterface -> {

            });
        });

        this.addPhotoNoteButton.setOnClickListener((View v) -> {

        });

        this.addVoiceNoteButton.setOnClickListener((View v) -> {

        });
    }


    protected void finishActivity() {
        Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(nextScreen, 100);
    }

}
