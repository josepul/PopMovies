package es.josepul.popmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jose on 31/10/2015.
 */
public class Trailer {
    @SerializedName("id")
    private String videoId;
    @SerializedName("key")
    private String videoKey;
    @SerializedName("name")
    private String videoName;
    @SerializedName("size")
    private String videoSize;
    @SerializedName("type")
    private String videoType;
    @SerializedName("site")
    private String videoSite;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoSite() {
        return videoSite;
    }

    public void setVideoSite(String videoSite) {
        this.videoSite = videoSite;
    }
}
