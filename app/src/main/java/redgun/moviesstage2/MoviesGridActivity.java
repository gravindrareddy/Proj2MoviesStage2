package redgun.moviesstage2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import redgun.moviesstage2.data.MoviesProvider;
import redgun.moviesstage2.util.Utility;

public class MoviesGridActivity extends AppCompatActivity {

    static String TAG;
    ArrayList<Movies> moviesList;
    GridView movies_gv;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_grid);
        TAG = this.getClass().getName();
        context = this;

        movies_gv = (GridView) findViewById(R.id.movies_gv);
        movies_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Send intent to SingleViewActivity
                Intent i = new Intent(getApplicationContext(), MovieDetailActivity.class);
                Movies selectedMovie = moviesList.get(position);
                Movies parcelMovie = new Movies(selectedMovie.getMovieId(), selectedMovie.getMovieTitle(), selectedMovie.getMoviePoster(), selectedMovie.getMovieOverview(), selectedMovie.getAverageRating(), selectedMovie.getMovieReleaseDate());
                i.putExtra("parcelMovie", parcelMovie);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //updateMovie();
        // todo get the user preference of sort order
    }

    public void onStart() {
        super.onStart();
        FetchMoviesTask moviesTask = new FetchMoviesTask(context);
        moviesTask.execute();

    }


    /**
     * Method to fetch Movies list from API
     */

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movies>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private final String MESSAGE = "MovieDetails";
        private final String REVIEW_MESSAGE = "ReviewDetails";
        private final Context mContext;
        private boolean DEBUG = true;
        private ProgressDialog progress;
        private volatile boolean running = true;

        public FetchMoviesTask(Context context) {
            mContext = context;
        }

        protected void onPreExecute() {
            if (Utility.isOnline(context)) {
                progress = new ProgressDialog(context);
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
        protected ArrayList<Movies> doInBackground(String... params) {

            if (!isCancelled()) {
                // URL for calling the API is needed
                final String OWM_APIKEY = "api_key";

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String sort_by = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_top));
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String moviesJsonStr = null;

                if (!sort_by.equals(getResources().getString(R.string.pref_sort_favorite))) {
                    try {
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("https")
                                .authority(getResources().getString(R.string.base_url))
                                .appendPath(getResources().getString(R.string.base_url_add1))
                                .appendPath(getResources().getString(R.string.base_url_add2))
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
                            APIResponseContract.MoviesAPIResponseEntry moviesResponse = gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)), APIResponseContract.MoviesAPIResponseEntry.class);
                            moviesList = moviesResponse.movies;

                        } else {
                            Utility.showToast(context, "Something went wrong");
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
                } else {
                    Uri.Builder builder = new Uri.Builder();
                    Uri _uri = builder.scheme("content")
                            .authority(getResources().getString(R.string.contentprovider_authority))
                            .appendPath(getResources().getString(R.string.contentprovider_movie_entry)).build();
                    Cursor _cursor = getContentResolver().query(_uri, null, null, null, null);
                    //ToDo cursor adapter
                }
            }
            return moviesList;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(final ArrayList<Movies> responseMoviesList) {
            moviesList = responseMoviesList;
            if (moviesList == null) {
                Utility.showToast(context, "No Movies Available. Please try again");
            } else {
                movies_gv.setAdapter(new MoviesGridAdapter(context, moviesList));
            }
            progress.dismiss();
        }
    }

}
