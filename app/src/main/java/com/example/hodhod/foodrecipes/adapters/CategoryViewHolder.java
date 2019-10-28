package com.example.hodhod.foodrecipes.adapters;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.hodhod.foodrecipes.R;
import com.example.hodhod.foodrecipes.models.Recipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    CircleImageView mCategoryImageView;
    TextView mCategoryTitle;
    OnRecipeListener mOnRecipeListener;

    private RequestManager requestManager;

    public CategoryViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener, RequestManager requestManager) {
        super(itemView);

        this.mOnRecipeListener = onRecipeListener;
        this.requestManager = requestManager;

        mCategoryImageView = itemView.findViewById(R.id.category_image);
        mCategoryTitle = itemView.findViewById(R.id.category_title);

        itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe){

        Uri path = Uri.parse("android.resource://com.example.hodhod.foodrecipes/drawable/" + recipe.getImage_url());

        requestManager
                .load(path)
                .into(mCategoryImageView);

        mCategoryTitle.setText(recipe.getTitle());
    }

    @Override
    public void onClick(View v) {
        mOnRecipeListener.onCategoryClick(mCategoryTitle.getText().toString());
    }
}
