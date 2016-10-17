package redgun.moviesstage2;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gravi on 09-10-2016.
 */
public class APIResponse {

    @SerializedName("results")
    public ArrayList<Movies> movies;
}
