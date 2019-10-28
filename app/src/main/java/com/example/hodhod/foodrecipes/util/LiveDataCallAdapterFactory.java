package com.example.hodhod.foodrecipes.util;

import android.telecom.Call;

import com.example.hodhod.foodrecipes.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.lifecycle.LiveData;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {


    /**
     * this method performs a number of checks and then returns the Response type for the Retrofit request.
     * (@bodyType is the ResponseType. It can be RecipeResponse or RecipeSearchResponse)
     *
     * CHECK #1) returnType returns LiveData
     * CHECK #2) Type LiveData<T> is of ApiResponse.class
     * CHECK #3) Make sure ApiResponse is parameterized. AKA: ApiResponse<T> exist.
     *
     *
     */

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        // Check #1
        // Make sure the CallAdapter is returning a type of LiveData
        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        // Check #2
        // type that LiveData is wrapping
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0,(ParameterizedType) returnType );

        // Check if it's of Type ApiResponse
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("Type must be a defined resource");
        }

        // Check #3
        // Check if ApiResponse is parameterized. AKA: Does ApiResponse<T> exist (must wrap around T) ?
        // T is either a RecipeResponse or a RecipeSearchResponse
        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("resource must be parameterized");
        }

        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0,(ParameterizedType) observableType);
        return new LiveDataCallAdapter<Type>(bodyType);
    }
}
