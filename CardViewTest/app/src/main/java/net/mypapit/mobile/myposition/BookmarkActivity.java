package net.mypapit.mobile.myposition;
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
 * BookmarkActivity.java
 * For Displaying list of saved location / location bookmarks
 * My GPS Location Tool
 */
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import net.mypapit.mobile.myposition.db.VenueBookmark;
import net.mypapit.mobile.myposition.model.BookmarkAdapter;
import net.mypapit.mobile.myposition.model.Venue;
import net.mypapit.mobile.myposition.model.VenueDistanceComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import garin.artemiy.compassview.library.CompassSensorsActivity;
//import sky2limit.compassview.lib.CompassSensorManager;

public class BookmarkActivity extends CompassSensorsActivity {
    ArrayList<Venue> venueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);


        double lat = 0.0;
        double lng = 0.0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!bundle.isEmpty()) {

                lat = bundle.getDouble("lat");
                lng = bundle.getDouble("lng");


            }
        }

        final ListView lv = findViewById(R.id.bookmarklistview);

        Location current = new Location("Current");
        current.setLatitude(lat);
        current.setLongitude(lng);

        List<VenueBookmark> venueBookmarkList = VenueBookmark.listAll(VenueBookmark.class);

        venueList = new ArrayList<>();

        for (VenueBookmark v : venueBookmarkList) {
            venueList.add(new Venue(v.name, v.address, v.lat, v.lng, v.distance, v.getId()).calculateDistance(lat, lng));

        }

        Collections.sort(venueList, new VenueDistanceComparator());

        // CompassSensorManager manager = new CompassSensorManager(this);


        final BookmarkAdapter bookmarkadapter = new BookmarkAdapter(this, venueList, current);
        lv.setAdapter(bookmarkadapter);

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(lv),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }


                            @Override
                            public void onPendingDismiss(ListViewAdapter recyclerView, int position) {

                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                bookmarkadapter.remove(position);
                                // bookmarkadapter.removeData(position);

                            }
                        });
        touchListener.setDismissDelay(3000);


        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    //Toast.makeText(getApplicationContext(), "Position " + position, Toast.LENGTH_SHORT).show();
                    String x = "geo:0,0?q=-33.8666,151.1957(Google+Sydney)";
                    Venue venue = venueList.get(position);
                    double lat = venue.location.lat;
                    double lng = venue.location.lng;


                    StringBuilder sb = new StringBuilder("geo:");
                    sb.append(lat).append(",").append(lng).append("?q=").append(lat).append(",").append(lng);
                    sb.append("(").append(venue.name).append(")");

                    Uri intentUri = Uri.parse(sb.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                    startActivity(mapIntent);


                }
            }
        });


        initAdsToolbar();


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initAdsToolbar() {
        MobileAds.initialize(getApplicationContext(), getString(R.string.app_adview_id));
        final AdView mAdView = findViewById(R.id.adViewBookmark);
        AdRequest adRequest = new AdRequest.Builder().build();

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


        Toolbar toolbar = findViewById(R.id.bookmarktoolbar);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbar_bookmark);

        }


    }

}
