package com.OmnifyTask.retrofitClient;

import com.OmnifyTask.gsonModel.StoryItems;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by chavali on 2018-04-08.
 */

public interface ApiInterface {


    @GET("topstories.json")
    Call<ArrayList<Long>> getTopStories();

    @GET("item/{story_id}.json")
    Call<StoryItems> articledetailsData(@Path("story_id") String storyid);

}
