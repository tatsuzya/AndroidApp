package com.example.comp7082_assignment1;

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

import static java.lang.Integer.parseInt;

public class SearchActivity extends AppCompatActivity {

    ArrayList<Image> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        images = new ArrayList<Image>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button btnCancel = (Button)findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        System.out.println("wtf1");
        System.out.println("wtf2");
        System.out.println("wtf3");

        Button btnSend = (Button)findViewById(R.id.button_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText startDate = (EditText)findViewById(R.id.startDate);
                EditText endDate = (EditText)findViewById(R.id.endDate);
                int startYear, startMonth, startDay;
                int endYear, endMonth, endDay;

                String startDateString = startDate.getText().toString();
                String endDateString = endDate.getText().toString();

                startYear = parseInt(startDateString.substring(0,4));
                startMonth = parseInt(startDateString.substring(4,6));
                startDay = parseInt(startDateString.substring(6,8));

                endYear = parseInt(endDateString.substring(0,4));
                endMonth = parseInt(endDateString.substring(4,6));
                endDay = parseInt(endDateString.substring(6,8));

                if(startMonth < 1 || startMonth > 12){
                    System.out.println("Invalid Month");
                    return;
                }
                if(startDay < 1 || startDay > 31){
                    System.out.println("Invalid Day");
                    return;
                }
                if(startMonth==4 || startMonth==6 || startMonth==9 || startMonth==11){
                    if(startDay > 30){
                        System.out.println("Invalid Day");
                        return;
                    }
                }
                if(startMonth == 2){
                    if (((startYear % 4 == 0) && (startYear % 100!= 0)) || (startYear%400 == 0))
                        System.out.println(startYear + "is a leap year.");
                    if(startDay > 29){
                        System.out.println("Invalid Day");
                        return;
                    }
                    else
                        System.out.println(startYear + "is not a leap year.");
                    if(startDay > 28){
                        System.out.println("Invalid Day");
                        return;
                    }
                }

                if(endMonth < 1 || endMonth > 12){
                    System.out.println("Invalid Month");
                    return;
                }
                if(endDay < 1 || endDay > 31){
                    System.out.println("Invalid Day");
                    return;
                }
                if(endMonth==4 || endMonth==6 || endMonth==9 || endMonth==11){
                    if(endDay > 30){
                        System.out.println("Invalid Day");
                        return;
                    }
                }
                if(endMonth == 2){
                    if (((endYear % 4 == 0) && (endYear % 100!= 0)) || (endYear%400 == 0))
                        System.out.println(endYear + "is a leap year.");
                    if(endDay > 29){
                        System.out.println("Invalid Day");
                        return;
                    }
                    else
                        System.out.println(endYear + " is not a leap year.");
                    if(endDay > 28){
                        System.out.println("Invalid Day");
                        return;
                    }
                }

                System.out.println(startYear + " " + startMonth + " " + startDay);
                System.out.println(endYear + " " + endMonth + " " + endDay);
                System.out.println("here");

                Calendar c = Calendar.getInstance();
                c.set(startYear, startMonth - 1, startDay, 0, 0);
                Date dateStartBound = new Date(c.getTimeInMillis());
                c.set(endYear, endMonth - 1, endDay, 0, 0);
                Date dateEndBound = new Date(c.getTimeInMillis());

                System.out.println(dateStartBound);
                System.out.println(dateEndBound);

                File directory = new File("/storage/emulated/0/Android/data/com.example.comp7082_assignment1/files/Pictures/");
                File[] files = directory.listFiles();
                //look at all files in directory
                for(int i =0; i < files.length; i++) {
                    File currentFile = files[i];
                    Date currentFileDate = new Date(currentFile.lastModified());
                    images.add(new Image(currentFile.getName(), currentFileDate));
                }
                //find all that match the date range
                for(int i =0; i < images.size(); i++) {
                    Image currentImage = images.get(i);
                    if(currentImage.PhotoDate.getTime() >= dateStartBound.getTime() && currentImage.PhotoDate.getTime() <= dateEndBound.getTime()) {
                        currentImage.foundInSearch = true;
                    }
                    else {
                        currentImage.foundInSearch = false;
                    }
                }
                //filter results
                for(int i =0; i < images.size(); i++) {
                    Image currentImage = images.get(i);
                    if(currentImage.foundInSearch) {
                        System.out.println(currentImage.Filename);
                    } else {
                        System.out.println("No results found");
                    }
                }
            }
        });
    }
}
