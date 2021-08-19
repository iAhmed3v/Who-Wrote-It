package com.ahmed3v.whowroteit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;


public class BooksLoader extends AsyncTaskLoader<String> {


    private String mQueryString;

    public BooksLoader(@NonNull Context context, String queryString) {

        super(context);

        mQueryString = queryString;
    }

    @Nullable
    @Override
    public String loadInBackground() {

        return NetworkUtils.getBookInfo(mQueryString);
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }
}
