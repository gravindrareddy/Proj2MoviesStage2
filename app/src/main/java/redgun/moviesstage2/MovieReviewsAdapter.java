package redgun.moviesstage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by gravi on 30-09-2016.
 */
public class MovieReviewsAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    ArrayList<MovieReviews> result;
    Context context;

    public MovieReviewsAdapter(Context context, ArrayList<MovieReviews> moviesReviewsArrayList) {
        // TODO Auto-generated constructor stub
        result = moviesReviewsArrayList;
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
        Holder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.movies_list_review_item, parent, false);
            holder = new Holder();
            holder.movie_review_content_tv = (TextView) rowView.findViewById(R.id.movie_review_content_tv);
            holder.movie_reviewer_tv = (TextView) rowView.findViewById(R.id.movie_reviewer_tv);
            rowView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.movie_review_content_tv.setText(result.get(position).getReview());
        holder.movie_reviewer_tv.setText(result.get(position).getReviewer());
        return rowView;
    }

    public class Holder {
        TextView movie_review_content_tv;
        TextView movie_reviewer_tv;
    }

}

