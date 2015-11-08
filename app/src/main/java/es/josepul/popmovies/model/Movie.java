package es.josepul.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jose on 08/07/2015.
 */
public class Movie implements Parcelable {
    @SerializedName("poster_path")
    private String movieImageUrl;
    @SerializedName("id")
    private String movieId;
    @SerializedName("title")
    private String movieTitle;
    @SerializedName("overview")
    private String movieOverview;
    @SerializedName("release_date")
    private String movieReleaseDate;
    @SerializedName("vote_average")
    private String movieRating;
    @SerializedName("runtime")
    private String movieRuntime;

    private int movieFavourite;
    private int moviePopular;
    private int movieHighestRated;

    public Movie(){

    }

    public String getMovieImageUrl() {
        return movieImageUrl;
    }

    public void setMovieImageUrl(String movieImageUrl) {
        this.movieImageUrl = movieImageUrl;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getMovieRuntime() {
        return movieRuntime;
    }

    public void setMovieRuntime(String movieRuntime) {
        this.movieRuntime = movieRuntime;
    }

    public int getMovieFavourite() {
        return movieFavourite;
    }

    public void setMovieFavourite(int movieFavourite) {
        this.movieFavourite = movieFavourite;
    }

    public int getMoviePopular() {
        return moviePopular;
    }

    public void setMoviePopular(int moviePopular) {
        this.moviePopular = moviePopular;
    }

    public int getMovieHighestRated() {
        return movieHighestRated;
    }

    public void setMovieHighestRated(int movieHighestRated) {
        this.movieHighestRated = movieHighestRated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieImageUrl);
        dest.writeString(movieId);
        dest.writeString(movieTitle);
        dest.writeString(movieOverview);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieRating);
        dest.writeString(movieRuntime);
        dest.writeInt(movieFavourite);
        dest.writeInt(moviePopular);
        dest.writeInt(movieHighestRated);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };

    private Movie(Parcel source) {
        this.movieImageUrl = source.readString();
        this.movieId = source.readString();
        this.movieTitle = source.readString();
        this.movieOverview = source.readString();
        this.movieReleaseDate = source.readString();
        this.movieRating = source.readString();
        this.movieRuntime = source.readString();
        this.movieFavourite = source.readInt();
        this.moviePopular = source.readInt();
        this.movieHighestRated = source.readInt();
    }
}
