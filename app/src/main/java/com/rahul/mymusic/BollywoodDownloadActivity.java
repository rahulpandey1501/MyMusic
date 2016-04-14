package com.rahul.mymusic;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.rahul.mymusic.Adapter.BollywoodRecylerAdapter;
import com.rahul.mymusic.Helper.BlurBuilder;
import com.rahul.mymusic.Helper.BollywoodDetailInformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class BollywoodDownloadActivity extends AppCompatActivity {

    BollywoodDetailInformation details;
    RecyclerView recyclerView;
    ImageView imageView;
    ListView albumDetailListView;
    View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bollywood_music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<small>"+getIntent().getStringExtra("title")+"</small>"));
        initializeView();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.listview_row, getListViewData());
        albumDetailListView.setAdapter(adapter);
        Picasso.with(this).load(details.image).into(imageView);
        Picasso.with(getApplicationContext()).load(details.image).placeholder(R.drawable.header).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                rootLayout.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(getApplicationContext(), bitmap)));
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new BollywoodRecylerAdapter(this, details.downloadList, true));
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String[] getListViewData() {
        List<String> list = new ArrayList<>();
        for (String key : details.albumDetail.keySet()){
            list.add(key + " " + details.albumDetail.get(key));
        }
        String albumDetailArray[] = new String[list.size()];
        list.toArray(albumDetailArray);
        return albumDetailArray;
    }

    public  void initializeView() {
        details = (BollywoodDetailInformation) getIntent().getSerializableExtra("details");
        imageView = (ImageView) findViewById(R.id.image_view);
        albumDetailListView = (ListView) findViewById(R.id.list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rootLayout = findViewById(R.id.root_layout);
    }
}
