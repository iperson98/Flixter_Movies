package com.example.amade002.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amade002.flicks.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.example.amade002.flicks.MovieListActivity.API_BASE_URL;
import static com.example.amade002.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    // the movie to display
    Movie movie;
    //instance fields
    AsyncHttpClient client;

    // key used for passing data between activities
    public final static String MOVIE_ID = "movie_ID";

    // a numeric code to identify the edit activity
    public final static int VIDEO_REQUEST_CODE = 20;


    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvOverview)
    TextView tvOverview;
    @BindView(R.id.rdView)
    TextView rdView;
    @BindView(R.id.rbVoteAverage)
    RatingBar rbVoteAverage;

    // tag for logging from this activity
    public final static String TAG = "MovieDetailsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        //resolve the view objects
        // ***This now happens before this class w/ @BindView


        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        Intent intent = getIntent();

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        //tvReleaseDate.setText(getResources().getString(R.string.release_date, intent.getStringExtra("release_date")));
        rdView.setText(movie.getReleaseDate());


        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // initialize the client
        client = new AsyncHttpClient();

        // Get Video
        getVideo();

    }

    private void getVideo() {

        // resolve the player view from the layout
        final YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youtubeplayer);

        // create the url
        String url = API_BASE_URL + "/movie/" + movie.getId() + "/videos";

        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API key, always required

        final String[] youtube_key = {null};
        // execute a GET request expecting a JSON object report
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray results = null;
                try {

                    results = response.getJSONArray("results");
                    JSONObject movie_video = results.getJSONObject(0);
                    youtube_key[0] = movie_video.getString("key");

                    // initialize with API key stored in secrets.xml
                    playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            // do any work here to cue video, play video, etc.
                            youTubePlayer.cueVideo(youtube_key[0]);
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            // log the error
                            Log.e("MovieTrailerActivity", "Error initializing YouTube player");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from movies_id endpoint", throwable, true);
            }
        });
    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }




}
