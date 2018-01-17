package sekcja23.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sekcja23.todo.Adapters.JournalAdapter;
import sekcja23.todo.Models.JournalEntry;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Referencja do bazy
    private DatabaseReference mDatabase;

    //Referencja do czegoś w rodzaju tabeli w bazie
    private DatabaseReference journalCloudEndPoint;

    //Lista wpisów
    private ArrayList<JournalEntry> journalEntries;

    //Metoda seed
    private void addInitialDataToFirebase() {
        List<JournalEntry> sampleJournalEntries = JournalEntry.getSampleJournalEntries();
        for (JournalEntry journalEntry : sampleJournalEntries) {
            String key = journalCloudEndPoint.push().getKey();
            journalEntry.setJournalId(key);
            journalCloudEndPoint.child(key).setValue(journalEntry);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Instancja bazy
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Instancja czegoś w rodzaju tabeli w bazie
        journalCloudEndPoint = mDatabase.child("journalentris");

        //Dodanie początkowego zadania do bazy - już zrobione dlatego zakomentowane. Nie odkomentowywać.
        //addInitialDataToFirebase();

        //Referencja na ListView z zadaniami w widoku
        final ListView journalsList = (ListView) findViewById(R.id.journalsList);

        //Kontekst tego widoku na potrzeby ListView
        final Context cont = this;

        //Incijalizacja listy na potrzeby wyświetlania w widoku
        journalEntries = new ArrayList<>();

        //Pobieranie danych
        journalCloudEndPoint.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    JournalEntry note = noteSnapshot.getValue(JournalEntry.class);

                    //Dodanie pobranych zadań do listy
                    journalEntries.add(note);

                    //Wyświetlenie zadań w ListView
                    ArrayAdapter adapter = new JournalAdapter(cont, R.layout.journal_item, journalEntries);
                    journalsList.setAdapter(adapter);
                }
            }

            //Metoda do celów diagnostycznych
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Dodawanie nowego zadania", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent nextScreen = new Intent(getApplicationContext(), AddNewTaskActivity.class);
                startActivityForResult(nextScreen, 100);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent myIntent = getIntent(); // gets the previously created intent
        String userEmail = myIntent.getStringExtra("userEmail");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.userEmailTextView);
        nav_user.setText(userEmail);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
