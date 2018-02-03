package sekcja23.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sekcja23.todo.Utils.DrawView;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //Włączenie możliwości rysowania
        ConstraintLayout parent = findViewById(R.id.drawView);
        parent.setDrawingCacheEnabled(true);
        DrawView drawView = new DrawView(this);
        parent.addView(drawView);

        Intent intent = getIntent(); // gets the previously created intent

        //Dekompresja zdjęcia
        byte[] bytes = intent.getByteArrayExtra("imageBitmapCompressed");
        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        ImageView imView = (ImageView) findViewById(R.id.imageView2);
        imView.setImageBitmap(image);

        //Przycisk zapisujący zdjęcie
        FloatingActionButton fab = findViewById(R.id.fabSave);
        fab.bringToFront();
        fab.setOnClickListener((View v) -> {
            Bitmap b = parent.getDrawingCache();
            saveToInternalStorage(b);
        });
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "ToDo_" + timeStamp + ".jpg";
        File mypath = new File(directory, "todo.jpg");

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
                dialog.setMessage("Zdjęcie zostało zapisane pomyślnie. Czy chcesz zrobić kolejne?");
                dialog.setPositiveButton("Nie", (DialogInterface dialogInterface, int id) -> {
                    Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(nextScreen);
                }).setNegativeButton("Tak ", (DialogInterface dialogInterface, int which) -> {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra("android.intent.extra.quickCapture", true);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent nextScreen = new Intent(getApplicationContext(), PhotoActivity.class);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            nextScreen.putExtra("imageBitmap", imageBitmap);
            startActivity(nextScreen);
        }
    }
}
