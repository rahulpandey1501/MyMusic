package com.rahul.mymusic.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rahul.mymusic.Adapter.BollywoodRecylerAdapter;
import com.rahul.mymusic.Helper.Constants;
import com.rahul.mymusic.Helper.Information;
import com.rahul.mymusic.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 14 Apr 2016.
 */
public class BollywoodFragment extends Fragment {
    private EditText searchBox;
    Button submitButton;
    public RecyclerView latestAlbumRecyclerView, searchRecyclerView;
    public ProgressBar progressBar, latestAlbumProgressBar;
    ImageView clearButton, hideArrow;
    List<Information> list;
    static boolean hideArrowState = true;
    LinearLayout searchLayout, latestAlbumLayout;

    public static BollywoodFragment newInstance() {
        BollywoodFragment fragment = new BollywoodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BollywoodFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bollywood_music_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeView(view);
        new LatestMovieAsyncTask().execute(Constants.BOLLYWOOD_MUSIC_URL);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitButton.performClick();
                    return true;
                }
                return false;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View focus = getActivity().getCurrentFocus();
                if (focus != null) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                }
                new BollywoodSearchAsyncTask().execute(getSearchLink(searchBox.getText().toString()));
            }
        });
        hideArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(200);
                rotate.setInterpolator(new LinearInterpolator());
                hideArrow.startAnimation(rotate);
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (hideArrowState) {
                            hideArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                            hideArrowState = false;
                            searchRecyclerView.setVisibility(View.GONE);
                        }else {
                            hideArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                            hideArrowState = true;
                            searchRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeView(View view) {
        submitButton = (Button) view.findViewById(R.id.submit_button);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        latestAlbumProgressBar = (ProgressBar) view.findViewById(R.id.latest_album_progress);
        latestAlbumRecyclerView = (RecyclerView) view.findViewById(R.id.latest_album_recycler_view);
        searchRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        clearButton = (ImageView) view.findViewById(R.id.clear_button);
        searchBox = (EditText) view.findViewById(R.id.search_box);
        hideArrow = (ImageView) view.findViewById(R.id.hide_arrow);
        searchLayout = (LinearLayout) view.findViewById(R.id.search_layout);
    }
    public class LatestMovieAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            latestAlbumProgressBar.setVisibility(View.VISIBLE);
            latestAlbumRecyclerView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            list = new ArrayList<>();
            try {
                Document document = Jsoup.connect(params[0])
                        .timeout(0)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                        .get();
                Elements elements = document.getElementsByClass("hm_song_inr").first().getElementsByClass("hm_song_col").get(2).getElementsByClass("song_rap").select("li");
                for (Element e : elements){
                    Information information = new Information();
                    information.title = e.select("a").text();
                    information.link = Constants.BOLLYWOOD_MUSIC_URL+e.select("a").attr("href");
                    list.add(information);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getContext(), list.size() + " Result(s) found", Toast.LENGTH_SHORT).show();
            latestAlbumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
            latestAlbumRecyclerView.setAdapter(new BollywoodRecylerAdapter(getContext(), list, false));
            latestAlbumRecyclerView.setVisibility(View.VISIBLE);
            latestAlbumProgressBar.setVisibility(View.GONE);
        }
    }


    public String getSearchLink(String query){
        return Constants.BOLLYWOOD_SONGS_SEARCH_URL+query.trim().replace(" ","+");
    }

    public class BollywoodSearchAsyncTask extends AsyncTask<String, Void, List<Information>> {

        @Override
        protected void onPreExecute() {
            searchLayout.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Information> doInBackground(String... params) {
            Log.d("url", params[0]);
            List<Information> list = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(0);
                con.connect();
                InputStream is = (InputStream) con.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null){
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int i=0; i<jsonArray.length(); ++i){
                    JSONObject jobj = jsonArray.getJSONObject(i);
                    Information information = new Information();
                    information.title = jobj.getString("titleNoFormatting");
                    information.link = jobj.getString("url");
                    if(information.link.contains(".mp3") )
                        information.isDownloadLink = true;
                    else information.isDownloadLink = false;
                    if (jobj.getJSONObject("richSnippet").has("metatags")) {
                        information.image = jobj.getJSONObject("richSnippet").getJSONObject("metatags").getString("ogImage");
                    }
                    else break;
                    list.add(information);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Information> list) {
            Toast.makeText(getContext(), list.size() + " Result(s) found", Toast.LENGTH_SHORT).show();
            hideArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
            hideArrowState = true;
            searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//            searchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
            searchRecyclerView.setAdapter(new BollywoodRecylerAdapter(getContext(), list, false));
            searchRecyclerView.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
