package com.rahul.mymusic;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rahul.mymusic.Fragment.BollywoodFragment;
import com.rahul.mymusic.Fragment.YoutubeSearchFragment;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        setViewPagerAdapter();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setViewPagerAdapter() {
        mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mTabLayout.setTabsFromPagerAdapter(mTabPagerAdapter);
        mViewPager.setAdapter(mTabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    class TabPagerAdapter extends FragmentStatePagerAdapter {

        String link;
        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
            this.link = link;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return BollywoodFragment.newInstance();
                case 1:
                    return YoutubeSearchFragment.newInstance();
                default:
                    return YoutubeSearchFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Bollywood";
                case 1:
                    return "Youtube Search";
                default:
                    return "All";
            }
        }
    }
}
