package es.josepul.popmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jose on 31/10/2015.
 */
public class Reviews {

    @SerializedName("results")
    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
