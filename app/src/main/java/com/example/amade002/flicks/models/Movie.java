package com.example.amade002.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by amade002 on 6/21/17.
 */

@Parcel
public class Movie {

    //values from API
    public String title;
    public String overview;
    public String posterPath; // only the path
    public String backdropPath;
    public Double voteAverage;
    public String releaseDate;
    public String id;



    // intialize from the JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
        releaseDate = object.getString("release_date");
        id = object.getString("id");
    }

    // no-arg, empty constructor required for the Parceler
    public Movie() {}


    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {

        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getId() {
        return id;
    }
}
