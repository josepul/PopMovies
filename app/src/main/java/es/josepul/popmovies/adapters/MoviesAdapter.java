package es.josepul.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.josepul.popmovies.MoviePostersFragment;
import es.josepul.popmovies.R;
import es.josepul.popmovies.util.MovieDBConstants;


/**
 * Created by Jose on 31/10/2015.
 */
public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {

        @Bind(R.id.list_item_movie_cover)
        ImageView movieCoverImageView;
        @Bind(R.id.list_item_movie_id)
        TextView movieIdTextView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movies_grid_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String movieId = cursor.getString(MoviePostersFragment.COL_MOVIE_ID);
        viewHolder.movieIdTextView.setText(movieId);

        String moviePoster = cursor.getString(MoviePostersFragment.COL_POSTER);
        Picasso.with(context)
                .load(MovieDBConstants.MOVIE_DB_POSTERS_BASE + moviePoster)
                .into(viewHolder.movieCoverImageView);
    }
}
