package com.example.comp7082_assignment1;

import java.util.Date;

public class Image {
    public String Filename;
    public Date PhotoDate;
    public Boolean foundInSearch;
    public Image(String _fileName, Date _photoDate) {
        this.foundInSearch = true;
        this.PhotoDate = _photoDate;
        this.Filename = _fileName;
    }
}
