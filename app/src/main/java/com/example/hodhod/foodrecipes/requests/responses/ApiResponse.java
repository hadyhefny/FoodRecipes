package com.example.hodhod.foodrecipes.requests.responses;

import com.example.hodhod.foodrecipes.viewmodels.RecipeListViewModel;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error) {
        return new ApiErrorResponse<>(error.getMessage().equals("") ? error.getMessage() : "Unknown Error\nCheck network connection");
    }

    public ApiResponse<T> create(Response<T> response) {

        if (response.isSuccessful()) {
            T body = response.body();

            if (body instanceof RecipeSearchResponse) {
                if (!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse) body)) {
                    String errorMsg = "Api key is expired or invalid";
                    return new ApiErrorResponse<>(errorMsg);
                }
                if(((RecipeSearchResponse) body).getCount() == 0){
                    // query is exhausted
                    return new ApiErrorResponse<>(RecipeListViewModel.QUERY_EXHAUSTED);
                }
            }
            if (body instanceof RecipeResponse) {
                if (!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeResponse) body)) {
                    String errorMsg = "Api key is expired or invalid";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }

            if (body == null || response.code() == 204) { // 204 is empty response code
                return new ApiEmptyResponse<>();
            } else {
                return new ApiSuccessResponse<>(body);
            }
        } else {
            String errorMessage = "";
            try {
                errorMessage = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = response.message();
            }
            return new ApiErrorResponse<>(errorMessage);

        }
    }

    public class ApiSuccessResponse<T> extends ApiResponse<T> {

        private T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    public class ApiErrorResponse<T> extends ApiResponse<T> {

        private String errorMessage;

        ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public class ApiEmptyResponse<T> extends ApiResponse<T> {
    }

}
