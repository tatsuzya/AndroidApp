package com.example.newphotogalleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditTagActivity extends AppCompatActivity  {

    String tagPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);

        Bundle extras;
        String photoFullPath = null;
        String tagFullPath = null;
        String tagTruncatedPath = null;

        List<String> tags;
        EditText tagText = findViewById(R.id.editTag);
        Button btnSend = findViewById(R.id.button_send_tag);

        if (savedInstanceState == null){
            extras = getIntent().getExtras();
            if (extras == null){
                System.out.println("extras is null.");
            } else {
                photoFullPath = extras.getString("PHOTO_FULL_PATH");
            }
        }
        tagFullPath = convertPath(photoFullPath);
        //tagTruncatedPath = truncateLastPart(tagFullPath);
        tagPath = compareAndFind(tagFullPath);
        tags = openAndRead(tagPath);
        // displaying the tags in the file to the editText tagText
        for(int i=0; i<tags.size(); i++){
            tagText.append(System.getProperty("line.separator"));
            tagText.append(tags.get(i));
        }



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EditTagActivity.this, MainActivity.class);
                try {
                    saveTags(tagPath);
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
                startActivity(myIntent);
            }
        });

    }

    private String convertPath(String photoFullPath){
        String tagFullPath;
        tagFullPath = photoFullPath.replace("/files/Pictures/","/files/Documents/");
        tagFullPath = tagFullPath.replace(".jpg", ".txt");
        return tagFullPath; // /storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/
    }

    private String truncateLastPart(String tagFullPath){
        String tagTruncatedPath;
        tagTruncatedPath = tagFullPath.substring(tagFullPath.indexOf("JPEG_"), tagFullPath.indexOf(".txt"));
        tagTruncatedPath = tagTruncatedPath.substring(0,20);
        System.out.println(tagTruncatedPath);
        return tagTruncatedPath;    // JPEG_20191010_160009
    }

    private String compareAndFind (String fileToFind){
        String pathFileFound = null;
        // Open the directory
        File directory = new File("/storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/");
        File[] files = directory.listFiles();
        for(int i = 0; i < files.length; i++){
            String temp = files[i].getPath();
            if (temp.contains(fileToFind)){
                pathFileFound = files[i].getAbsolutePath();
                System.out.println("I FOUND IT BITCHES!");
            }
        }
        return pathFileFound;   // JPEG_20191010_160009_1849029762813521602.txt
    }

    private List<String> openAndRead(String filename){
        List<String> tags = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String tag;
            while ((tag = reader.readLine()) != null)
            {
                tags.add(tag);
            }
            reader.close();
            return tags;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    private void saveTags(String tagPath) throws IOException{
        EditText tagText = findViewById(R.id.editTag);
        System.out.println(tagText.getText());
        String tags_buffer = null;
        BufferedWriter out = null;
        tags_buffer = tagText.getText().toString();
        try {
            FileWriter fstream = new FileWriter(tagPath, false); //true tells to append data.
            out = new BufferedWriter(fstream);
            out.write(tags_buffer);
        }

        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        finally {
            if(out != null) {
                out.close();
            }
        }

    }

}
