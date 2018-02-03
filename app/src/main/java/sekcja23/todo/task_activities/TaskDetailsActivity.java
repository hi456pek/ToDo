package sekcja23.todo.task_activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;

import sekcja23.todo.HomeActivity;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.PhotoActivity;
import sekcja23.todo.PhotoDetailActivity;
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
                Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(nextScreen);
            });
        });

        this.removeButton.setOnClickListener((View v) -> {
            this.referenceToModel.removeValue();
            Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(nextScreen);
        });
    }

    protected void setEnabledTextFields(boolean state) {
        this.titleField.setEnabled(state);
        this.commentField.setEnabled(state);
    }

    protected void fillEditText() {
        if(this.taskDetailsModel.getContent().contains(".jpg"))
        {
            loadImageFromStorage(this.taskDetailsModel.getContent());
        }
        else
        {
            this.titleField.setText(this.taskDetailsModel.getTitle());
            this.commentField.setText(this.taskDetailsModel.getContent());
        }
    }

    //Metoda do pobierania zdjęcia z pamięci telefonu
    private void loadImageFromStorage(String fileName)
    {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            File f=new File(directory, fileName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            Intent nextScreen = new Intent(getApplicationContext(), PhotoDetailActivity.class);

            //Compress bitmap
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            nextScreen.putExtra("imageBitmapCompressed", bytes);
            this.finishActivity();
            startActivity(nextScreen);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void finishActivity() {
        Intent returnIntent = new Intent(getApplicationContext(), HomeActivity.class);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
