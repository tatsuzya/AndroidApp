package Models;

import java.util.Date;
import java.io.Serializable;

public class Image implements Serializable{
    public String Filename;
    public Date PhotoDate;
    public Boolean foundInSearch;
    public Image(String _fileName, Date _photoDate) {
        this.foundInSearch = true;
        this.PhotoDate = _photoDate;
        this.Filename = _fileName;
    }
}
