package com.example.multiplemarker.networking;

import com.example.multiplemarker.data.model.response.ModelResultNearby;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("place/nearbysearch/json")
    Call<ModelResultNearby> getDataResult(@Query("key") String key,
                                          @Query("keyword") String keyword,
                                          @Query("location") String location,
                                          @Query("rankby") String rankby);

}

