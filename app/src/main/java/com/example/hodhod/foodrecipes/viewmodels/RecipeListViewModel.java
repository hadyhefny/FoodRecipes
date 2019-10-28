package com.example.hodhod.foodrecipes.viewmodels;

import android.app.Application;
import android.util.Log;

import com.example.hodhod.foodrecipes.models.Recipe;
import com.example.hodhod.foodrecipes.repositories.RecipeRepository;
import com.example.hodhod.foodrecipes.util.Resource;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    public static final String QUERY_EXHAUSTED = "No more results.";


    public enum ViewState {CATEGORIES, RECIPES}

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();
    private RecipeRepository mRecipeRepository;

    // query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init() {
        if (viewState == null) {
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return recipes;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setViewCategories() {
        viewState.setValue(ViewState.CATEGORIES);
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeSearch();
        }
    }

    public void searchNextPage() {
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++;
            executeSearch();
        }
    }

    private void executeSearch() {
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositoryResource = mRecipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositoryResource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                if (!cancelRequest) {
                    if (listResource != null) {
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + ((System.currentTimeMillis()) - requestStartTime) / 1000 + " seconds");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is exhausted");
                                    recipes.setValue(new Resource<List<Recipe>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED));
                                    isQueryExhausted = true;
                                }
                            }
                            recipes.removeSource(repositoryResource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + ((System.currentTimeMillis()) - requestStartTime) / 1000 + " seconds");
                            isPerformingQuery = false;
                            if (listResource.message.equals(QUERY_EXHAUSTED)) {
                                isQueryExhausted = true;
                            }
                            recipes.removeSource(repositoryResource);
                        }
                        recipes.setValue(listResource);
                    } else {
                        recipes.removeSource(repositoryResource);
                    }
                } else {
                    recipes.removeSource(repositoryResource);
                }
            }
        });
    }

    public void cancelSearchRequest() {
        if (isPerformingQuery) {
            Log.d(TAG, "cancelSearchRequest: canceling the request");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }

}
