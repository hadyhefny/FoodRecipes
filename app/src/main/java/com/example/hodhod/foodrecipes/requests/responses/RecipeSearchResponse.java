package com.example.hodhod.foodrecipes.requests.responses;

import com.example.hodhod.foodrecipes.models.Recipe;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeSearchResponse {

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("error")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    @SerializedName("recipes")
    @Expose
    private List<Recipe> recipes;

    public int getCount() {
        return count;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }


    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", error='" + error + '\'' +
                ", recipes=" + recipes +
                '}';
    }
}
