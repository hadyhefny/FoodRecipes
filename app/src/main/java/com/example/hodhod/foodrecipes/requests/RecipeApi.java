package com.example.hodhod.foodrecipes.requests;

import com.example.hodhod.foodrecipes.requests.responses.ApiResponse;
import com.example.hodhod.foodrecipes.requests.responses.RecipeResponse;
import com.example.hodhod.foodrecipes.requests.responses.RecipeSearchResponse;

import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    //SEARCH
    @GET("api/search")
    LiveData<ApiResponse<RecipeSearchResponse>> searchRecipe(
      @Query("key") String key,
      @Query("q") String query,
      @Query("page") String page
    );

    //GET recipe request
    @GET("api/get")
    LiveData<ApiResponse<RecipeResponse>> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id
    );



}
