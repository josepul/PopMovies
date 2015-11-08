package es.josepul.popmovies.client;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import es.josepul.popmovies.model.Movie;
import es.josepul.popmovies.model.Movies;
import es.josepul.popmovies.model.Reviews;
import es.josepul.popmovies.model.Trailers;
import es.josepul.popmovies.util.MovieDBConstants;
import retrofit.RestAdapter;

/**
 * Created by Jose on 12/07/2015.
 */
public class MovieDBClient {

    private final static String LOG_TAG = MovieDBClient.class.getSimpleName();

    /**
     * Returns an initialized RestAdapter object
     * @return The RestAdapter object
     */
    private static RestAdapter createWebService(){
        //[en] Create RestAdapter object with themoviedb endpoint
        //[es] Crea el objeto RestAdapter con el enpdpoint de themoviedb
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MovieDBConstants.MOVIE_DB_API_BASE)
                .build();
        return restAdapter;
    }

    /**
     * Get movies from themoviedb with Retrofit.
     * @param actualOrder Actual order criteria
     * @param popularity Order by popularity value in preferences
     * @param rating Orber by rating value in preferences
     * @return The movies from themoviedb
     */
    public static Movies getMovies(String actualOrder, String popularity, String rating){

        Movies result = null;

        RestAdapter restAdapter = createWebService();

        //[en] Params map with API key
        //[es] Mapa de par�metros con la API key
        Map<String, String> params = new HashMap<String, String>();
        params.put(MovieDBConstants.MOVIE_DB_API_KEY_PARAM, MovieDBConstants.MOVIE_DB_API_KEY_VALUE);

        if (actualOrder.equalsIgnoreCase(popularity)) {
            //[en] Add params to order by popularity
            //[es] A�adir par�metros para ordenar por popularidad
            params.put(MovieDBConstants.MOVIE_DB_SORT_PARAM, MovieDBConstants.MOVIE_DB_SORT_POPULARITY);
        } else if (actualOrder.equalsIgnoreCase(rating)){
            //[en] Add params to order by highest rated movies
            //[es] A�adir par�metros para ordenar por las pel�culas m�s valoradas
            params.put(MovieDBConstants.MOVIE_DB_SORT_PARAM, MovieDBConstants.MOVIE_DB_SORT_HIGHEST_RATED);
            params.put(MovieDBConstants.MOVIE_DB_VOTE_COUNT_PROPERTY, MovieDBConstants.MOVIE_DB_MIN_VOTES_COUNT);
        } else {
            Log.d(LOG_TAG, "Order criteria not found: " + actualOrder);
        }

        //[en] Create service with retrofit
        //[es]Crear el servicio con retrofit
        WebServiceMovies cliente = restAdapter.create(WebServiceMovies.class);

        //[en] Call service
        //[es] Llamada al servicio
        result = cliente.moviesByCriteria(params);

        return result;
    }

    /**
     * Get movie from themoviedb with Retrofit.
     * @param movieId Movie id to get
     * @return The movie from themoviedb
     */
    public static Movie getMovieById(String movieId){
        Movie result = null;

        RestAdapter restAdapter = createWebService();

        //[en] Params map with API key
        //[es] Mapa de par�metros con la API key
        Map<String, String> params = new HashMap<String, String>();
        params.put(MovieDBConstants.MOVIE_DB_API_KEY_PARAM, MovieDBConstants.MOVIE_DB_API_KEY_VALUE);

        //[en] Create service with retrofit
        //[es]Crear el servicio con retrofit
        WebServiceMovies cliente = restAdapter.create(WebServiceMovies.class);

        //[en] Call service
        //[es] Llamada al servicio
        result = cliente.movieById(movieId, params);

        return result;
    }

    /**
     * Get videos from themoviedb with Retrofit.
     * @param movieId Movie id to fetch videos
     * @return Videos from themoviedb
     */
    public static Trailers getMovieTrailersById(String movieId){
        Trailers result = null;

        RestAdapter restAdapter = createWebService();

        //[en] Params map with API key
        //[es] Mapa de parámetros con la API key
        Map<String, String> params = new HashMap<String, String>();
        params.put(MovieDBConstants.MOVIE_DB_API_KEY_PARAM, MovieDBConstants.MOVIE_DB_API_KEY_VALUE);

        //[en] Create service with retrofit
        //[es]Crear el servicio con retrofit
        WebServiceMovies cliente = restAdapter.create(WebServiceMovies.class);

        //[en] Call service
        //[es] Llamada al servicio
        result = cliente.movieVideosById(movieId, params);

        return result;
    }

    /**
     * Get reviews from themoviedb with Retrofit.
     * @param movieId Movie id to fetch reviews
     * @return Reviews from themoviedb
     */
    public static Reviews getMovieReviewsById(String movieId){
        Reviews result = null;

        RestAdapter restAdapter = createWebService();

        //[en] Params map with API key
        //[es] Mapa de parámetros con la API key
        Map<String, String> params = new HashMap<String, String>();
        params.put(MovieDBConstants.MOVIE_DB_API_KEY_PARAM, MovieDBConstants.MOVIE_DB_API_KEY_VALUE);

        //[en] Create service with retrofit
        //[es]Crear el servicio con retrofit
        WebServiceMovies cliente = restAdapter.create(WebServiceMovies.class);

        //[en] Call service
        //[es] Llamada al servicio
        result = cliente.movieReviewsById(movieId, params);

        return result;
    }
}
