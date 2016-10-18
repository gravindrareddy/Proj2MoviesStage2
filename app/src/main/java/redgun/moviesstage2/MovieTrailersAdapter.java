package redgun.moviesstage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

import udacity.redgun.moviesstage2.R;


/**
 * Created by gravi on 30-09-2016.
 */
public class MovieTrailersAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    ArrayList<String> result;
    Context context;

    public MovieTrailersAdapter(Context context, ArrayList<String> moviesTrailersArrayList) {
        // TODO Auto-generated constructor stub
        result = moviesTrailersArrayList;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.movies_list_trailer_item, parent, false);
            holder = new Holder();
            holder.movie_trailer_yv = (YouTubePlayerView) rowView.findViewById(R.id.movie_trailer_yv);
            rowView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        //TODO YouTube Video
        return rowView;
    }

    public class Holder {
        YouTubePlayerView movie_trailer_yv;
    }

}

