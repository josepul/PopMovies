package es.josepul.popmovies;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.josepul.popmovies.data.MoviesContract;
import es.josepul.popmovies.util.DateUtils;
import es.josepul.popmovies.util.MovieDBConstants;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    static final String DETAIL_URI = "MOVIE_URI";

    static final String SHARE_TAG = "#PopularMovies - ";

    private Uri mUri;

    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEWS_LOADER = 1;
    private static final int TRAILERS_LOADER = 2;

    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_NAME_TITLE,
            MoviesContract.MovieEntry.COLUMN_NAME_POSTER,
            MoviesContract.MovieEntry.COLUMN_NAME_SYNOPSIS,
            MoviesContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_NAME_RUNTIME,
            MoviesContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_NAME_FAVOURITE
    };

    private static final String[] REVIEW_COLUMNS = {
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry._ID,
            MoviesContract.ReviewEntry.COLUMN_NAME_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_NAME_CONTENT
    };

    private static final String[] TRAILER_COLUMNS = {
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.COLUMN_NAME_NAME,
            MoviesContract.TrailerEntry.COLUMN_NAME_KEY,
            MoviesContract.TrailerEntry.COLUMN_NAME_SIZE
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER = 2;
    public static final int COL_MOVIE_SYNOPSIS = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_RUNTIME = 5;
    public static final int COL_MOVIE_VOTE_AVERAGE = 6;
    public static final int COL_MOVIE_FAVOURITE = 7;

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_CONTENT = 2;

    public static final int COL_TRAILER_ID = 0;
    public static final int COL_TRAILER_NAME = 1;
    public static final int COL_TRAILER_KEY = 2;


    @Bind(R.id.movie_detail_name) TextView mTitleTextView;
    @Bind(R.id.movie_detail_overview) TextView mOverviewTextView;
    @Bind(R.id.movie_detail_release_date) TextView mReleaseDateTextView;
    @Bind(R.id.movie_detail_rating) TextView mRatingTextView;
    @Bind(R.id.movie_detail_poster) ImageView mPosterImageView;
    @Bind(R.id.movie_detail_rating_bar) RatingBar mRatingBar;
    @Bind(R.id.movie_detail_runtime) TextView mRuntimeTextView;
    @Bind(R.id.movie_detail_reviews) LinearLayout mReviews;
    @Bind(R.id.movie_detail_trailers) LinearLayout mTrailers;
    @Bind(R.id.movie_detail_mark_favourite) Button mFavourite;
    @Bind(R.id.movie_trailers_label) TextView mTrailersLabel;
    @Bind(R.id.movie_detail_reviews_label) TextView mReviewsLabel;


    public MovieDetailActivityFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null){
            //Load uri from MainActivity into mUri
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //Init loaders
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mTrailers != null && mTrailers.getChildCount() > 0) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String shareText = "";
        if(mTrailers != null && mTrailers.getChildCount() > 0){
            //If the novie has trailers, create an intent to share the first trailer
            View firstTrailer = mTrailers.getChildAt(0);
            TextView trailerKey = (TextView) firstTrailer.findViewById(R.id.trailer_item_key);
            shareText = trailerKey.getText().toString();
        }

        shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_TAG
                + getResources().getString(R.string.youtube_url)+shareText);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        CursorLoader cursorLoader = null;
        if (mUri != null ) {

            if (id == DETAIL_LOADER) {
                //Cursor para cargar el detalle de las peliculas
                cursorLoader = new CursorLoader(
                        getActivity(),
                        mUri,
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            } else if (id == REVIEWS_LOADER) {
                //Cursor para cargar las reviews
                //Obtener el id de la pelicula de la uri recibida en el constructor
                long idMovie = Long.parseLong(MoviesContract.ReviewEntry.getMovieIdFromUri(mUri));
                cursorLoader = new CursorLoader(
                        getActivity(),
                        MoviesContract.ReviewEntry.buildReviewUri(idMovie),
                        REVIEW_COLUMNS,
                        null,
                        null,
                        null
                );
            } else if (id == TRAILERS_LOADER) {
                //Cursor para cargar los trailers
                //Obtener el id de la pelicula de la uri recibida en el constructor
                long idMovie = Long.parseLong(MoviesContract.TrailerEntry.getMovieIdFromUri(mUri));
                cursorLoader = new CursorLoader(
                        getActivity(),
                        MoviesContract.TrailerEntry.buildTrailerUri(idMovie),
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null
                );
            }
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Ha terminado la carga del cursor
        if (data != null && data.moveToFirst()) {

            if(loader.getId() == DETAIL_LOADER){
                //Se carga el cursor de detalle de la pelicula
                Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/walkway_semiBold.ttf");
                mTitleTextView.setTypeface(custom_font);
                mTitleTextView.setVisibility(View.VISIBLE);


                final long movieId = data.getLong(COL_MOVIE_ID);
                String movieTitle = data.getString(COL_MOVIE_TITLE);
                String moviePoster = data.getString(COL_MOVIE_POSTER);
                String movieSynopsis = data.getString(COL_MOVIE_SYNOPSIS);
                long movieReleaseDate = data.getLong(COL_MOVIE_RELEASE_DATE);
                int movieRuntime = data.getInt(COL_MOVIE_RUNTIME);
                Double movieVoteAverage = data.getDouble(COL_MOVIE_VOTE_AVERAGE);
                final int favourite = data.getInt(COL_MOVIE_FAVOURITE);

                mTitleTextView.setText(movieTitle);
                mOverviewTextView.setText(movieSynopsis);
                mReleaseDateTextView.setText(DateUtils.getStringDateFromMilliseconds(movieReleaseDate));
                mRatingTextView.setText(getText(R.string.movie_rating_label) + ": " + movieVoteAverage + "/" + getString(R.string.movie_max_rating_label));
                mRatingBar.setRating(movieVoteAverage.floatValue() / 2);
                mRatingBar.setVisibility(View.VISIBLE);
                mRuntimeTextView.setText(String.valueOf(movieRuntime) + getString(R.string.movie_runtime_units));
                mFavourite.setVisibility(View.VISIBLE);
                Picasso.with(getActivity().getApplicationContext())
                        .load(MovieDBConstants.MOVIE_DB_POSTERS_BASE+moviePoster)
                        .into(mPosterImageView);
                if(1 == favourite){
                    mFavourite.setText(getString(R.string.unmark_favourite));
                }else{
                    mFavourite.setText(getString(R.string.mark_favourite));
                }
                mFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues contentValues = new ContentValues();
                        if(1 == favourite) {
                            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_FAVOURITE, 0);
                            mFavourite.setText(getString(R.string.mark_favourite));
                            Log.d(LOG_TAG, "Movie unmarked as favourite");
                        }else{
                            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_FAVOURITE, 1);
                            mFavourite.setText(getString(R.string.unmark_favourite));
                            Log.d(LOG_TAG, "Movie marked as favourite");
                        }

                        getContext().getContentResolver().update(MoviesContract.MovieEntry.CONTENT_URI,
                                contentValues,
                                MoviesContract.MovieEntry._ID + " = " + movieId,
                                null
                        );
                    }
                });
            }else if( loader.getId() == REVIEWS_LOADER){

                //Se carga el cursor con las review en un LinearLayout

                if ( data != null && data.getCount() > 0){
                    //Inflar el layout
                    LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    do{
                        //Iterar sobre las reviews
                        String labelAuthor = getString(R.string.review_author);
                        String labelContent = getString(R.string.review_content);
                        String reviewAuthor = data.getString(MovieDetailActivityFragment.COL_REVIEW_AUTHOR);
                        String reviewContent = data.getString(MovieDetailActivityFragment.COL_REVIEW_CONTENT);

                        View v = inflater.inflate(R.layout.review_item, null);
                        TextView reviewAuthorTextView = (TextView) v.findViewById(R.id.review_item_author);
                        reviewAuthorTextView.setText(labelAuthor + " : " + reviewAuthor);
                        TextView reviewContentTextView = (TextView) v.findViewById(R.id.review_item_content);
                        reviewContentTextView.setText(reviewContent);
                        mReviews.addView(v);
                    }while(data.moveToNext());

                    mReviewsLabel.setVisibility(View.VISIBLE);
                }else{
                    mReviewsLabel.setVisibility(View.INVISIBLE);
                }

            }else if( loader.getId() == TRAILERS_LOADER){
                //Se carga el cursor con los trailers
                if ( data != null && data.getCount() > 0){

                    if(mShareActionProvider != null){
                        mShareActionProvider.setShareIntent(new Intent());
                    }

                    //Se infla el layout
                    LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    data.moveToFirst();
                    do{
                        //Iterar sobre los trailers
                        String trailerName = data.getString(MovieDetailActivityFragment.COL_REVIEW_AUTHOR);
                        String trailerKey = data.getString(MovieDetailActivityFragment.COL_REVIEW_CONTENT);

                        View v = inflater.inflate(R.layout.trailer_item, null);
                        TextView trailerNameTextView = (TextView) v.findViewById(R.id.trailer_item_name);
                        trailerNameTextView.setText(trailerName);
                        final TextView trailerKeyTextView = (TextView) v.findViewById(R.id.trailer_item_key);
                        trailerKeyTextView.setText(trailerKey);
                        ImageView trailerPlayImageView = (ImageView) v.findViewById(R.id.trailer_item_play);
                        trailerPlayImageView.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                                getResources().getString(R.string.youtube_intent_uri) + trailerKeyTextView.getText()));
                                        startActivity(intent);
                                    }
                                }
                        );
                        mTrailers.addView(v);
                    }while(data.moveToNext());

                    if(mTrailers.getChildCount() > 0){
                        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                        if (mShareActionProvider != null) {
                            mShareActionProvider.setShareIntent(null);
                        }
                    }

                    mTrailersLabel.setVisibility(View.VISIBLE);
                }else{
                    mTrailersLabel.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}

