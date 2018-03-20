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
 * NearbyVideoMapActivity.java
 * NearbyVideoMapActivity Class - Activity for listing the nearby Youtube Videos on Google Map
 * My GPS Location Tool
 */

package net.mypapit.mobile.myposition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import net.mypapit.mobile.video.VideoInfo;

import java.security.SecureRandom;
import java.util.HashMap;


public class NearbyVideoMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    HashMap<Marker, VideoInfo> hmap;
    UiSettings uisettings;
    private GoogleMap mMap;
    private InterstitialAd mInterstitialAd;
    private String json = "[]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_video_map);


        initToolbar();
        initAds();
        hmap = new HashMap<>(20);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!bundle.isEmpty()) {
                json = bundle.getString("json");

            }
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void initAds() {

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_adview_id));
        final AdView mAdView = findViewById(R.id.adViewMap);

        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "T");

        AdRequest adRequest = new AdRequest.Builder().setIsDesignedForFamilies(false)
                .addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);


            }
        });
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intersitial));

        mInterstitialAd.loadAd(adRequest);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        final Gson gson = new Gson();
        final VideoInfo vinfo[] = gson.fromJson(this.json, VideoInfo[].class);
        mMap = googleMap;
        LatLng latlng = new LatLng(-34, 151);

        uisettings = mMap.getUiSettings();

        uisettings.setZoomControlsEnabled(true);


        for (VideoInfo video : vinfo) {
            if (video.getRecordingDetails() != null) {
                if (video.getRecordingDetails().getLocation() != null) {
                    latlng = new LatLng(
                            video.getRecordingDetails().getLocation().getLatitude(),
                            video.getRecordingDetails().getLocation().getLongitude());

                    Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(video.getSnippet().getChannelTitle())
                            .snippet(video.getSnippet().getTitle())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_youtube))

                    );

                    hmap.put(marker, video);

                }


            }


        }

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException sex) {

            Snackbar.make(new CoordinatorLayout(this), R.string.grant_access_snackbar, Snackbar.LENGTH_SHORT);

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13.0f));
        mMap.setOnInfoWindowClickListener(this);


    }

    protected void onPause() {
        super.onPause();

        SecureRandom random = new SecureRandom();

        int data = random.nextInt(12);

        if (data > 5) {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
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

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_videomap);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbarvideo_layout);
        //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        //AppCompatImageView image = findViewById(R.id.image_videoheader);

        //image.setImageResource(R.drawable.film_bg);


        setSupportActionBar(toolbar);

        //toolbar.setOnMenuItemClickListener(this);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setTitle(R.string.toolbar_sharelocation);

        }


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        VideoInfo vinfo = hmap.get(marker);

        String vid = vinfo.getId();

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
}
