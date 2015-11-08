package es.josepul.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "popularmovies.db";

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        //Create a table to hold movies
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " ("+
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + " INTEGER NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_SYNOPSIS + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_FAVOURITE + " INTEGER NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_POPULAR + " INTEGER NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_HIGHEST_RATED + " INTEGER NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_NAME_RUNTIME + " INTEGER," +

                " UNIQUE (" + MoviesContract.MovieEntry.COLUMN_NAME_TITLE + ") " +
                " ON CONFLICT REPLACE, " +
                " UNIQUE (" + MoviesContract.MovieEntry._ID + ") " +
                " ON CONFLICT REPLACE);";

        //Create a table to hold reviews
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewEntry.TABLE_NAME + " ("+
                MoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.ReviewEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_NAME_URL + " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + MoviesContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + "(" + MoviesContract.MovieEntry._ID + "));";

        //Create a table to hold trailers
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.TrailerEntry.TABLE_NAME + " ("+
                MoviesContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.TrailerEntry.COLUMN_NAME_KEY + " TEXT NOT NULL," +
                MoviesContract.TrailerEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                MoviesContract.TrailerEntry.COLUMN_NAME_SIZE + " INTEGER NOT NULL," +
                MoviesContract.TrailerEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + MoviesContract.TrailerEntry.COLUMN_NAME_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + "(" + MoviesContract.MovieEntry._ID + "));";

        //Execute SQL sentences to create tables
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
