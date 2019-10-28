package com.example.hodhod.foodrecipes.viewmodels;

import android.app.Application;

import com.example.hodhod.foodrecipes.models.Recipe;
import com.example.hodhod.foodrecipes.repositories.RecipeRepository;
import com.example.hodhod.foodrecipes.util.Resource;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId) {
        return mRecipeRepository.searchRecipesApi(recipeId);
    }
}