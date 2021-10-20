package com.example.multiplemarker.data.model.nearby;

import com.google.gson.annotations.SerializedName;

public class ModelGeometry {

    @SerializedName("location")
    private ModelLocation modelLocation;

    public ModelLocation getModelLocation() {
        return modelLocation;
    }

    public void setModelLocation(ModelLocation modelLocation) {
        this.modelLocation = modelLocation;
    }
}
