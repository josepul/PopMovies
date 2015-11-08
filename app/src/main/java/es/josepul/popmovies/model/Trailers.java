package es.josepul.popmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jose on 31/10/2015.
 */
public class Trailers {

    @SerializedName("results")
    private List<Trailer> trailers;

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
