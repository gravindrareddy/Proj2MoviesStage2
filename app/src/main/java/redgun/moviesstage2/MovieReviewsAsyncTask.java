package redgun.moviesstage2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import redgun.moviesstage2.util.Utility;
import redgun.moviesstage2.BuildConfig;
import redgun.moviesstage2.R;

/**
 * Created by gravi on 18-10-2016.
 */


public class MovieReviewsAsyncTask extends AsyncTask<String, Void, ArrayList<MovieReviews>> {

    private final String LOG_TAG = MovieReviewsAsyncTask.class.getSimpleName();
    private final String MESSAGE = "MovieDetails";
    private final String REVIEW_MESSAGE = "ReviewDetails";
    private final Context mContext;
    private ListView movie_reviews_lv;

    public MovieReviewsAsyncTask(Context context, ListView movie_reviews_lv) {
        mContext = context;
        this.movie_reviews_lv = movie_reviews_lv;
    }

    private boolean DEBUG = true;
    private ProgressDialog progress;
    private volatile boolean running = true;

    protected void onPreExecute() {
        if (Utility.isOnline(mContext)) {
            progress = new ProgressDialog(mContext);
            progress.show();
        } else {
            cancel(true);
        }
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        running = false;
    }

    @Override
    protected ArrayList<MovieReviews> doInBackground(String... params) {
        ArrayList<MovieReviews> moviesReviewsList = new ArrayList<MovieReviews>();
        if (!isCancelled()) {
            // URL for calling the API is needed
            final String OWM_APIKEY = "api_key";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String sort_by = prefs.getString(mContext.getString(R.string.pref_sort_key), mContext.getString(R.string.pref_sort_top));
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority(mContext.getResources().getString(R.string.base_url))
                        .appendPath(mContext.getResources().getString(R.string.base_url_add1))
                        .appendPath(mContext.getResources().getString(R.string.base_url_add2))
                        .appendPath(sort_by)
                        .appendQueryParameter(OWM_APIKEY, BuildConfig.MOVIES_DB_API_KEY);
                URL url = new URL(builder.build().toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (urlConnection.getResponseCode() == 200) {
                    Gson gson = new GsonBuilder().create();
                    APIResponseContract.MovieReviewsAPIResponseEntry moviesResponse = gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)), APIResponseContract.MovieReviewsAPIResponseEntry.class);
                    moviesReviewsList = moviesResponse.movieReviews;

                } else {
                    Utility.showToast(mContext, "Something went wrong");
                }


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                e.printStackTrace();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment12", "Error closing stream", e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }
            }
        }
        return moviesReviewsList;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(final ArrayList<MovieReviews> responseMovieReviewsList) {
        if (responseMovieReviewsList == null) {
            //Utility.showToast(mContext, "No Movies Available. Please try again");
        } else {
            movie_reviews_lv.setAdapter(new MovieReviewsAdapter(mContext, responseMovieReviewsList));
        }
        progress.dismiss();
    }
}