/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * MyLocation 3.0 for Android <mypapit@gmail.com> (9w2wtf)
 * Copyright 2018 Mohammad Hafiz bin Ismail. All rights reserved.
 *
 * Info url :
 * https://github.com/mypapit/mylocation/
 * http://kirostudio.com
 * http://blog.mypapit.net/
 *
 *
 * NearbyVideoActivity.java
 * NearbyVideoActivity Class - Activity for listing the nearby Youtube Videos.
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.mypapit.mobile.myposition.model.NearbyVideoAdapter;
import net.mypapit.mobile.video.Maxres;
import net.mypapit.mobile.video.Snippet;
import net.mypapit.mobile.video.VideoInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

interface VideoListListener {
    void doneLoading(String videloList);


}

public class NearbyVideoActivity extends AppCompatActivity implements VideoListListener, Toolbar.OnMenuItemClickListener {

    final String URL = "http://api.repeater.my/youtube/search.php?location=";
    double lat, lng;
    String venueString = "[]";
    Toolbar toolbar;
    RecyclerView recyclerView;

    public static double dround(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_video);

        initToolbar();

        recyclerView = findViewById(R.id.recycler_view);

        GridLayoutManager grid = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(grid);


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

        new GetVideoVenueList(finalURL, this).execute();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nearby_video_menu, menu);
        return true;
    }

    @Override
    public void doneLoading(String videoList) {
        final Gson gson = new Gson();
        final VideoInfo vinfo[] = gson.fromJson(videoList, VideoInfo[].class);
        this.venueString = videoList;


        Maxres headerImage = null;
        for (VideoInfo vfo : vinfo) {

            Snippet snippet = vfo.getSnippet();

            Maxres temp = snippet.getThumbnails().getAbsMaxres();
            if (temp != null) {

                headerImage = snippet.getThumbnails().getAbsMaxres();
            }


            Log.d("vfo", snippet.getDescription());


        }

        NearbyVideoAdapter adapter = new NearbyVideoAdapter(this, vinfo);

        adapter.setOnItemClickListener(new NearbyVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                String vid = vinfo[position].getId();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vid));

                Intent oldIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + vid));

                // Toast.makeText(getApplicationContext(), vid,Toast.LENGTH_SHORT).show();

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(oldIntent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        AppCompatImageView hImage = findViewById(R.id.image_videoheader);

        if (headerImage != null) {

            Picasso.with(this).load(headerImage.getUrl()).into(hImage);
        }


    }

    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent();
        Bundle bundle;

        switch (item.getItemId()) {

            case R.id.nearbyvideomap_menu:

                bundle = new Bundle();
                intent.setClass(this, NearbyVideoMapActivity.class);

                if (venueString != null) {
                    bundle.putString("json", venueString);
                }
                intent.putExtras(bundle);

                startActivity(intent);
                return false;


        }

        return false;
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar_video);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbarvideo_layout);
        //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        AppCompatImageView image = findViewById(R.id.image_videoheader);

        image.setImageResource(R.drawable.film_bg);


        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setTitle(R.string.toolbar_sharelocation);

        }


    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class GetVideoVenueList extends AsyncTask<Void, Void, String> {

        VideoInfo[] videoList;
        VideoListListener videoListListener;
        String finalURL;

        public GetVideoVenueList(String finalURL, VideoListListener videoListListener) {

            this.finalURL = finalURL;
            this.videoListListener = videoListListener;


        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client;

            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();

                //final Gson gson = new Gson();
                //VideoInfo vinfo[] = gson.fromJson(response.body().charStream(), VideoInfo[].class);

                if (response.body() == null) {
                    return null;
                }

                return response.body().string();


/*
                for (VideoInfo vfo : vinfo) {

                    Snippet snippet = vfo.getSnippet();

                    Log.d("vfo", snippet.getDescription());


                }
*/


            } catch (java.io.IOException jioex) {

                Log.e("vfo", jioex.getMessage());

            }

            return "[]";
        }

        protected void onPostExecute(String videoList) {

            videoListListener.doneLoading(videoList);


        }
    }


}
