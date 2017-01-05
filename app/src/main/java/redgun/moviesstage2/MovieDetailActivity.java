package redgun.moviesstage2;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import redgun.moviesstage2.data.MoviesContract;
import redgun.moviesstage2.util.Utility;


public class MovieDetailActivity extends AppCompatActivity {

    ImageView movie_poster_iv;
    TextView movie_release_date_tv;
    TextView movie_user_rating_tv;
    TextView movie_title_tv;
    TextView movie_synopsis_tv;
    ListView movie_trailers_lv;
    ListView movie_reviews_lv;
    ToggleButton movie_favorite_tb;
    Context mContext;
    Movies intentReceivedMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        Intent i = getIntent();
        intentReceivedMovie = i.getExtras().getParcelable("parcelMovie");

        movie_poster_iv = (ImageView) findViewById(R.id.movie_poster_iv);
        movie_release_date_tv = (TextView) findViewById(R.id.movie_release_date_tv);
        movie_user_rating_tv = (TextView) findViewById(R.id.movie_user_rating_tv);
        movie_title_tv = (TextView) findViewById(R.id.movie_title_tv);
        movie_synopsis_tv = (TextView) findViewById(R.id.movie_synopsis_tv);
        movie_trailers_lv = (ListView) findViewById(R.id.movie_trailers_lv);
        movie_reviews_lv = (ListView) findViewById(R.id.movie_reviews_lv);
        movie_favorite_tb = (ToggleButton) findViewById(R.id.movie_favorite_tb);

        Picasso.with(this).load(getResources().getString(R.string.base_image_url).concat(intentReceivedMovie.getMoviePoster())).into(movie_poster_iv);
        movie_release_date_tv.setText(intentReceivedMovie.getMovieReleaseDate());
        movie_user_rating_tv.setText(intentReceivedMovie.getAverageRating() + "");
        movie_title_tv.setText(intentReceivedMovie.getMovieTitle());
        movie_synopsis_tv.setText(intentReceivedMovie.getMovieOverview());
        movie_favorite_tb.setChecked(intentReceivedMovie.isFavorite());

        movie_favorite_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // save this movie to DB
                    ContentValues movieContentValues = new ContentValues();
                    movieContentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, intentReceivedMovie.getMovieId());
                    movieContentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, intentReceivedMovie.getMovieReleaseDate());
                    movieContentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, intentReceivedMovie.getMovieTitle());
                    movieContentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, intentReceivedMovie.getMovieOverview());
                    movieContentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER, intentReceivedMovie.getMoviePoster());
                    movieContentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING, intentReceivedMovie.getAverageRating());
                    getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieContentValues);
                } else {
                    String[] selectionArgs = {intentReceivedMovie.getMovieId()};
                    getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " =?", selectionArgs);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateMovie();
        // todo get the user preference of sort order
    }

    public void onStart() {
        super.onStart();
        MovieReviewsAsyncTask1 reviewAsyncTask = new MovieReviewsAsyncTask1();
        reviewAsyncTask.execute();

        MovieVideosAsyncTask videoAsyncTask = new MovieVideosAsyncTask();
        videoAsyncTask.execute();

    }

    public class MovieReviewsAsyncTask1 extends AsyncTask<String, Void, ArrayList<MovieReviews>> {

        private final String LOG_TAG = MovieReviewsAsyncTask1.class.getSimpleName();
        private final String MESSAGE = "MovieDetails";
        private final String REVIEW_MESSAGE = "ReviewDetails";
        private boolean DEBUG = true;
        private ProgressDialog progress1;
        private volatile boolean running = true;

        public MovieReviewsAsyncTask1() {

        }

        protected void onPreExecute() {
            if (Utility.isOnline(mContext)) {
                progress1 = new ProgressDialog(mContext);
                progress1.show();
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
                            .appendPath(intentReceivedMovie.getMovieId())
                            .appendPath("reviews")
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
            progress1.dismiss();
        }
    }


    public class MovieVideosAsyncTask extends AsyncTask<MovieVideos, Void, ArrayList<MovieVideos>> {

        private final String LOG_TAG = MovieVideosAsyncTask.class.getSimpleName();


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
                            .appendPath(intentReceivedMovie.getMovieId())
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
                movie_trailers_lv.setAdapter(new MovieVideosAdapter(mContext, responseMovieTrailersList));
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
}
