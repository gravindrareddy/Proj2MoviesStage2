package redgun.moviesstage2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gravi on 18-10-2016.
 */
public class MovieVideos implements Parcelable {


    @SerializedName("key")
    String key;



    protected MovieVideos(Parcel in) {
        key = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieVideos> CREATOR = new Creator<MovieVideos>() {
        @Override
        public MovieVideos createFromParcel(Parcel in) {
            return new MovieVideos(in);
        }

        @Override
        public MovieVideos[] newArray(int size) {
            return new MovieVideos[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String review) {
        this.key = key;
    }


}
