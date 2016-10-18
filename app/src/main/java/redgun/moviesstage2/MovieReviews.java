package redgun.moviesstage2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gravi on 18-10-2016.
 */
public class MovieReviews {


    @SerializedName("content")
    String review;

    @SerializedName("author")
    String reviewer;


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

}
