package com.OmnifyTask.activites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.OmnifyTask.R;
import com.OmnifyTask.adapter.StoryListAdapter;
import com.OmnifyTask.databaseTable.StoryDetails;
import com.OmnifyTask.databaseTable.TopStoryList;
import com.OmnifyTask.gsonModel.StoryItems;
import com.OmnifyTask.retrofitClient.ApiClient;
import com.OmnifyTask.retrofitClient.ApiInterface;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleListView extends AppCompatActivity {


    private static final String UNIQUE_ID = "UNIQUE_ID";
    int start_count = 0, limit_count = 10;
    RecyclerView article_Recycler;
    private Dialog dialog;
    private ProgressBar mProgressView;
    private Realm myRealm;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list_view);

        myRealm = Realm.getDefaultInstance();

        article_Recycler = findViewById(R.id.article_Recycler);

        final RealmResults<TopStoryList> results = myRealm.where(TopStoryList.class).findAll();
        RealmResults<StoryDetails> storyResult = myRealm.where(StoryDetails.class).findAll();


        if (storyResult.size() != 0) {
            // get the size of the list in the story
            start_count = storyResult.size();
            limit_count = start_count + 10;
        } else {
            start_count = 0;
        }

        if (results.size() == 0) {
//            clearDB();
            //call the Api to load the TOP storeis Api
            Topstoies();
        } else {
            accessdatabase();
        }


    }

    public void Topstoies() {

        //call the Api to load the TOP storeis Api

        // show progressbar wile loading and hide after conpetion
        showProgressBar();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<Long>> topstories = apiService.getTopStories();

        topstories.enqueue(new Callback<ArrayList<Long>>() {
            @Override
            public void onResponse(Call<ArrayList<Long>> call, Response<ArrayList<Long>> response) {

                hideProgress();

                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); i++) {
                        myRealm.beginTransaction();
                        TopStoryList storesid = myRealm.createObject(TopStoryList.class);
                        storesid.setTopstoryid(response.body().get(i));
                        myRealm.commitTransaction();
                    }
                    RealmResults<TopStoryList> results = myRealm.where(TopStoryList.class).findAll();

                    accessdatabase();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Long>> call, Throwable t) {

                hideProgress();
            }
        });

    }


    public void ArticleDetials(String id) {

        // calling the api to load the data from the ids from the topstoreis and calling single id and storing the in the database
//        showProgressBar();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<StoryItems> atricledetails = apiService.articledetailsData(id);

        atricledetails.enqueue(new Callback<StoryItems>() {
            @Override
            public void onResponse(Call<StoryItems> call, Response<StoryItems> response) {

//                hideProgress();
                if (response.isSuccessful()) {
                    myRealm.beginTransaction();

                    StoryDetails storydetails = myRealm.createObject(StoryDetails.class);

                    if (response.body().getBy() != null) {
                        storydetails.setBy(response.body().getBy());
                    }

                    if (response.body().getDescendants() != null) {
                        storydetails.setDescendants(response.body().getDescendants());
                    }

                    if (response.body().getId() != null) {
                        storydetails.setId(response.body().getId());
                    }

                    if (response.body().getKids() != null) {

                        RealmList<Long> comments = new RealmList<>();

                        for (int i = 0; i < response.body().getKids().size(); i++) {

                            comments.add(response.body().getKids().get(i));
                        }

                        storydetails.setComments(comments);
                    }

                    if (response.body().getScore() != null) {
                        storydetails.setScore(response.body().getScore());
                    }

                    if (response.body().getTime() != null) {
                        storydetails.setTime(response.body().getTime());
                    }

                    if (response.body().getTitle() != null) {
                        storydetails.setTitle(response.body().getTitle());
                    }

                    if (response.body().getType() != null) {
                        storydetails.setType(response.body().getType());
                    }

                    if (response.body().getUrl() != null) {
                        storydetails.setUrl(response.body().getUrl());
                    }

                    myRealm.commitTransaction();

                }

            }

            @Override
            public void onFailure(Call<StoryItems> call, Throwable t) {
//                hideProgress();

            }
        });
    }

    public void clearDB() {

        // to clear the database command
        myRealm = Realm.getDefaultInstance();
        myRealm.beginTransaction();
        myRealm.delete(TopStoryList.class);
        myRealm.commitTransaction();
        myRealm.close();
    }

    private void accessdatabase() {

        //This wil help to access data from stored database

        RealmResults<TopStoryList> results = myRealm.where(TopStoryList.class).findAll();

        showProgressBar();
        for (int i = start_count; i < limit_count; i++) {

            ArticleDetials(results.get(i).getTopstoryid() + "");

        }

        hideProgress();

        RealmResults<StoryDetails> storyResult = myRealm.where(StoryDetails.class).findAll();
        Log.e("storyResult", String.valueOf(storyResult.size()));

        RealmList<StoryDetails> list = new RealmList<>();
        list.addAll(storyResult);

        StoryListAdapter madpter = new StoryListAdapter(ArticleListView.this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        article_Recycler.setLayoutManager(mLayoutManager);
        article_Recycler.setItemAnimator(new DefaultItemAnimator());
        article_Recycler.setAdapter(madpter);

        madpter.SetOnItemClickListener(new StoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String articleId) {
                Log.e("sadsads", String.valueOf(articleId));

                Intent articledetail = new Intent(ArticleListView.this, ArticleDetailView.class);
                articledetail.putExtra("articleId", articleId);
                startActivity(articledetail);
            }


        });

    }

    private void showProgressBar() {

        dialog = new Dialog(ArticleListView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressbar_layout);
        dialog.setCancelable(false);
        mProgressView = (ProgressBar) dialog.findViewById(R.id.progress_dialog);
        mProgressView.setVisibility(View.VISIBLE);
        dialog.show();

    }

    private void hideProgress() {

        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
            mProgressView.setVisibility(View.GONE);
        }
    }


}
