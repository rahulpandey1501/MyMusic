package com.rahul.mymusic.Helper;

/**
 * Created by Rahul on 10 Apr 2016.
 */
public class Constants {
    public static final String SEARCH_QUERY_PREFIX = "http://suggestqueries.google.com/complete/search?hl=en&client=youtube&q=";
    public static final String FETCH_SEARCH_QUERY_URL = "http://www.youzik.com/";
    public static final String VIDEO_TO_MP3_SERVER_1 = "http://www.youtubeinmp3.com/fetch/?video=";
    public static final String VIDEO_TO_MP3_SERVER_2 = "http://www.youzik.com/application/core/server.php?id=";
    public static final String BOLLYWOOD_SONGS_SEARCH_URL = "https://www.googleapis.com/customsearch/v1element?" +
            "key=AIzaSyCVAXiUzRYsML1Pv6RwSG1gunmMikTzQqY&rsz=filtered_cse&" +
            "num=10&hl=en&prettyPrint=false&source=gcsc&gss=.in&" +
            "sig=432dd570d1a386253361f581254f9ca1&cx=015279129310856890483:oz1lrhqkxro&" +
            "sort=&googlehost=www.google.com&q=";
    public static final String BOLLYWOOD_MUSIC_URL = "http://songspk.city";
    public static final String[] IGNORE_LIST = {"writter", "size", "bitrate", "composer"};
}
