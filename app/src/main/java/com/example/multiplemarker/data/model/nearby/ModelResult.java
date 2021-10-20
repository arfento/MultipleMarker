package com.example.multiplemarker.data.model.nearby;

import com.google.gson.annotations.SerializedName;

public class ModelResult {

    @SerializedName("geometry")
    private ModelGeometry modelGeometry;

    @SerializedName("name")
    private String name;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("rating")
    private double rating;

    public ModelGeometry getModelGeometry() {
        return modelGeometry;
    }

    public void setModelGeometry(ModelGeometry modelGeometry) {
        this.modelGeometry = modelGeometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
