package com.example.hodhod.foodrecipes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.hodhod.foodrecipes.adapters.OnRecipeListener;
import com.example.hodhod.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.example.hodhod.foodrecipes.models.Recipe;
import com.example.hodhod.foodrecipes.util.Resource;
import com.example.hodhod.foodrecipes.util.VerticalSpacingItemDecorator;
import com.example.hodhod.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.hodhod.foodrecipes.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecipeRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        //instantiation of the view model
        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);


        initRecyclerView();
        initSearchView();
        subscribeObservers();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    }

    private void subscribeObservers() {

        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);
                    if (listResource.data != null) {
                        switch (listResource.status) {

                            case LOADING: {
                                if (mRecipeListViewModel.getPageNumber() > 1) {
                                    mAdapter.displayLoading();
                                } else {
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case ERROR: {
                                Log.e(TAG, "onChanged: cannot refresh the cache");
                                Log.e(TAG, "onChanged: Error message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #recipes" + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();
                                if (listResource.message.equals(QUERY_EXHAUSTED)) {
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            }
                            case SUCCESS: {
                                Log.d(TAG, "onChanged: cache has been refreshed");
                                Log.d(TAG, "onChanged: status: SUCCESS, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                break;
                            }

                        }
                    }
                }
            }
        });

        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(RecipeListViewModel.ViewState viewState) {

                if (viewState != null) {
                    switch (viewState) {
                        case RECIPES: {
                            // recipes will show automatically from another observer
                            break;
                        }
                        case CATEGORIES: {
                            displaySearchCategories();
                            break;
                        }
                    }
                }

            }
        });
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);
        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloadSizeProvider = new ViewPreloadSizeProvider<>();
        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloadSizeProvider);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewPreloader<String> recyclerViewPreloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mAdapter,
                viewPreloadSizeProvider,
                30);

        mRecyclerView.addOnScrollListener(recyclerViewPreloader);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!mRecyclerView.canScrollVertically(1)
                            && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPES) {
                        mRecipeListViewModel.searchNextPage();
                        Log.d(TAG, "onScrollChanged: pagination... + sdk above 23");
                    }
                }
            });

        } else {
            mRecyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    // search for the next page
                    if (!mRecyclerView.canScrollVertically(1)) {
                        mRecipeListViewModel.searchNextPage();
                        Log.d(TAG, "onScrollChanged: pagination... + sdk below 23");
                    }
                }
            });
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void searchRecipesApi(String query) {
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    private void initSearchView() {

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipesApi(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);

    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipesApi(category);
    }

    private void displaySearchCategories() {
        mAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES) {
            super.onBackPressed();
        } else {
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }
    }
}
