package sekcja23.todo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent(); // gets the previously created intent

        //Dekompresja zdjęcia
        byte[] bytes = intent.getByteArrayExtra("imageBitmapCompressed");
        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Bitmap croppedBitmap = Bitmap.createBitmap(image, 0, 350, image.getWidth(), (image.getHeight() - 350));
        croppedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), (croppedBitmap.getHeight() - 350));

        ImageView imView= (ImageView) findViewById(R.id.imageView3);
        imView.setImageBitmap(croppedBitmap);
    }
}
