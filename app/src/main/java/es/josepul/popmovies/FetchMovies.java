package es.josepul.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import es.josepul.popmovies.client.MovieDBClient;
import es.josepul.popmovies.data.MoviesContract;
import es.josepul.popmovies.model.Movie;
import es.josepul.popmovies.model.Review;
import es.josepul.popmovies.model.Trailer;
import es.josepul.popmovies.util.DateUtils;
import es.josepul.popmovies.util.Utils;
import retrofit.RetrofitError;

public class FetchMovies extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovies.class.getName();

    private RetrofitError mRetrofitError;
    private final Context mContext;
    private final View mRootView;

    public FetchMovies(Context context, View rootView){
        mContext = context;
        mRootView = rootView;
    }

    @Override
    protected Void doInBackground(Void... params) {

        List<Movie> results = null;

        //[en] Get order preferences on SharedPreferences. Get also values for popularity and
        //highest rated orders to call MovieDBClient
        //[es] Obtener el criterio de ordenacion preferido de SharedPreferences. Obtener tambien
        //los valores de los criterios de ordenacion or popularidad y por nota para llamar
        //a MovieDBClient
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        String actualOrder = sharedPrefs.getString(
                mContext.getString(R.string.pref_order_key),
                mContext.getString(R.string.pref_order_popular));

        String popularity = mContext.getString(R.string.pref_order_popular);
        String rating = mContext.getString(R.string.pref_order_highest_rated);
        int rowsOnDatabse = 0;


        try {
            //[en] Get movies from web service movies client
            //[es] Obtener las peliculas del cliente del servicio web de peliculas
            results = MovieDBClient.getMovies(actualOrder, popularity, rating).getMovies();
            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(results.size());
            int updatedMovies = 0;
            int rowsInserted = 0;
            int reviewsInserted = 0;
            int trailersInserted = 0;

            Iterator<Movie> movieIterator = results.iterator();
            while (movieIterator.hasNext()){
                Movie movie = movieIterator.next();

                //Check if movie is stored on database
                Cursor movieCursor = mContext.getContentResolver().query(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        MoviesContract.MovieEntry._ID + " = " + movie.getMovieId(),
                        null,
                        null
                );


               if (movieCursor.getCount() < 1){

                   Movie detailedMovie = MovieDBClient.getMovieById(movie.getMovieId());

                   movie.setMovieRuntime(detailedMovie.getMovieRuntime());

                   //Movie is not persisted
                   ContentValues contentValues = new ContentValues();
                   movieIntoContentValues(movie, actualOrder, popularity, rating, contentValues);

                   mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI,
                           contentValues);
                   rowsInserted++;

                   //get reviews from web service
                   List<Review> reviews = MovieDBClient.getMovieReviewsById(movie.getMovieId()).getReviews();

                   Iterator<Review> reviewIterator = reviews.iterator();
                   while(reviewIterator.hasNext()){
                       //iterate over reviews and insert into database
                       contentValues = reviewIntoContentValues(reviewIterator.next(), movie.getMovieId());
                       mContext.getContentResolver().insert(MoviesContract.ReviewEntry.CONTENT_URI,
                               contentValues);
                       reviewsInserted++;
                   }

                   //get trailers from web service
                   List<Trailer> trailers = MovieDBClient.getMovieTrailersById(movie.getMovieId()).getTrailers();

                   Iterator<Trailer> trailerIterator = trailers.iterator();
                   while(trailerIterator.hasNext()){
                       //iterate over trailers
                       Trailer trailer = trailerIterator.next();
                       if(mContext.getResources().getString(R.string.movie_video_type).equals(trailer.getVideoType()) &&
                                mContext.getResources().getString(R.string.movie_video_site).equals(trailer.getVideoSite())){
                           //Only store trailers on YouTube
                           contentValues = trailerIntoContentValues(trailer, movie.getMovieId());
                           mContext.getContentResolver().insert(MoviesContract.TrailerEntry.CONTENT_URI,
                                   contentValues);
                           trailersInserted++;
                       }
                   }


               }else{

                   //Movie already exists in database
                   ContentValues contentValues = new ContentValues();
                   //Check if movie needs to be updated
                   boolean updateNeeded = movieIntoContentValues(movieCursor, actualOrder, popularity, rating, contentValues);

                   if(updateNeeded){
                       //update movie in database
                       mContext.getContentResolver().update(MoviesContract.MovieEntry.CONTENT_URI,
                               contentValues,
                               MoviesContract.MovieEntry._ID + " = " + movie.getMovieId(),
                               null
                       );
                       updatedMovies++;
                   }
               }


            }


            Log.d(LOG_TAG, "FetchMovies Complete. " + rowsInserted + " movies inserted, " + trailersInserted + " trailers inserted, "
                    + reviewsInserted + " reviewsInserted, " + updatedMovies + " movies updated");

        }catch (RetrofitError error){

            String where = "";
            if(actualOrder.equalsIgnoreCase(popularity)){
                where = MoviesContract.MovieEntry.COLUMN_NAME_POPULAR + " = 1";
            }else if(actualOrder.equalsIgnoreCase(rating)){
                where = MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED + " = 1";
            }

            //Check if there are movies in database
            Cursor movieCursor = mContext.getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    where,
                    null,
                    null
            );

            //If there was an error and there are no movies in database, show error layout
            if(movieCursor==null ||  movieCursor.getCount() <= 0){
                mRetrofitError = error;
            }
        }
        return null;
    }

    /**
     * Load a movie into ContentValue according to the current order parameter
     * @param movie The movie
     * @param actualOrder Actual order preference
     * @param popularityOrder Popularity preference
     * @param ratedOrder Highest rated preference
     * @param contentValues Content values to fill
     */
    private void movieIntoContentValues(Movie movie, String actualOrder, String popularityOrder, String ratedOrder, ContentValues contentValues){

        contentValues.put(MoviesContract.MovieEntry._ID, Integer.parseInt(movie.getMovieId()));
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_TITLE, movie.getMovieTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
                DateUtils.getMillisecondsFromStringDate(movie.getMovieReleaseDate()));
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_POSTER, movie.getMovieImageUrl());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getMovieRating());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_SYNOPSIS, movie.getMovieOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_FAVOURITE, 0);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_RUNTIME, movie.getMovieRuntime());

        if(actualOrder.equals(popularityOrder)){
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_POPULAR, 1);
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED, 0);
        }else if(actualOrder.equals(ratedOrder)){
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_POPULAR, 0);
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED, 1);
        }
    }

    /**
     * Load a cursor into ContentValue according to the current order parameter
     * @param movieCursor The movie cursor
     * @param actualOrder Actual order preference
     * @param popularityOrder Popularity preference
     * @param ratedOrder Highest rated preference
     * @param contentValues Content values to fill
     */
    private boolean movieIntoContentValues(Cursor movieCursor, String actualOrder, String popularityOrder, String ratedOrder, ContentValues contentValues){

        boolean updateNeeded = false;
        movieCursor.moveToFirst();
        int indexPopular = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_POPULAR);
        int indexRated = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED);
        int popularValue = movieCursor.getInt(indexPopular);
        int ratedValue = movieCursor.getInt(indexRated);

        if(actualOrder.equals(popularityOrder) && popularValue == 0){
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_POPULAR, 1);
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED, 0);
            updateNeeded = true;
        }else if(actualOrder.equals(ratedOrder) && ratedValue == 0){
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_POPULAR, 0);
            contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED, 1);
            updateNeeded = true;
        }

        return updateNeeded;
    }

    /**
     * Load a review into a ContentValue
     * @param review The review
     * @param movieId The id movie for this review
     * @return The content value
     */
    private ContentValues reviewIntoContentValues(Review review, String movieId){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.ReviewEntry.COLUMN_NAME_AUTHOR, review.getReviewAuthor());
        contentValues.put(MoviesContract.ReviewEntry.COLUMN_NAME_CONTENT, review.getReviewContent());
        contentValues.put(MoviesContract.ReviewEntry.COLUMN_NAME_MOVIE_ID, movieId);
        contentValues.put(MoviesContract.ReviewEntry.COLUMN_NAME_URL, review.getReviewUrl());

        return contentValues;
    }

    /**
     * Load a trailer into a ContentValue
     * @param trailer The trailer
     * @param movieId The id movie for this review
     * @return The content value
     */
    private ContentValues trailerIntoContentValues(Trailer trailer, String movieId){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.TrailerEntry.COLUMN_NAME_KEY, trailer.getVideoKey());
        contentValues.put(MoviesContract.TrailerEntry.COLUMN_NAME_NAME, trailer.getVideoName());
        contentValues.put(MoviesContract.TrailerEntry.COLUMN_NAME_MOVIE_ID, movieId);
        contentValues.put(MoviesContract.TrailerEntry.COLUMN_NAME_SIZE, trailer.getVideoSize());

        return contentValues;
    }


    @Override
    protected void onPostExecute(Void data) {
        String alertTitle;
        String alertMessage;
        String alertButtonLabel = null;

        if(mRetrofitError != null && mRootView != null){
            //[en] There's an exception fetching movie detail
            //[es] Hay una excepción obteniendo el detalle de la película

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            alertTitle = mContext.getString(R.string.error_title_dialog);
            alertButtonLabel = mContext.getString(R.string.error_ok_button);

            if(mRetrofitError.getKind() == RetrofitError.Kind.NETWORK
                    && !Utils.internetConnectionAvailable(connectivityManager)){
                //[en] If there's a network exception check if there's internet connection. If not,
                //show alert dialog with no_internet_connection text
                //[es] Si hay una excepción de red se comprueba si hay conexión a internet. Si no
                //hay se muestra un diálogo de alerta con la cadena no_internet_connection_text
                alertMessage = mContext.getString(R.string.error_no_internet_connection);
            }else{
                //[en] There's not network error or there's internet connection available. Show
                // alert dialog error_unexpected_error text
                //[es] No hay error en la red o hay conexión a internet disponible. Mostrar el
                //diálogo de alerta con el texto error_unexpected_error
                alertMessage = mContext.getString(R.string.error_unexpected_error);
            }


            //[en] Create alert dialog with ok button.
            //[es] Crear diálogo de alerta con el botón de ok
            new AlertDialog.Builder(mContext)
                    .setTitle(alertTitle)
                    .setMessage(alertMessage)
                    .setPositiveButton(alertButtonLabel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();


            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            ImageView errorImageView = (ImageView) mRootView.findViewById(R.id.error_image);
            errorImageView.setImageResource(R.drawable.error_cross);
            TextView errorTextView = (TextView) mRootView.findViewById(R.id.error_text);
            errorTextView.setText(R.string.error_unexpected_error);
            mRootView.findViewById(R.id.movies_grid).setVisibility(View.GONE);

        } else {
            mRootView.findViewById(R.id.error_layout).setVisibility(View.GONE);
            mRootView.findViewById(R.id.movies_grid).setVisibility(View.VISIBLE);
        }
    }

}