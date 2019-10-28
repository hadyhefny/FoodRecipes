package com.example.hodhod.foodrecipes;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.MainThread;

public class AppExecutors {

    private static AppExecutors instance;

    private AppExecutors() {
    }

    public static AppExecutors getInstance(){
        if(instance == null){
            instance = new AppExecutors();
        }
        return instance;
    }

    // load db cache on background thread
    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

    // send information from background thread to main thread
    private Executor mMainThreadExecutor = new MainThreadExecutor();

    public Executor diskIO(){
        return mDiskIO;
    }

    public Executor mainThread(){
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor{

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
