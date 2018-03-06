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
 * ShareLocationMap.java
 * For Displaying Nearby location on a map.
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.SimpleArrayMap;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.mypapit.mobile.myposition.model.Venue;

import java.util.ArrayList;
import java.util.List;

public class ShareLocationMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    SimpleArrayMap<Marker, Venue> mark;
    private GoogleMap mMap;
    InterstitialAd  mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        MobileAds.initialize(getApplicationContext(), getString(R.string.app_adview_id));
        final AdView mAdView = findViewById(R.id.adViewMap);

        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating","T");

        AdRequest adRequest = new AdRequest.Builder().setIsDesignedForFamilies(false)
                                .addNetworkExtrasBundle(AdMobAdapter.class,extras).build();

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


        mark = new SimpleArrayMap<>();


        Toolbar toolbar = findViewById(R.id.sharemaptoolbar);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbar_sharemaplocation);

        }
        //toolbar.setOnMenuItemClickListener(this);

    }
protected void onPause(){
        super.onPause();

        if (mInterstitialAd.isLoaded()) {

            mInterstitialAd.show();

        }



}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException sex) {


        }


        mMap.setOnInfoWindowClickListener(this);

        Bundle bundle = getIntent().getBundleExtra("venues");
        List<Venue> vlist = (ArrayList<Venue>) bundle.getSerializable("venues");


        //LatLng sydney = new LatLng(-34, 151);
        if (vlist != null) {
            for (Venue venue : vlist) {

                MarkerOptions option = new MarkerOptions().position(new LatLng(venue.location.lat, venue.location.lng)).
                        title(venue.name).snippet(venue.location.address);
                Marker marker = mMap.addMarker(option);


                mark.put(marker, venue);


            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(vlist.get(0).location.lat, vlist.get(0).location.lng), 15));


        }


    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Venue venue = mark.get(marker);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        StringBuilder buffer = new StringBuilder();
        String address;

        buffer.append(getString(R.string.message_near)).append(venue.name).append("\n");

        if (venue.location.address == null) {
            address = "";
        } else {
            address = venue.location.address;
        }
        buffer.append(address).append("\n").append("http://maps.google.com/?q=");
        buffer.append(venue.location.lat).append(",").append(venue.location.lng);


        intent.putExtra(Intent.EXTRA_TEXT, buffer.toString());
        intent.setType("text/plain");
        startActivity(intent);
        //Toast.makeText(this,venue.location.address+" clicked",Toast.LENGTH_SHORT).show();


    }
}
