package es.josepul.popmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.josepul.popmovies.adapters.MoviesAdapter;
import es.josepul.popmovies.data.MoviesContract;


public class MoviePostersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private MoviesAdapter mMoviesAdapter;
    private String mLastOrderValue;
    private View mRootView;
    private boolean mMoviesFetched = false;
    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();

    @Bind(R.id.error_image) ImageView mErrorImageView;
    @Bind(R.id.error_text) TextView mErrorTextView;

    private static final int MOVIES_LOADER = 0;
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID ,
            MoviesContract.MovieEntry.COLUMN_NAME_POSTER,
            MoviesContract.MovieEntry.COLUMN_NAME_TITLE
    };
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER = 1;
    public static final int COL_TITLE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, mRootView);

        String actualOrder;

        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);

        //[en] Get order preference from SharedPreferences
        //[es] Obtener las preferencias de ordenacion de SharedPreferences
        actualOrder = getOrderPreferences();
        if(mLastOrderValue == null || mLastOrderValue.isEmpty()){
            mLastOrderValue = actualOrder;
        }

        String order = getOrderPreferences();
        String favouritePreference = getString(R.string.pref_order_favourite);

        if(!order.equals(favouritePreference) && !mMoviesFetched){
            new FetchMovies(this.getContext(), mRootView).execute();
            mMoviesFetched = true;
        }


        GridView gridView = (GridView) mRootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) mMoviesAdapter.getItem(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MoviesContract.MovieEntry.buildMovieUri(
                                    cursor.getLong(COL_MOVIE_ID)
                            ));
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String actualOrder = getOrderPreferences();
        String favouriteOrder = getString(R.string.pref_order_favourite);
        if(mLastOrderValue != null && !mLastOrderValue.equalsIgnoreCase(actualOrder)){
            //[en] If last execution of FetchMovies was with different order criteria it's
            // necessary to execute FetchMovies task because movies order has changed in preferences.
            //[es] Si la ultima ejecucion de FetchMovies fue con un criterio de ordenacion diferente
            //es necesario ejecutar de nuevo FetchMovies porque el criterio de ordenacion
            //ha cambiado en las preferencias
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
            new FetchMovies(getActivity(), mRootView).execute();

            //[en] Update lastOrderValue to actual order preference
            //[es] Actualizar lastOrderValue al criterio de ordenacion actual en las preferencias
            mLastOrderValue = actualOrder;
        }

        if(actualOrder.equals(favouriteOrder)){
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Get the preference order from SharedPreferences
     * @return Stored shared preference
     */
    private String getOrderPreferences(){
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPrefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_order_popular));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri moviesUri = MoviesContract.MovieEntry.CONTENT_URI;

        String order = getOrderPreferences();
        String popular = getString(R.string.pref_order_popular);
        String highestRated = getString(R.string.pref_order_highest_rated);
        String favourite = getString(R.string.pref_order_favourite);

        String selection = "";
        String[] selectionArgs = {"1"};

        if(order.equals(popular)){
            selection = MoviesContract.MovieEntry.COLUMN_NAME_POPULAR + "=?";
        }else if(order.equals(highestRated)){
            selection = MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED + "=?";
        }else if(order.equals(favourite)){
            selection = MoviesContract.MovieEntry.COLUMN_NAME_FAVOURITE + "=?";
        }

        return new CursorLoader(getActivity(),
                moviesUri,
                MOVIES_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        if(data != null && data.getCount() == 0){
            String order = getOrderPreferences();
            String favourite = getString(R.string.pref_order_favourite);
            if(order.equalsIgnoreCase(favourite)){
                mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.movies_grid).setVisibility(View.GONE);
                if(mErrorImageView != null && mErrorTextView != null ){
                    mErrorImageView.setImageResource(R.drawable.no_favourites);
                    mErrorTextView.setText(getString(R.string.no_favourite_movies));
                }
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }
}
