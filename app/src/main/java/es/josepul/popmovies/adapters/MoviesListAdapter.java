package es.josepul.popmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.josepul.popmovies.R;
import es.josepul.popmovies.model.Movie;
import es.josepul.popmovies.util.MovieDBConstants;


/**
 * Created by Jose on 08/07/2015.
 */
public class MoviesListAdapter extends ArrayAdapter<Movie> {

    private Activity activity;
    private List<Movie> movieList;


    public MoviesListAdapter(Activity context, List<Movie> list) {
        super(context, R.layout.movies_grid_item, list);
        this.activity = context;
        this.movieList = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //[en] Inflate layout for individual items
        //[en] Generar layout para �tems individuales
        if(convertView == null){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.movies_grid_item, null);
        }

        ViewHolder holder = new ViewHolder(convertView);

        //[en] Get Movie object from position in ListView
        //[es] Obtener el objeto Movie desde la posici�n en el ListView
        final Movie movie = movieList.get(position);

        //[en] Load image with picasso into movieCover ImageView
        //[es] Cargar la imagen con picaso en el ImageView movieCover
        Picasso.with(this.getContext())
                .load(MovieDBConstants.MOVIE_DB_POSTERS_BASE+movie.getMovieImageUrl())
                .into(holder.movieCoverImageView);
        holder.movieIdTextView.setText(movie.getMovieId());

        return convertView;
    }

    static class ViewHolder{

        @Bind(R.id.list_item_movie_cover)
        ImageView movieCoverImageView;
        @Bind(R.id.list_item_movie_id)
        TextView movieIdTextView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

}
