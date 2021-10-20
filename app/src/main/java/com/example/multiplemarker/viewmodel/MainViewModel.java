package com.example.multiplemarker.viewmodel;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.multiplemarker.data.model.nearby.ModelResult;
import com.example.multiplemarker.data.model.response.ModelResultNearby;
import com.example.multiplemarker.networking.ApiClient;
import com.example.multiplemarker.networking.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ModelResult>> modelResultsMutableLiveData = new MutableLiveData<>();
    public static String strApiKey = "AIzaSyC5evqQOcIDmaxGpUoCygNxyFIN3LUqWGM";

    public void setMarkerLocation(String strLocation, String strKeyword) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ModelResultNearby> call = apiService.getDataResult(strApiKey, strKeyword, strLocation, "distance");
        Log.d(TAG, "setMarkerLocation: strapi" + strApiKey);
        Log.d(TAG, "setMarkerLocation: strapi" + strKeyword);
        Log.d(TAG, "setMarkerLocation: strapi" + strLocation);
        call.enqueue(new Callback<ModelResultNearby>() {
            @Override
            public void onResponse(Call<ModelResultNearby> call, Response<ModelResultNearby> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: ");
                    Log.e("response", response.toString());
                } else if (response.body() != null) {
                    ArrayList<ModelResult> items = new ArrayList<>(response.body().getModelResults());
                    modelResultsMutableLiveData.postValue(items);
                    Log.d(TAG, "onResponse: response" + response.body().getModelResults().toString());
                    Log.d(TAG, "onResponse: response" + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ModelResultNearby> call, Throwable t) {
                Log.e("failure", t.toString());
                t.printStackTrace();
                Log.d(TAG, "onFailure: failure" + t.getMessage());
            }
        });
    }

    public LiveData<ArrayList<ModelResult>> getMarkerLocation() {
        return modelResultsMutableLiveData;
    }

}
