package com.rahul.mymusic;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rahul.mymusic.Adapter.YoutubeRecyclerAdapter;
import com.rahul.mymusic.Helper.Constants;
import com.rahul.mymusic.Helper.Information;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 10 Apr 2016.
 */
public class YoutubeSongsListAsyncTask extends AsyncTask<String, Void, String> {

    Context mContext;
    List<Information> list;
    RecyclerView recyclerView;
    View view;
    public YoutubeSongsListAsyncTask(Context context, View view){
        mContext = context;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        list = new ArrayList<>();
        view.findViewById(R.id.recycler_view).setVisibility(View.GONE);
        view.findViewById(R.id.submit_button).setVisibility(View.GONE);
        view.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Document document = Jsoup.connect(Constants.FETCH_SEARCH_QUERY_URL)
                    .timeout(0)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                    .data("search", params[0])
                    .post();
            Elements links = document.getElementsByClass("results");
            for (Element link : links){
                if (link.getElementsByClass("playVideo").size() != 0) {
                    Information information = new Information();
                    information.link = "http://"+link.select("div.playVideo").select("a").attr("href");
                    information.title = link.select("*[class^=opacity]").first().ownText().replace("\"", "");
                    information.duration = link.select("*[class^=opacity]").get(1).text();
                    information.duration = information.duration.replace("Duration • ", "");
                    String image = link.select("div").attr("style");
                    image = image.substring(image.indexOf("(")+1, image.indexOf(")"));
                    information.image = image;
                    list.add(information);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(mContext, list.size() + " Result(s) found", Toast.LENGTH_LONG).show();
//        view.findViewById(R.id.item_count).setText(list.size() + " Result(s)");
        view.findViewById(R.id.submit_button).setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        Log.d("list", list.size() + "");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new YoutubeRecyclerAdapter(mContext, list));
        recyclerView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressbar).setVisibility(View.GONE);
        super.onPostExecute(s);
    }
}
