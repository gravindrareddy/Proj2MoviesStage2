package redgun.moviesstage2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import udacity.redgun.moviesstage2.R;


/**
 * Created by gravi on 30-09-2016.
 */
public class MoviesGridAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    ArrayList<Movies> result;
    Context context;

    public MoviesGridAdapter(Context context, ArrayList<Movies> moviesArrayList) {
        // TODO Auto-generated constructor stub
        result = moviesArrayList;
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
            rowView = inflater.inflate(R.layout.movies_grid_item, parent, false);
            holder = new Holder();
            holder.grid_movie_poster_iv = (ImageView) rowView.findViewById(R.id.grid_movie_poster_iv);

            rowView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        //TODO Use Picasso example
        // holder.grid_movie_poster_iv.(result.get(position).getMoviePoster());
        Picasso.with(context).load(context.getResources().getString(R.string.base_image_url).concat(result.get(position).getMoviePoster())).into(holder.grid_movie_poster_iv);
        return rowView;
    }

    public class Holder {
        ImageView grid_movie_poster_iv;


    }

}

