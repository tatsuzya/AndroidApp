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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Models.Image;
import Models.SearchResults;

public class MainActivity extends AppCompatActivity {

    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    ImageView imageView;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ArrayList<String> photoGallery;
    private int currentPhotoIndex = 0;
    private SearchResults storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new SearchResults();
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
                displayPhoto(currentPhotoPath);

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(currentPhotoPath);
                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(myIntent, SEARCH_ACTIVITY_REQUEST_CODE);
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
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("back to the zone");
        if(storage.updateResult) {
            storage.updateResult = false;
            photoGallery = new ArrayList<String>();

            System.out.println("Total Pictures: " + storage.imageList.size());
            for(int i =0; i < storage.imageList.size(); i++) {
                Image currentImage = storage.imageList.get(i);
                System.out.println(currentImage.Filename);
                System.out.println(currentImage.foundInSearch);
                if(currentImage.foundInSearch) {
                    photoGallery.add(currentImage.Filename);
                }
            }
            System.out.println(photoGallery);
            /*currentPhotoIndex = 0;

            if(photoGallery.size() > 0) {
                System.out.println("You are in");
                this.displayPhoto(photoGallery.get(currentPhotoIndex));
            }*/
        }
    }

    private void setTimestamp(){
        TextView timestamp_textView = (TextView)findViewById(R.id.timestamp_textview);
        String temp = currentPhotoPath;
        String temp2;
        String timestamp;

        temp2 = temp.substring(temp.indexOf("JPEG_"), temp.indexOf(".jpg"));
        timestamp = temp2.substring(5,13);
        timestamp_textView.setText(timestamp);
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

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        //Date minDate = new Date(Long.MIN_VALUE);
        //Date maxDate = new Date(Long.MAX_VALUE);
        //photoGallery = populateGallery(minDate, maxDate);
        setTimestamp();
    }

}
