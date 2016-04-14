package com.rahul.mymusic.Helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rahul on 14 Apr 2016.
 */
public class BollywoodDetailInformation implements Serializable{
    public HashMap<String , String > artist, albumDetail;
    public List<Information> downloadList;
    public String image, title;
}
