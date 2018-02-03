package sekcja23.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.Utils.DrawView;
import sekcja23.todo.task_activities.AddNewTaskActivity;

public class PhotoActivity extends AppCompatActivity {

    //Referencja do bazy
    protected DatabaseReference mDatabase;

    //Referencja do czegoś w rodzaju tabeli w bazie
    protected DatabaseReference journalCloudEndPoint;

    protected static final String JOURNAL_DATABASE_TABLE = "journalentris";

    //Zmienne na potrzeby obsługi aparatu
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        initDataBase();

        //Włączenie możliwości rysowania
        ConstraintLayout parent = findViewById(R.id.drawView);
        parent.setDrawingCacheEnabled(true);
        DrawView drawView = new DrawView(this);
        parent.addView(drawView);

        Intent intent = getIntent(); // gets the previously created intent

        //Dekompresja zdjęcia
        byte[] bytes = intent.getByteArrayExtra("imageBitmapCompressed");
        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        ImageView imView= (ImageView) findViewById(R.id.imageView2);
        imView.setImageBitmap(image);

        //Przycisk zapisujący zdjęcie
        FloatingActionButton fab = findViewById(R.id.fabSave);
        fab.bringToFront();
        fab.setOnClickListener((View v) -> {
            Bitmap b = parent.getDrawingCache();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            saveToInternalStorage(b, timeStamp);
            addToTaskList(timeStamp);
        });
    }

    protected void initDataBase() {
        //Instancja bazy
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        //Instancja czegoś w rodzaju tabeli w bazie
        journalCloudEndPoint = mDatabase.child(JOURNAL_DATABASE_TABLE);
    }

    private void addToTaskList(String timeStamp)
    {
        //Utworzenie klucza dla nowego wpisu w bazie
        String key = journalCloudEndPoint.push().getKey();

        //Nowy obiekt do dodania do bazy
        JournalEntry journalEntry = new JournalEntry();

        //Inicjalizacja obiektu poprzez settery
        journalEntry.setTitle("Notatka zdjęciowa");
        journalEntry.setContent("ToDo_" + timeStamp + ".jpg");
        journalEntry.setJournalId(key);

        //Dodanie obiektu do bazy
        journalCloudEndPoint.child(key).setValue(journalEntry);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String timeStamp){

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        String imageFileName = "ToDo_" + timeStamp + ".jpg";
        File mypath=new File(directory, imageFileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setCancelable(false);
                dialog.setTitle("Zapisano");
                dialog.setMessage("Zdjęcie zostało zapisane pomyślnie. Czy chcesz zrobić kolejne?" );
                dialog.setPositiveButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(nextScreen);
                    }
                })
                        .setNegativeButton("Tak ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleCamera();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void handleCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
    }

    //Create temporary image file
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                //Compress bitmap
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();

                //Open preview
                Intent nextScreen = new Intent(getApplicationContext(), PhotoActivity.class);
                nextScreen.putExtra("imageBitmapCompressed", bytes);
                startActivity(nextScreen);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
