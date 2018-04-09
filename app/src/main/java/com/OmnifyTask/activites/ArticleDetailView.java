package com.OmnifyTask.activites;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.OmnifyTask.R;
import com.OmnifyTask.databaseTable.StoryDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

public class ArticleDetailView extends AppCompatActivity {

    TabLayout tabLayout;
    TextView title, url, time, author;
    String articleId;
    ArrayList<String> title_tab = new ArrayList<>();
    String data_Url = "";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Realm myRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail_view);
        myRealm = Realm.getDefaultInstance();

        inti();

        if (getIntent().hasExtra("articleId")) {
            articleId = getIntent().getStringExtra("articleId");
            Log.e("\"articleId\"", articleId);
            searchDatabse(articleId);
        }


    }


    public void searchDatabse(String id) {

        // get the data from the database stored from the preovioud screen using article id
        title_tab.add("Comments");

        Long articaleid = Long.valueOf(id);
        StoryDetails story = myRealm.where(StoryDetails.class).equalTo("id", articaleid).findFirst();

        title.setText(story.getTitle());

        if (story.getUrl() != null) {
            url.setText(story.getUrl());
            title_tab.add("Article");
            data_Url = story.getUrl();
        } else {
            url.setVisibility(View.GONE);
        }
        author.setText(story.getBy());
        // convert seconds to milliseconds
        Date date = new java.util.Date(story.getTime() * 1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        //        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+5:30"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);

        time.setText(formattedDate);

        // dynamically laod the tilte of the tabs in case the url is not present it will hide the data
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), title_tab);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public void inti() {

        title = findViewById(R.id.title);
        url = findViewById(R.id.url);
        time = findViewById(R.id.time);
        author = findViewById(R.id.author);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<String> title;

        // dynamically laod the tilte of the tabs in case the url is not present it will hide the data

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> title) {
            super(fm);
            this.title = title;
        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return PlaceholderFragment.newInstance(position + 1);


                case 1:
                    return AtricleUrl.newInstance(data_Url);

                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return title.get(position).toString();
        }
    }
}
