package es.josepul.popmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jose on 16/07/2015.
 */
public class Movies {

    @SerializedName("results")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
