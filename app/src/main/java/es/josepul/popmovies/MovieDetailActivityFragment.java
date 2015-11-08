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
import android.util.Log;
import android.view.LayoutInflater;
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
import es.josepul.popmovies.adapters.ReviewsAdapter;
import es.josepul.popmovies.adapters.TrailersAdapter;
import es.josepul.popmovies.data.MoviesContract;
import es.josepul.popmovies.util.DateUtils;
import es.josepul.popmovies.util.MovieDBConstants;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public MovieDetailActivityFragment() {
    }

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

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
    public static final int COL_TRAILER_SIZE = 3;


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
    ReviewsAdapter mReviewsAdapter;
    TrailersAdapter mTrailersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        //mReviewsAdapter = new ReviewsAdapter(getActivity(), null, 0);
        //mReviews.setAdapter(mReviewsAdapter);

        //mTrailersAdapter = new TrailersAdapter(getActivity(), null, 0);
        //mTrailers.setAdapter(mTrailersAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        CursorLoader cursorLoader = null;
        if (intent == null) {
            return null;
        }


        if (id == DETAIL_LOADER) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            cursorLoader = new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }else if(id == REVIEWS_LOADER){
            long idMovie = Long.parseLong(MoviesContract.ReviewEntry.getMovieIdFromUri(intent.getData()));
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.ReviewEntry.buildReviewUri(idMovie),
                    REVIEW_COLUMNS,
                    null,
                    null,
                    null
            );
        }else if(id == TRAILERS_LOADER){
            long idMovie = Long.parseLong(MoviesContract.TrailerEntry.getMovieIdFromUri(intent.getData()));
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.TrailerEntry.buildTrailerUri(idMovie),
                    TRAILER_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            if(loader.getId() == DETAIL_LOADER){
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
                mRuntimeTextView.setText(String.valueOf(movieRuntime));
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
                //mReviewsAdapter.swapCursor(data);

                if ( data != null ){

                    LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    while(data.moveToNext()){
                        String labelAuthor = getString(R.string.review_author);
                        String labelContent = getString(R.string.review_content);
                        String reviewAuthor = data.getString(MovieDetailActivityFragment.COL_REVIEW_AUTHOR);
                        String reviewContent = data.getString(MovieDetailActivityFragment.COL_REVIEW_CONTENT);

                        View v = inflater.inflate(R.layout.review_item, null);
                        TextView reviewAuthorTextView = (TextView) v.findViewById(R.id.review_item_author);
                        reviewAuthorTextView.setText(labelAuthor + " : " + reviewAuthor);
                        TextView reviewContentTextView = (TextView) v.findViewById(R.id.review_item_content);
                        reviewContentTextView.setText(reviewContent);
                        if(data.isLast()){
                            View reviewHorizontalLine = v.findViewById(R.id.review_item_horizontal_line);
                            reviewHorizontalLine.setVisibility(View.GONE);
                        }
                        mReviews.addView(v);
                    }
                }

            }else if( loader.getId() == TRAILERS_LOADER){
                if ( data != null ){

                    LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    while(data.moveToNext()){
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
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKeyTextView.getText()));
                                        startActivity(intent);
                                    }
                                }
                        );
                        if(data.isLast()){
                            View reviewHorizontalLine = v.findViewById(R.id.trailer_item_horizontal_line);
                            reviewHorizontalLine.setVisibility(View.GONE);
                        }
                        mTrailers.addView(v);
                    }
                }
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}

