package sekcja23.todo.TaskActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.GregorianCalendar;

import sekcja23.todo.HomeActivity;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.R;

public class TaskDetailsActivity extends AppCompatActivity {
    // Layout
    private static final int ADD_TASK_LAYOUT = R.layout.activity_add_new_task;
    // Controls
    private static final int ADD_BUTTON_CONTROL = R.id.addButton;
    private static final int TITLE_FIELD_CONTROL = R.id.titleField;
    private static final int COMMENT_FIELD_CONTROL = R.id.commentField;
    // String
    private static final int MODIFY_STRING = R.string.modify_button;
    private static final int SAVE_STRING = R.string.save_button;
    private static final String DATABASE_TABLE = "journalentris";
    private static final String TASK_ID = "TaskId";

    // Controls
    private Button modifyButton;
    private EditText titleField;
    private EditText commentField;

    // Database
    private DatabaseReference mDatabase;
    private DatabaseReference journalCloudEndPoint;

    // Data models
    private JournalEntry taskDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ADD_TASK_LAYOUT);
        this.init();
    }

    protected void init() {
        this.initControls();
        this.initDataBaseAndTaskData();
        this.setEnabledTextFields(false);
        this.setButtonFunction(this.modifyButton);

    }

    protected void initControls() {
        this.modifyButton = findViewById(ADD_BUTTON_CONTROL);
        this.titleField = findViewById(TITLE_FIELD_CONTROL);
        this.commentField = findViewById(COMMENT_FIELD_CONTROL);
    }

    protected void initDataBaseAndTaskData() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        journalCloudEndPoint = this.mDatabase.child(DATABASE_TABLE);
        String taskId = getIntent().getStringExtra(TASK_ID);
        Log.i("TaskID => ", taskId);
        if (taskId != null)
            journalCloudEndPoint.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    taskDetailsModel = dataSnapshot.getValue(JournalEntry.class);
                    fillEditText();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

    protected void setButtonFunction(Button modifyButton) {
        modifyButton.setText(getResources().getText(MODIFY_STRING));
        modifyButton.setOnClickListener((View v) -> {
            this.setEnabledTextFields(true);
            modifyButton.setText(getResources().getText(SAVE_STRING));
            modifyButton.setOnClickListener((View view) -> {
                JournalEntry newModel = new JournalEntry();
                newModel.setDateModified(new GregorianCalendar().getTimeInMillis());
                newModel.setContent(this.commentField.getText().toString());
                newModel.setTitle(this.titleField.getText().toString());

                journalCloudEndPoint.child(getIntent().getStringExtra(TASK_ID)).updateChildren(newModel.toMap());

                Intent returnIntent = new Intent(getApplicationContext(), HomeActivity.class);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            });
        });

    }

    protected void setEnabledTextFields(boolean state) {
        this.titleField.setEnabled(state);
        this.commentField.setEnabled(state);
    }

    protected void fillEditText() {
        this.titleField.setText(this.taskDetailsModel.getTitle());
        this.commentField.setText(this.taskDetailsModel.getContent());
    }

}
