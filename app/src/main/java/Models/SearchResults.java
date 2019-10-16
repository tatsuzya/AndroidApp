package Models;

import android.provider.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SearchResults {
    public static ArrayList<Image> imageList =  new ArrayList<Image>();
    public static boolean updateResult = false;

    public static ArrayList<String> GetTagData(String fileName) {
        ArrayList<String> tagsToShow  = new ArrayList<String>();
        fileName = fileName.replace(".jpg", ".txt");
        File directory = new File("/storage/emulated/0/Android/data/com.example.newphotogalleryapp/files/Documents/");
        File[] files = directory.listFiles();
        for(int i = 0; i < files.length; i++){
            String currentFile = files[i].getName();
            System.out.println(currentFile);
            if (currentFile.contains(fileName)){
                try
                {
                    System.out.println("wtf");
                    BufferedReader reader = new BufferedReader(new FileReader(files[i]));
                    String tag;
                    while ((tag = reader.readLine()) != null)
                    {
                        tagsToShow.add(tag);
                    }
                    reader.close();
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
        return tagsToShow;
    }
}
