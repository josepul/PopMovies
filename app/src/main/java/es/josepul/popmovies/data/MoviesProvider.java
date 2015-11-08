package es.josepul.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Jose on 31/10/2015.
 */
public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int REVIEW = 200;
    static final int REVIEW_WITH_ID = 201;
    static final int TRAILER = 300;
    static final int TRAILER_WITH_ID = 301;

    private static final SQLiteQueryBuilder sReviewByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sTrailerByMovieIdQueryBuilder;

    static{
        sReviewByMovieIdQueryBuilder = new SQLiteQueryBuilder();
        sTrailerByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        sReviewByMovieIdQueryBuilder.setTables(
                MoviesContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.ReviewEntry.TABLE_NAME +
                        "." + MoviesContract.ReviewEntry.COLUMN_NAME_MOVIE_ID +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry._ID);

        sTrailerByMovieIdQueryBuilder.setTables(
                MoviesContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.TrailerEntry.TABLE_NAME +
                        "." + MoviesContract.TrailerEntry.COLUMN_NAME_MOVIE_ID +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry._ID);
    }

    //location.location_setting = ?
    private static final String sMovieSelection =
            MoviesContract.MovieEntry.TABLE_NAME+
                    "." + MoviesContract.MovieEntry._ID + " = ? ";

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_REVIEW + "/", REVIEW);
        matcher.addURI(authority, MoviesContract.PATH_REVIEW + "/*", REVIEW_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TRAILER + "/", TRAILER);
        matcher.addURI(authority, MoviesContract.PATH_TRAILER + "/*", TRAILER_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    private Cursor getReviewsByMovieId(Uri uri, String[] projection, String sortOrder) {
        String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = sMovieSelection;

        return sReviewByMovieIdQueryBuilder.query(moviesDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailersByMovieId(Uri uri, String[] projection, String sortOrder) {
        String movieId = MoviesContract.TrailerEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = sMovieSelection;

        return sTrailerByMovieIdQueryBuilder.query(moviesDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case MOVIE : {
                retCursor = moviesDbHelper.getReadableDatabase().query(
                    MoviesContract.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID : {
                String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);

                selectionArgs = new String[]{movieId};
                selection = sMovieSelection;
                retCursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILER : {
                retCursor = getTrailersByMovieId(uri, projection, sortOrder);
                break;
            }
            case REVIEW : {
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }
            case REVIEW_WITH_ID : {
                String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);
                retCursor = sReviewByMovieIdQueryBuilder.query(moviesDbHelper.getReadableDatabase(),
                        projection,
                        sMovieSelection,
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILER_WITH_ID : {
                String movieId = MoviesContract.TrailerEntry.getMovieIdFromUri(uri);
                retCursor = sTrailerByMovieIdQueryBuilder.query(moviesDbHelper.getReadableDatabase(),
                        projection,
                        sMovieSelection,
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        //Use the Uri Matcher to determine what kind of URI this is
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case TRAILER:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                }else{
                    throw  new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(_id);
                }else{
                    throw  new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER: {
                long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri = MoviesContract.TrailerEntry.buildTrailerUri(_id);
                }else{
                    throw  new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deletedRows;

        if(null == selection){
            selection = "1";
        }

        switch (match){
            case MOVIE: {
                deletedRows = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                deletedRows = db.delete(MoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                deletedRows = db.delete(MoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;

        switch (match) {
            case MOVIE: {
                count = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                count = db.update(MoviesContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                count = db.update(MoviesContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(count != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
