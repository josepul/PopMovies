package es.josepul.popmovies.util;

/**
 * Created by Jose on 10/07/2015.
 *
 * [en] Constants for themoviedb.org API
 * [es] Constantes para la API de rhwmoviedb.org
 */
public class MovieDBConstants {

    public static final String MOVIE_DB_API_KEY_PARAM = "api_key";

    public static final String MOVIE_DB_API_KEY_VALUE = "3b9174adea52a338e6cf7bef2e84fb62";

    public static final String MOVIE_DB_API_BASE = "http://api.themoviedb.org/3/";

    public static final String MOVIE_DB_DISCOVER_MOVIE_ENDPOINT = "discover/movie";

    public static final String MOVIE_DB_FIND_MOVIE_ENDPOINT = "movie/";

    public static final String MOVIE_DB_SORT_PARAM = "sort_by";

    public static final String MOVIE_DB_SORT_POPULARITY = "popularity.desc";

    public static final String MOVIE_DB_SORT_HIGHEST_RATED = "vote_average.desc";

    public static final String MOVIE_DB_RESULTS_PROPERTY = "results";

    public static final String MOVIE_DB_MOVIE_ID_PROPERTY = "id";

    public static final String MOVIE_DB_POSTER_PATH_PROPERTY = "poster_path";

    public static final String MOVIE_DB_TITLE_PROPERTY = "title";

    public static final String MOVIE_DB_OVERVIEW_PROPERTY = "overview";

    public static final String MOVIE_DB_RELEASE_DATE_PROPERTY = "release_date";

    public static final String MOVIE_DB_VOTE_AVERAGE_PROPERTY = "vote_average";

    public static final String MOVIE_DB_VOTE_COUNT_PROPERTY = "vote_count.gte";

    public static final String MOVIE_DB_MIN_VOTES_COUNT= "50";

    public static final String MOVIE_DB_POSTERS_BASE = "http://image.tmdb.org/t/p/w342/";

}
