package sekcja23.todo.task_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.GregorianCalendar;

import sekcja23.todo.HomeActivity;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.R;

public class TaskDetailsActivity extends AddNewTaskActivity {

    // Controls
    protected static final int MODIFY_BUTTON_CONTROL = R.id.modifyButton;
    protected static final int REMOVE_BUTTON_CONTROL = R.id.removeButton;

    // String
    protected static final int MODIFY_STRING = R.string.modify_button;
    protected static final int SAVE_STRING = R.string.save_button;
    protected static final String TASK_ID = "TaskId";

    // Controls
    protected Button modifyButton;
    protected Button removeButton;

    // Data models
    protected JournalEntry taskDetailsModel;
    protected DatabaseReference referenceToModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.init();
    }

    protected void init() {
        this.referenceToModel = journalCloudEndPoint.child(getIntent().getStringExtra(TASK_ID));
        this.initControls();
        this.initTaskData();
        this.setEnabledTextFields(false);
        this.setButtonsOnClickFunction();
    }

    protected void initControls() {
        super.initControls();

        this.modifyButton = findViewById(MODIFY_BUTTON_CONTROL);
        this.removeButton = findViewById(REMOVE_BUTTON_CONTROL);

        this.addButton.setVisibility(View.INVISIBLE);
        this.modifyButton.setVisibility(View.VISIBLE);
        this.removeButton.setVisibility(View.VISIBLE);
    }

    protected void initTaskData() {
        String taskId = getIntent().getStringExtra(TASK_ID);
        Log.i("TaskID => ", taskId);
        if (taskId != null)
            this.referenceToModel.addListenerForSingleValueEvent(new ValueEventListener() {
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

    protected void setButtonsOnClickFunction() {
        this.modifyButton.setOnClickListener((View v) -> {
            this.setEnabledTextFields(true);
            this.modifyButton.setText(getResources().getText(SAVE_STRING));
            this.modifyButton.setOnClickListener((View view) -> {
                JournalEntry newModel = new JournalEntry();
                newModel.setDateModified(new GregorianCalendar().getTimeInMillis());
                newModel.setContent(this.commentField.getText().toString());
                newModel.setTitle(this.titleField.getText().toString());

                this.referenceToModel.updateChildren(newModel.toMap());
                this.finishActivity();
            });
        });

        this.removeButton.setOnClickListener((View v) -> {
            this.referenceToModel.removeValue();
            this.finishActivity();
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

    @Override
    protected void finishActivity() {
        Intent returnIntent = new Intent(getApplicationContext(), HomeActivity.class);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
