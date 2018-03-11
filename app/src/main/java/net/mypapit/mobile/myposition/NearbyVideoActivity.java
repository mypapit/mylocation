package net.mypapit.mobile.myposition;

import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import net.mypapit.mobile.myposition.model.Meta;
import net.mypapit.mobile.myposition.model.Venue;
import net.mypapit.mobile.video.Snippet;
import net.mypapit.mobile.video.VideoInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearbyVideoActivity extends AppCompatActivity {

    double lat, lng;
    final String URL = "http://api.repeater.my/youtube/search.php?location=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_video);

        Bundle bundle = getIntent().getExtras();
        lat = 0.0;
        lng = 0.0;
        if (bundle != null) {
            if (!bundle.isEmpty()) {

                lat = bundle.getDouble("lat");
                lng = bundle.getDouble("lng");


            }
        }

        StringBuilder sb = new StringBuilder(URL);

        lat = dround(lat, 2);
        lng = dround(lng, 3);

        sb.append(lat).append(",").append(lng);
        String finalURL = sb.toString();

        new GetVideoVenueList(finalURL).execute();





    }

    public static double dround(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    static class GetVideoVenueList extends AsyncTask<Void, Void, VideoInfo[]> {

        VideoInfo[] videoList;
        String finalURL;
        public GetVideoVenueList(String finalURL){

            this.finalURL = finalURL;

        }

        @Override
        protected VideoInfo[] doInBackground(Void... voids) {
            OkHttpClient client;

            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();

                final Gson gson = new Gson();
                VideoInfo vinfo[] = gson.fromJson(response.body().charStream(), VideoInfo[].class);

                for (VideoInfo vfo : vinfo) {

                    Snippet snippet = vfo.getSnippet();

                    Log.d("vfo", snippet.getDescription());


                }

                return vinfo;


            } catch (java.io.IOException jioex) {

                Log.e("vfo", jioex.getMessage());

            }

            return new VideoInfo[1];
        }
    }
}