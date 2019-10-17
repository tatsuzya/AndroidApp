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

        List<String> tags;
        EditText tagText = findViewById(R.id.editTag);
        Button btnSend = findViewById(R.id.button_send_tag);

        // getExtras() used for getting information from previous intent
        // we need to know which image's tag to edit, therefore
        // we used getExtras() to pass the PHOTO_FULL_PATH to this intent
        if (savedInstanceState == null){
            extras = getIntent().getExtras();
            if (extras == null){
                System.out.println("extras is null.");
            } else {
                photoFullPath = extras.getString("PHOTO_FULL_PATH");
            }
        }

        tagFullPath = convertPath(photoFullPath);
        tagPath = compareAndFind(tagFullPath);
        tags = openAndRead(tagPath);

        // displaying the tags in the file to the editText tagText
        for(int i=0; i<tags.size(); i++){
            tagText.append(System.getProperty("line.separator"));
            tagText.append(tags.get(i));
        }

        // onclick for the Send button
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

    // converting photoFullPath to tagFullPath
    // from: /storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Pictures/JPEG_20191016_172849_1763023278466420925.jpg
    //   to: /storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/JPEG_20191016_172849_1763023278466420925.txt
    private String convertPath(String photoFullPath){
        String tagFullPath;
        tagFullPath = photoFullPath.replace("/files/Pictures/","/files/Documents/");
        tagFullPath = tagFullPath.replace(".jpg", ".txt");
        return tagFullPath;
    }

    // find the file we want to edit
    private String compareAndFind (String fileToFind){
        String fileName = null;
        // Open the directory
        File directory = new File("/storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/");
        File[] files = directory.listFiles();
        for(int i = 0; i < files.length; i++){
            String temp = files[i].getPath();
            if (temp.contains(fileToFind)){
                fileName = files[i].getAbsolutePath();
            }
        }
        return fileName;   // JPEG_20191010_160009_1849029762813521602.txt
    }

    // open the file and read its content
    private List<String> openAndRead(String fileName){
        List<String> tags = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
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
            System.err.format("Exception occurred trying to read '%s'.", fileName);
            e.printStackTrace();
            return null;
        }
    }

    // save the content in the EditText to the tag file
    private void saveTags(String tagPath) throws IOException{
        EditText tagText = findViewById(R.id.editTag);
        System.out.println(tagText.getText());
        String tags_buffer = null;
        BufferedWriter out = null;
        tags_buffer = tagText.getText().toString();
        try {
            FileWriter fstream = new FileWriter(tagPath, false); // true tells to append data.
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
