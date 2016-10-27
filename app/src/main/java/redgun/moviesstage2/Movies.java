package redgun.moviesstage2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gravi on 29-09-2016.
 */
public class Movies implements Parcelable {


    @SerializedName("id")
    String movieId;

    @SerializedName("title")
    String movieTitle;

    @SerializedName("poster_path")
    String moviePoster;

    @SerializedName("overview")
    String movieOverview;

    @SerializedName("vote_average")
    double averageRating;

    @SerializedName("release_date")
    String movieReleaseDate;

    @SerializedName("reviews")
    ArrayList<MovieReviews> movieReviews;

    ArrayList<String> movieTrailers;


    Movies(String movieId, String movieTitle, String moviePoster, String movieOverview, double averageRating, String movieReleaseDate) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.movieOverview = movieOverview;
        this.averageRating = averageRating;
        this.movieReleaseDate = movieReleaseDate;
    }

    // "De-parcel object
    public Movies(Parcel in) {
        movieId = in.readString();
        movieTitle = in.readString();
        moviePoster = in.readString();
        movieOverview = in.readString();
        averageRating = in.readDouble();
        movieReleaseDate = in.readString();
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(movieTitle);
        dest.writeString(moviePoster);
        dest.writeString(movieOverview);
        dest.writeDouble(averageRating);
        dest.writeString(movieReleaseDate);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };




}
