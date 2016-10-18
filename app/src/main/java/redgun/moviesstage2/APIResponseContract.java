package redgun.moviesstage2;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gravi on 09-10-2016.
 * This class was created on-line with DB contract. This class will hold the entries of all API JSON responses
 */
public class APIResponseContract {

    static class MoviesAPIResponseEntry {
        @SerializedName("results")
        public ArrayList<Movies> movies;
    }

    static class MovieReviewsAPIResponseEntry {
        @SerializedName("results")
        public ArrayList<MovieReviews> movieReviews;
    }

    static class MovieTrailersAPIResponseEntry {
        @SerializedName("results")
        public ArrayList<String> movieTrailers;
    }
}
