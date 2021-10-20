package com.example.multiplemarker.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.example.multiplemarker.data.model.nearby.ModelResult;
import java.util.List;

public class ModelResultNearby {

    @SerializedName("results")
    private List<ModelResult> modelResults;

    public List<ModelResult> getModelResults() {
        return modelResults;
    }

    public void setModelResults(List<ModelResult> modelResults) {
        this.modelResults = modelResults;
    }
}
