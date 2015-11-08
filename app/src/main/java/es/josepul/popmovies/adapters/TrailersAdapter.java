package es.josepul.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.josepul.popmovies.MovieDetailActivityFragment;
import es.josepul.popmovies.R;


/**
 * Created by Jose on 31/10/2015.
 */
public class TrailersAdapter extends CursorAdapter {

    public TrailersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {

        @Bind(R.id.trailer_item_name)
        TextView mTrailerName;
        @Bind(R.id.trailer_item_key)
        TextView mTrailerKey;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String trailerName = cursor.getString(MovieDetailActivityFragment.COL_TRAILER_NAME);
        viewHolder.mTrailerName.setText(trailerName);

        String trailerKey = cursor.getString(MovieDetailActivityFragment.COL_TRAILER_KEY);
        viewHolder.mTrailerKey.setText(trailerKey);
    }
}
