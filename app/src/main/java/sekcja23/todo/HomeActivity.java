package sekcja23.todo;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sekcja23.todo.Adapters.JournalAdapter;
import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.task_activities.AddNewTaskActivity;
import sekcja23.todo.task_activities.TaskDetailsActivity;

//Klasa obsługująca ekran główny aplikacji wyświetlający listę zadań.
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Stałe łańcuchy znaków.
    private static final String DATABASE_TABLE = "journalentris";
    private static final String TASK_ID = "TaskId";
    private static final String EMAIL_ADDRES = "userEmail";

    //Zmienne na potrzeby obsługi aparatu.
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    //Referencja do bazy
    private DatabaseReference mDatabase;

    //Referencja do czegoś w rodzaju tabeli w bazie.
    private DatabaseReference journalCloudEndPoint;

    //Lista wpisów
    private ArrayList<JournalEntry> journalEntries;

    //Kontrolka listy w widoku
    private ListView journalsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Inicjalizacja komunikacji z bazą danych Firebase.
        initDataBase();

        //Incijalizacja listy na potrzeby wyświetlania w widoku.
        journalEntries = new ArrayList<>();

        //Pobieranie danych
        getJournalData();

        //Obsługa dotknięcia przycisku dodawania zadania.
        goToAddNewTask();

        //Obsługa dotknięcia elementu listy.
        goToTaskDetails();

        //Inicjalizacja menu bocznego.
        initDrawerLayout();

        //Ustawienie w widoku menu adresu e-mail aktualnie zalogowanego użytkownika.
        setUserEmailInNavigationView();
    }

    //Obsługa dotknięcia przycisku dodawania zadania.
    private void goToAddNewTask()
    {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> {
            Intent nextScreen = new Intent(getApplicationContext(), AddNewTaskActivity.class);
            startActivity(nextScreen);
        });
    }

    //Obsługa dotknięcia elementu listy.
    private void goToTaskDetails()
    {
        journalsList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            JournalEntry entry = (JournalEntry) journalsList.getItemAtPosition(position);

            if(entry != null) {
                Intent nextScreen = new Intent(getApplicationContext(), TaskDetailsActivity.class);
                nextScreen.putExtra(TASK_ID, entry.getJournalId());
                startActivityForResult(nextScreen, 100);
            }
        });
    }

    //Ustawienie w widoku menu adresu e-mail aktualnie zalogowanego użytkownika.
    private void setUserEmailInNavigationView()
    {
        Intent myIntent = getIntent();
        String userEmail = myIntent.getStringExtra(EMAIL_ADDRES);

        NavigationView navigationView = findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.userEmailTextView);
        nav_user.setText(userEmail);

        navigationView.setNavigationItemSelectedListener(this);
    }

    //Inicjalizacja komunikacji z bazą danych Firebase.
    private void initDataBase()
    {
        //Instancja bazy
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Instancja czegoś w rodzaju tabeli w bazie
        journalCloudEndPoint = mDatabase.child(DATABASE_TABLE);
    }

    //Inicjalizacja menu bocznego.
    private void initDrawerLayout()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Metoda pobierająca listę zadań.
    private void getJournalData()
    {
        //Referencja na ListView z zadaniami w widoku
        journalsList = (findViewById(R.id.journalsList));

        //Kontekst tego widoku na potrzeby ListView
        final Context cont = this;

        journalCloudEndPoint.addValueEventListener(new ValueEventListener() {

            SharedPreferences settings = getApplicationContext().getSharedPreferences("ToDoPreferences", 0);
            String currentUserId = settings.getString("CurrentUser", "");

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    JournalEntry note = noteSnapshot.getValue(JournalEntry.class);

                    if(note.getUserId() != null && note.getUserId().equals(currentUserId)) {
                        //Dodanie pobranych zadań do listy
                        journalEntries.add(note);
                    }

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
    }

    //Obsługa sprzętowego przycisku wstecz.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra("android.intent.extra.quickCapture",true);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else if (id == R.id.nav_slideshow) {
            Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(nextScreen);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            Intent nextScreen = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(nextScreen);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Tworzenie tymczasowego pliku zdjęcia w pamięci urządzenia.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    //Reakcja na zrobienie zdjęcia.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                //Skompresuj bitmapę przed przesłaniem do podglądu zdjęcia
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();

                //Otwórz podgląd z możliwością rysowania po zdjęciu
                Intent nextScreen = new Intent(getApplicationContext(), PhotoActivity.class);
                nextScreen.putExtra("imageBitmapCompressed", bytes);
                startActivity(nextScreen);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
