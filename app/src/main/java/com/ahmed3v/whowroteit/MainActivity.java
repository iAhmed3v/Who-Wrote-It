package com.ahmed3v.whowroteit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.BundleCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText mBookEditText;
    private TextView mTitle;
    private TextView mAuthor;
    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookEditText = findViewById(R.id.books_edit_text);
        mTitle = findViewById(R.id.title_text_view);
        mAuthor = findViewById(R.id.book_text_view);
        mSearchButton = findViewById(R.id.search_btn);


        mSearchButton.setOnClickListener(v -> {

            // Get the search string from the input field.
            String queryString = mBookEditText.getText().toString();

            // Hide the keyboard when the button is clicked.
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager != null ) {

                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            // Check the status of the network connection.
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (connectivityManager != null) {

                networkInfo = connectivityManager.getActiveNetworkInfo();
            }


            // If the network is available, connected, and the search field
            // is not empty, start a BooksLoader AsyncTask.
            if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {

                Bundle queryBundle = new Bundle();
                queryBundle.putString("queryString", queryString);
                getSupportLoaderManager().restartLoader(0, queryBundle, this);

                mAuthor.setText("");
                mTitle.setText(R.string.loading);
            }

            // Otherwise update the TextView to tell the user there is no
            // connection, or no search term.
            else {

                if (queryString.length() == 0) {

                    mAuthor.setText("");
                    mTitle.setText(R.string.no_search_term);

                } else {

                    mAuthor.setText("");
                    mTitle.setText(R.string.no_network);
                }
            }

        });


    }

    @NonNull
    @Override
    public Loader <String> onCreateLoader(int id , Bundle args) {

        String queryString = "";

        if (args != null) {

            queryString = args.getString("queryString");
        }

        return new BooksLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader <String> loader , String data) {

        try {

            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(data);

            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");


            // Initialize iterator and results fields.
            int i = 0;
            String title = null;
            String authors = null;

            // Look for results in the items array, exiting when both the
            // title and author are found or when all items have been checked.
            while (i < itemsArray.length() && (authors == null && title == null)) {

                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {

                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }


            // If title and authors are found, display the result.
            if (title != null && authors != null) {

                mTitle.setText(String.format("Title: %s" , title));
                mAuthor.setText(String.format("Author: %s" , authors));

            } else {
                // If none are found, update the UI to show failed results.
                mTitle.setText(R.string.no_result);
                mAuthor.setText("");
            }

        }catch(JSONException e){

            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            mTitle.setText(R.string.no_result);
            mAuthor.setText("");


            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader <String> loader) {

        //Do nothing, Required by interface.
    }
}