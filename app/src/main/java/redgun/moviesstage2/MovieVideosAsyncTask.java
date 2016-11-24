package redgun.moviesstage2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
/**
 * Created by gravi on 18-10-2016.
 */


public class MovieVideosAsyncTask extends AsyncTask<MovieVideos, Void, ArrayList<MovieVideos>> {

    private final String LOG_TAG = MovieVideosAsyncTask.class.getSimpleName();
    private final Context mContext;
    private final String mMovieId;
    private ListView movie_trailers_lv;

    public MovieVideosAsyncTask(Context context, String movieId, ListView movie_trailers_lv) {
        mContext = context;
        mMovieId = movieId;
        this.movie_trailers_lv = movie_trailers_lv;
    }

    private volatile boolean running = true;
    private ProgressDialog progress;

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
    protected ArrayList<MovieVideos> doInBackground(MovieVideos... params) {
        ArrayList<MovieVideos> moviesTrailersList = new ArrayList<MovieVideos>();
        if (!isCancelled()) {
            // URL for calling the API is needed
            final String OWM_APIKEY = "api_key";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            //String sort_by = prefs.getString(mContext.getString(R.string.pref_sort_key), mContext.getString(R.string.pref_sort_top));
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority(mContext.getResources().getString(R.string.base_url))
                        .appendPath(mContext.getResources().getString(R.string.base_url_add1))
                        .appendPath(mContext.getResources().getString(R.string.base_url_add2))
                        .appendPath(mMovieId)
                        .appendPath(mContext.getResources().getString(R.string.base_url_videos))
                        .appendQueryParameter(OWM_APIKEY, BuildConfig.MOVIES_DB_API_KEY);
                URL url = new URL(builder.build().toString());
                Log.i("VideosAynscTask", url.getPath());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (urlConnection.getResponseCode() == 200) {
                    Gson gson = new GsonBuilder().create();
                    APIResponseContract.MovieTrailersAPIResponseEntry moviesResponse = gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)), APIResponseContract.MovieTrailersAPIResponseEntry.class);
                    moviesTrailersList = moviesResponse.movieTrailers;

                } else {
                    Utility.showToast(mContext, "Something went wrong");
                }


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                e.printStackTrace();
                // If the code didn't successfully get the data, there's no point in attempting
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
        return moviesTrailersList;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(final ArrayList<MovieVideos> responseMovieTrailersList) {
        if (responseMovieTrailersList == null) {
            //    Utility.showToast(mContext, "No Movies Available. Please try again");
        } else {
            MovieVideosAdapter madapter = new MovieVideosAdapter(mContext, responseMovieTrailersList);
            movie_trailers_lv.setAdapter(madapter);
            madapter.notifyDataSetChanged();
            //movie_trailers_lv.notify();
            movie_trailers_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    // Send intent to SingleViewActivity
                    Intent i = new Intent(mContext, MovieVideoActivity.class);
                    i.putExtra("videoKey", responseMovieTrailersList.get(position).getKey());
                    mContext.startActivity(i);
                }
            });
        }
        progress.dismiss();
    }
}