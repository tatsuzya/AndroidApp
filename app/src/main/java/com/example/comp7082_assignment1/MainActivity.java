package com.example.comp7082_assignment1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ArrayList<String> photoGallery;
    private int currentPhotoIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = (Button)findViewById(R.id.button_snap);
        Button btnLeft = (Button)findViewById(R.id.button_left);
        Button btnRight = (Button)findViewById(R.id.button_right);
        Button btnSearch = (Button)findViewById(R.id.button_search);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --currentPhotoIndex;
                //When reach the first photo
                if (currentPhotoIndex < 0){
                    currentPhotoIndex = photoGallery.size() - 1;
                }
                //When reach the last photo
                if (currentPhotoIndex >= photoGallery.size()){
                    currentPhotoIndex = 0;
                }

                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                Log.d("photoleft, size", Integer.toString(photoGallery.size()));
                Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
                displayPhoto(currentPhotoPath);
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++currentPhotoIndex;
                //When reach the first photo
                if (currentPhotoIndex < 0){
                    currentPhotoIndex = photoGallery.size() - 1;
                }
                //When reach the last photo
                if (currentPhotoIndex >= photoGallery.size()){
                    currentPhotoIndex = 0;
                }

                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                Log.d("photoleft, size", Integer.toString(photoGallery.size()));
                Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
                displayPhoto(currentPhotoPath);

            }
        });


        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoGallery = populateGallery(minDate, maxDate);
        // Print the size of photoGallery on the Log
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0){
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
        }

        //imageView = (ImageView)findViewById(R.id.imageView);
        displayPhoto(currentPhotoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File imgFile = new  File(currentPhotoPath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
            imageView.setImageBitmap(myBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                //JPEG_20190925_232600_5043141561115288621.jpg
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("", "The File cannot be created.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //makes link for JPEG_20190925_232600_5043141561115288621.jpg
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.comp7082_assignment1",
                        photoFile);
                this.currentPhotoPath = photoFile.toString();
                System.out.println(this.currentPhotoPath);
                //camera app
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.comp7082_assignment1/files/Pictures");
        photoGallery = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : file.listFiles()) {
                photoGallery.add(f.getPath());
            }
        }
        return photoGallery;
    }

    private void displayPhoto(String path) {
        //imageView = (ImageView)findViewById(R.id.imageView);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
    }

}
