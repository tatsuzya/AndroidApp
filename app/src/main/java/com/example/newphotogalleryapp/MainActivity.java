// 2019-10-16 5:50pm
package com.example.newphotogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.Image;
import Models.SearchResults;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    private int currentPhotoIndex;
    private ArrayList<String> photoGallery;
    private String currentPhotoPath;
    // private String tagPath;
    private ImageView imageView;
    private boolean displayimageaftercapture;
    private SearchResults storage;      // storage is a model of SearchResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ask users for permission if access device location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("no permission request 4 them");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        storage = new SearchResults();      // SearchResult is a model
        currentPhotoIndex = 0;
        Button btnCamera = (Button)findViewById(R.id.button_snap);
        Button btnLeft = (Button)findViewById(R.id.button_left);
        Button btnRight = (Button)findViewById(R.id.button_right);
        Button btnSearch = (Button)findViewById(R.id.button_search);
        Button btnEditTag = (Button)findViewById(R.id.button_edit);
        Button btnReset = (Button)findViewById(R.id.button_reset);
        displayimageaftercapture = false;

        // onclick for the Snap button
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // onclick for the left button
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoGallery.size() > 0){
                    --currentPhotoIndex;
                    // when reach the first photo
                    if (currentPhotoIndex < 0){
                        currentPhotoIndex = photoGallery.size() - 1;
                    }
                    // when reach the last photo
                    if (currentPhotoIndex >= photoGallery.size()){
                        currentPhotoIndex = 0;
                    }

                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                    System.out.println(currentPhotoIndex);
                }
            }
        });

        // onclick for the right button
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoGallery.size() > 0){
                    ++currentPhotoIndex;
                    // when reach the first photo
                    if (currentPhotoIndex < 0){
                        currentPhotoIndex = photoGallery.size() - 1;
                    }
                    // when reach the last photo
                    if (currentPhotoIndex >= photoGallery.size()){
                        currentPhotoIndex = 0;
                    }

                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                    System.out.println(currentPhotoIndex);
                }
            }
        });

        // onclick for the search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(myIntent, 1);
            }
        });

        // onclick for the edit button
        btnEditTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent (MainActivity.this, EditTagActivity.class);
                if(photoGallery.size() > 0){
                    myIntent.putExtra("PHOTO_FULL_PATH", photoGallery.get(currentPhotoIndex));
                    startActivity(myIntent);
                }
            }
        });

        // onclick for the reset button
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // populate the photoGallery
                Date minDate = new Date(Long.MIN_VALUE);
                Date maxDate = new Date(Long.MAX_VALUE);
                photoGallery = populateGallery(minDate, maxDate);

                // if there's photo, display photo
                if(photoGallery.size() > 0){
                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                } else {
                    System.out.println("No Photos");
                }
            }
        });

        // populate photoGallery
        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoGallery = populateGallery(minDate, maxDate);

        // if there's photo, display photo
        if(photoGallery.size() > 0){
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            displayPhoto(currentPhotoPath);
        } else {
            System.out.println("No Photos");
        }
    }

    // return back to activity_main after search
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("back to the activity_main");

        //if we just finished a search, re-create the photoGallery
        if(storage.updateResult) {                                  // storage holds all the photos
            storage.updateResult = false;                           // make this false again, and wait for the next search
            photoGallery = new ArrayList<String>();                 // re-create the photoGallery arrayList<String>

            // finding all the matched images
            for(int i =0; i < storage.imageList.size(); i++) {                                  // loop through storage
                Image currentImage = storage.imageList.get(i);                                  // set currentImage
                System.out.println(currentImage.foundInSearch + " " + currentImage.Filename);   // foundInSearch is a boolean that shows if the image fulfills search query
                if(currentImage.foundInSearch) {                                                // if it fulfills
                    photoGallery.add(currentImage.Filename);                                    // add currentImage to the photoGallery
                }
            }

            // if there are no matching results, display default image, clear timestamp
            // else, display the first photo
            if(photoGallery.size() == 0) {
                System.out.println("---NO RESULTS FOUND---");
                imageView = findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.search_no_results);
                TextView timestamp_textView = findViewById(R.id.timestamp_textview);
                timestamp_textView.setText("");
            } else {
                imageView = findViewById(R.id.imageView);
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }

        // if we just finished taking a photo, display the latest photo
        if(displayimageaftercapture && photoGallery.size() > 0){
            currentPhotoIndex = photoGallery.size() - 1;
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            displayPhoto(currentPhotoPath);
        }
    }

    // display the timestamps in the textView
    private void setTimestamp(){
        TextView timestamp_textView = (TextView)findViewById(R.id.timestamp_textview);
        String temp = currentPhotoPath;
        String temp2;
        String timestamp;
        temp2 = temp.substring(temp.indexOf("JPEG_"), temp.indexOf(".jpg"));
        timestamp = temp2.substring(5,13);
        timestamp_textView.setText(timestamp + "\n");
        setTags();
    }

    // display the location tag in the textView
    private void setLocationTag(String pathToTags){
        TextView timestamp_textView = (TextView)findViewById(R.id.timestamp_textview);

        // get the location file
        String pathToLocation = pathToTags.replace(".txt", "_location.txt");

        // read the file
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(pathToLocation));
            String locationTag = null;
            locationTag = reader.readLine();

            timestamp_textView.append("\n" + locationTag);
            reader.close();
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", pathToLocation);
            e.printStackTrace();
            return;
        }
    }

    // display the tags in the textView
    private void setTags(){
        String tagFullPath;
        String pathFileFound = null;
        List<String> tagsToShow = new ArrayList<String>();;
        TextView tag_textView = findViewById(R.id.timestamp_textview);

        // converting the path to image to path to tags
        tagFullPath = photoGallery.get(currentPhotoIndex).replace("/files/Pictures/","/files/Documents/");
        tagFullPath = tagFullPath.replace(".jpg", ".txt"); // /storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/JPEG_20191010_160009_1849029762813521602.txt

        // open the directory
        File directory = new File("/storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/");
        File[] files = directory.listFiles();

        // find the correct file in the directory
        for(int i = 0; i < files.length; i++){
            String temp = files[i].getPath();
            if (temp.contains(tagFullPath)){
                pathFileFound = files[i].getAbsolutePath(); // /storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/JPEG_20191010_160009_1849029762813521602.txt
            }
        }

        // read the file
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(pathFileFound));
            String tag;
            while ((tag = reader.readLine()) != null)
            {
                tagsToShow.add(tag);
            }
            reader.close();
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", pathFileFound);
            e.printStackTrace();
            return;
        }
        tag_textView.append(tagsToShow.toString());
        setLocationTag(pathFileFound);
    }

    // get user's current latitude longitude from NETWORK.PROVIDER (alternative GPS.PROVIDER)
    // convert user's current lat lon to an address using Geocoder
    private String getLocation(){
        double lat = 0;
        double lon = 0;
        String location = "";
        try {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("no permission request 4 them");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else {
                System.out.println("I am here");
                Location test = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                lat = test.getLatitude();
                lon = test.getLongitude();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if(addresses != null) {
                    location = addresses.get(0).getAddressLine(0);
                }
            }
        } catch(Exception e) {

        }

        return location;
    }

    // creating the image and tag files and saving them
    private File createImageFile() throws IOException {

        // get uesr's address
        String imageLocation = getLocation();

        // create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir_image = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir_tag = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        // create image file
        File image = File.createTempFile(
                imageFileName,    /* prefix */
                ".jpg",     /* suffix */
                storageDir_image  /* directory */
        );

        // create tags file
        try {
            File file = new File(storageDir_tag, image.getName().replace(".jpg", ".txt"));
            FileWriter writer = new FileWriter(file);
            writer.append("");
            writer.flush();
            writer.close();
        } catch(Exception e) {

        }

        // create location file
        try {
            File file = new File(storageDir_tag, image.getName().replace(".jpg", "_location.txt"));
            FileWriter writer = new FileWriter(file);
            writer.append(imageLocation);
            writer.flush();
            writer.close();
        } catch(Exception e) {

        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println(currentPhotoPath);

        return image;
    }

    // intent for taking photos from camera app
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        displayimageaftercapture = true;

        // ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // create the File where the photo should go
            File photoFile = null;
            try {
                // eg. JPEG_20190925_232600_5043141561115288621.jpg
                photoFile = createImageFile();
            } catch (IOException ex) {
                // error occurred while creating the File
                Log.e("", "The File cannot be created.");
                System.out.println("The File cannot be created");
            }

            // continue only if the File was successfully created
            if (photoFile != null) {
                // makes link for JPEG_20190925_232600_5043141561115288621.jpg
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.newphotogalleryapp",
                        photoFile);
                this.currentPhotoPath = photoFile.toString();

                // hand over to camera app
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

            // populate photoGallery again
            Date minDate = new Date(Long.MIN_VALUE);
            Date maxDate = new Date(Long.MAX_VALUE);
            photoGallery = populateGallery(minDate, maxDate);
        }
    }

    // populate the photoGallery
    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.newphotogalleryapp/files/Pictures");
        photoGallery = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : file.listFiles()) {
                photoGallery.add(f.getPath());
            }
        }
        return photoGallery;
    }

    // display the photo and set the timestamp
    private void displayPhoto(String path) {
        String pathToDisplay;

        // somehow... the path is truncated after search...idk why...
        if( (path.charAt(0)) != '/' ){
            pathToDisplay = "/storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Pictures/" + path;
        } else {
            pathToDisplay = path;
        }

        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(pathToDisplay));
        setTimestamp();
    }
}
