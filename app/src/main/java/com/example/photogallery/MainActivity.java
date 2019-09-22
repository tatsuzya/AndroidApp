package com.example.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static com.example.photogallery.R.id.btn_camera;
import static com.example.photogallery.R.id.imageView;

public class MainActivity extends AppCompatActivity {

    Button cam_btn;
    ImageView photo_view; //placeholder imageview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        cam_btn = (Button) findViewById(btn_camera);
        photo_view = (ImageView) findViewById(imageView);
        cam_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 123);

            }

        });

    }
    //PACEHOLDER CAMERA ACTIVITY
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = (ImageView) findViewById(imageView); //sets imageview as the bitmap
            imageview.setImageBitmap(image);


        }
    }
}