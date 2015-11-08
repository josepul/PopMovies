package es.josepul.popmovies.client;

import java.util.Map;

import es.josepul.popmovies.model.Movie;
import es.josepul.popmovies.model.Movies;
import es.josepul.popmovies.model.Reviews;
import es.josepul.popmovies.model.Trailers;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by Jose on 16/07/2015.
 */
public interface WebServiceMovies {

    @GET("/movie/{id}")
    public Movie movieById(@Path("id") String movieId, @QueryMap Map<String, String> params) throws RetrofitError;

    @GET("/discover/movie")
    public Movies moviesByCriteria(@QueryMap Map<String, String> params) throws RetrofitError;

    @GET("/movie/{id}/videos")
    public Trailers movieVideosById(@Path("id") String movieId, @QueryMap Map<String, String> params) throws RetrofitError;

    @GET("/movie/{id}/reviews")
    public Reviews movieReviewsById(@Path("id") String movieId, @QueryMap Map<String, String> params) throws RetrofitError;
}
