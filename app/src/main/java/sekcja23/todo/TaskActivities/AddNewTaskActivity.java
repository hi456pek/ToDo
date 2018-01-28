package sekcja23.todo.TaskActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sekcja23.todo.HomeActivity;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.R;

public class AddNewTaskActivity extends AppCompatActivity {

    //Referencja do bazy
    private DatabaseReference mDatabase;

    //Referencja do czegoś w rodzaju tabeli w bazie
    private DatabaseReference journalCloudEndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        //Instancja bazy
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Instancja czegoś w rodzaju tabeli w bazie
        journalCloudEndPoint = mDatabase.child("journalentris");

        //Obsługa przycisku dodawania nowego zadania
        final Button button = (Button) findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Utworzenie klucza dla nowego wpisu w bazie
                String key = journalCloudEndPoint.push().getKey();

                //Nowy obiekt do dodania do bazy
                JournalEntry journalEntry = new JournalEntry();

                //Pobranie tekstu z pola edycyjnego z tytułem zadnia
                EditText titleEdit = (EditText) findViewById(R.id.titleField);

                //Pobranie tekstu z pola edycyjnego z opisem zadania
                EditText contentEdit = (EditText) findViewById(R.id.commentField);

                //Inicjalizacja obiektu poprzez settery
                journalEntry.setTitle(titleEdit.getText().toString());
                journalEntry.setContent(contentEdit.getText().toString());
                journalEntry.setJournalId(key);

                //Dodanie obiektu do bazy
                journalCloudEndPoint.child(key).setValue(journalEntry);

                //Powrót do widoku listy
                Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
                startActivityForResult(nextScreen, 100);
            }
        });
    }
}
