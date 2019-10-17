package com.example.newphotogalleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Models.SearchResults;
import Models.Image;

import static java.lang.Integer.parseInt;

public class SearchActivity extends AppCompatActivity {

    // <Image> is the model we created
    // String Filename; Date PhotoDate; Boolean foundInSearch
    private ArrayList<Image> images;
    private EditText fromDate;
    private EditText toDate;
    private Button btnCancel;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        System.out.println("Entered activity_search");

        fromDate = (EditText) findViewById(R.id.startDate);
        toDate   = (EditText) findViewById(R.id.endDate);
        images = new ArrayList<Image>();    // images is an ArrayList<Image>, not ArrayList<String>
        btnCancel = (Button)findViewById(R.id.button_cancel);
        btnSend = (Button)findViewById(R.id.button_search);

        // onlclick for cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        // onclick for search button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date startEndBound[] = new Date[2];
                Date dateStartBound = null;
                Date dateEndBound = null;
                ArrayList<String> tags = null;
                EditText tagkeyword = (EditText)findViewById(R.id.Tags);
                String keyword = tagkeyword.getText().toString();
                EditText locationKeyword = (EditText)findViewById(R.id.locationKeywordText);
                String keywordLocation = locationKeyword.getText().toString();
                SearchResults storage = new SearchResults();    // SearchResults  ArrayList<Image> imageList; boolean updateResult
                EditText startDate = (EditText)findViewById(R.id.startDate);
                EditText endDate = (EditText)findViewById(R.id.endDate);

                // set minimum input for date
                if(startDate.length() < 8 || endDate.length() < 8){
                    if(startDate.length() == 0 && endDate.length() == 0){
                        System.out.println("No Date Constraint");
                    } else {
                        System.out.println("Invalid input");
                        return;
                    }
                }

                // if date inputs are 8 digits
                if(startDate.length() != 0 && endDate.length() != 0){
                    // grab user input
                    String startDateString = startDate.getText().toString();
                    String endDateString = endDate.getText().toString();

                    // check for invalid input
                    checkInput(startDateString);
                    checkInput(endDateString);

                    System.out.println("User Input Start Date: " + startDateString);
                    System.out.println("User Input End Date: " + endDateString);

                    // set dateStartBound and dateEndBound
                    startEndBound = convertInputs(startDateString, endDateString);
                    dateStartBound = startEndBound[0];
                    dateEndBound = startEndBound[1];
                }

                // open file
                File directory = new File("/storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Pictures/");
                // create files to contain information
                File[] files = directory.listFiles();
                // check if files is empty
                if(files == null){
                    return;
                }
                // clear anything from previous search
                storage.imageList = new ArrayList<Image>();
                // look at all files in directory
                for(int i =0; i < files.length; i++) {
                    File currentFile = files[i];
                    Date currentFileDate = new Date(currentFile.lastModified());
                    String currentFileName = currentFile.getName();
                    storage.imageList.add(new Image(currentFileName, currentFileDate));     // storage.imageList is an ArrayList<Image>
                                                                                            // new Image(name, date) from Java library
                }

                // searching algorithm below
                for(int i =0; i < storage.imageList.size(); i++) {

                    tags = storage.GetTagData(storage.imageList.get(i).Filename);
                    Image currentImage = storage.imageList.get(i);
                    Boolean foundTag = false;
                    Boolean foundLocation = false;
                    Boolean noDate = false;

                    // if users left tag field blank, we treat it as every photo matches
                    // else, loop thru tags, and compare with the keyword user provided
                    // only work for search for 1 keyword
                    if(keyword == null || keyword.equals("")) {
                        foundTag = true;
                    }
                    else {
                        for(int j =0; j < tags.size(); j++) {
                            String currentTag = tags.get(j);
                            if(currentTag != null && currentTag.contentEquals(keyword) ) {
                                foundTag = true;
                            }
                        }
                    }

                    // if users left location field blank, we treat it as every photo matches
                    // else, compare the address with the keyword provided by users using .contains()
                    if(keywordLocation == null || keywordLocation.equals((""))) {
                        foundLocation = true;
                    }
                    else {
                        String lastLocation = storage.GetLocation(storage.imageList.get(i).Filename);
                        if(!lastLocation.equals("")) {
                            if(lastLocation.toLowerCase().contains(keywordLocation.toLowerCase())) {
                                System.out.println(lastLocation);
                                foundLocation = true;
                            }
                        }
                    }

                    // if users left dates blank, we treat it as every photo matches
                    if(dateStartBound == null || dateEndBound == null || dateStartBound.equals("") || dateEndBound.equals("")){
                        noDate = true;
                    }
                    // dateWithinRange checks if either no date constraints provideds by the user or images are within range
                    Boolean dateWithinRange = (noDate || (currentImage.PhotoDate.getTime() >= dateStartBound.getTime() && currentImage.PhotoDate.getTime() <= dateEndBound.getTime()));

                    // foundInSearch is a variable in each image model
                    // true only if ALL THREE booleans are true
                    if(dateWithinRange && foundTag && foundLocation) {
                        currentImage.foundInSearch = true;
                    }
                    else {
                        currentImage.foundInSearch = false;
                    }

                }

                // filter results, can be commented out, just for println
                System.out.println("Filtered Results below:");
                for(int i =0; i < storage.imageList.size(); i++) {
                    Image currentImage = storage.imageList.get(i);
                    if(currentImage.foundInSearch) {
                        System.out.println(i + " MATCHED: " + currentImage.Filename);
                    }
                    else {
                        System.out.println(i + " NOT MATCHED: " + currentImage.Filename);
                    }
                }

                // updateResult is a variable in SearchResult model
                // used to control if we are returning from a search, or from other intent (such as dispatchTakePictureIntent)
                storage.updateResult = true;
                finish();
            }
        });

    }

    private void checkInput(String date){
        // parse user input to year, month, day
        int Year = parseInt(date.substring(0,4));
        int Month = parseInt(date.substring(4,6));
        int Day = parseInt(date.substring(6,8));

        if(Month < 1 || Month > 12){
            System.out.println("Invalid Month");
            return;
        }
        if(Day < 1 || Day > 31){
            System.out.println("Invalid Day");
            return;
        }
        if(Month==4 || Month==6 || Month==9 || Month==11){
            if(Day > 30){
                System.out.println("Invalid Day");
                return;
            }
        }
        if(Month == 2){
            if (((Year % 4 == 0) && (Year % 100!= 0)) || (Year%400 == 0)){
                System.out.println(Year + "is a leap year.");
                if (Day > 29){
                    System.out.println("Invalid Day");
                    return;
                }
            } else {
                System.out.println(Year + "is not a leap year.");
                if(Day > 28) {
                    System.out.println("Invalid Day");
                    return;
                }
            }
        }
    }

    private Date[] convertInputs(String startDate, String endDate){
        Date[] dateBoundaries = new Date[2];
        Calendar c = Calendar.getInstance();
        int startYear = parseInt(startDate.substring(0,4));
        int startMonth = parseInt(startDate.substring(4,6));
        int startDay = parseInt(startDate.substring(6,8));
        c.set(startYear, startMonth - 1, startDay, 0, 0);
        dateBoundaries[0] = new Date(c.getTimeInMillis());
        int endYear = parseInt(endDate.substring(0,4));
        int endMonth = parseInt(endDate.substring(4,6));
        int endDay = parseInt(endDate.substring(6,8));
        c.set(endYear, endMonth - 1, endDay, 23, 59);
        dateBoundaries[1] = new Date(c.getTimeInMillis());
        return dateBoundaries;
    }

}
