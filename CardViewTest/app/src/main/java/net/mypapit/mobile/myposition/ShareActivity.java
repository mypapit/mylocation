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
 * ShareActvity.java
 * List several location in the surrounding area for sharing location
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import net.mypapit.mobile.myposition.model.FoursquareResponse;
import net.mypapit.mobile.myposition.model.Meta;
import net.mypapit.mobile.myposition.model.ShareLocationAdapter;
import net.mypapit.mobile.myposition.model.Venue;
import net.mypapit.mobile.myposition.model.VenueDistanceComparator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShareActivity extends AppCompatActivity implements FourSquareDoneListener, Toolbar.OnMenuItemClickListener, AdapterView.OnItemClickListener {

    ListView lv;
    TextView tv;

    ArrayList<Venue> venueArrayList;
    Venue defaultVenue;
    private String CLIENT = "MyLocation/3.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Toolbar toolbar = findViewById(R.id.sharetoolbar);
        defaultVenue = new Venue(getString(R.string.unknown), getString(R.string.unknown), 0.0, 0.0, 0.0);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbar_sharelocation);

        }
        toolbar.setOnMenuItemClickListener(this);


        venueArrayList = new ArrayList<>();


        lv = findViewById(R.id.sharelistview);
        lv.setVisibility(ListView.GONE);
        lv.setEmptyView(findViewById(R.id.empty));
        lv.setOnItemClickListener(this);

        tv = findViewById(R.id.empty);
        tv.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!bundle.isEmpty()) {

                String locality = bundle.getString("locality", getString(R.string.unknown));
                String address = bundle.getString("address", getString(R.string.unknown));
                double lat = bundle.getDouble("lat");
                double lng = bundle.getDouble("lng");
                defaultVenue = new Venue(locality, address, lat, lng, 0.0);

                new GetLocationsList(this, lat, lng).execute();
                shareLocation();


            } else {

                new GetLocationsList(this).execute();

            }
        }


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // handle arrow click here
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.sharemapmenuitem:

                if (venueArrayList.size() < 1) {

                    Snackbar.make(new CoordinatorLayout(this), R.string.label_location_notdetected, Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                intent.setClass(this, ShareLocationMap.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("venues", (Serializable) (venueArrayList));
                intent.putExtra("venues", bundle);

                //Toast.makeText(this, "sharemapitem selected", Toast.LENGTH_SHORT).show();
                startActivity(intent);

                return true;
        }

        return false;
    }


    @Override
    public void doneLoading(Venue[] venue) {

        if (venue == null || venue.length < 1) {

            //Toast.makeText(this, "Location not detected", Toast.LENGTH_LONG).show();
            Snackbar.make(new CoordinatorLayout(this), R.string.label_location_notdetected, Snackbar.LENGTH_SHORT).show();


            return;

        }
        ArrayList<Venue> vlist = new ArrayList<Venue>(Arrays.asList(venue));
        vlist.add(defaultVenue);

        Collections.sort(vlist, new VenueDistanceComparator());

        ShareLocationAdapter slAdapter = new ShareLocationAdapter(this, vlist);

        lv.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);

        lv.setAdapter(slAdapter);

        this.venueArrayList = vlist;


    }

    public void shareLocation () {
        OkHttpClient client = new OkHttpClient();

        RequestBody formbody = new FormBody.Builder()
                .add("lat", defaultVenue.location.lat+"")
                .add("lng", defaultVenue.location.lng+"")
                .add("deviceid", Build.PRODUCT + " " + Build.MODEL)
                .add("locality", defaultVenue.name+"")
                .add("client", CLIENT)
                .build();
        Request request = new Request.Builder()
                .url("http://api.repeater.my/mylocation/endp.php")
                .post(formbody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OKHttp", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("OKHttp", "Success:" + response.body().string());

            }
        });



    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Venue venue = (Venue) lv.getItemAtPosition(position);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        StringBuilder buffer = new StringBuilder();
        buffer.append(getString(R.string.message_near)).append(venue.name);

        buffer.append("\n").append("http://maps.google.com/?q=");
        buffer.append(venue.location.lat).append(",").append(venue.location.lng);

        intent.putExtra(Intent.EXTRA_TEXT, buffer.toString());
        intent.setType("text/plain");
        startActivity(intent);


    }
}


class GetLocationsList extends AsyncTask<Void, Void, Venue[]> {
    Venue[] venues;
    FourSquareDoneListener fourSquareDoneListener;
    double lat, lng;

    GetLocationsList(FourSquareDoneListener listener, double lat, double lng) {
        this.fourSquareDoneListener = listener;
        this.lat = lat;
        this.lng = lng;

        Log.d("mypapit venue", "loc :" + lat + "," + lng);


    }


    GetLocationsList(FourSquareDoneListener listener) {
        this(listener, 6.12, 100.375);


    }



    @Override
    protected Venue[] doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.FOUR_URL).newBuilder();
        urlBuilder.addQueryParameter("ll", lat + "," + lng);
        urlBuilder.addQueryParameter("client_id", Constants.CLIENT_ID);
        urlBuilder.addQueryParameter("client_secret", Constants.CLIENT_SECRET);
        urlBuilder.addQueryParameter("v", "20140228");
        urlBuilder.addQueryParameter("limit", "25");
        urlBuilder.addQueryParameter("intent", "checkin");
        urlBuilder.addQueryParameter("radius", "1500");

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            final Gson gson = new Gson();

            //  JsonParser parser = new JsonParser();
            //JsonObject data = parser.parse(response.body().charStream()).getAsJsonObject();

            // Log.d("mypapit-json",response.body().string());

            Meta meta = gson.fromJson(response.body().charStream(), Meta.class);


            FoursquareResponse foursquareResponse = meta.response;


            //List<Venue> venues = foursquareResponse.getVenueList();

            venues = foursquareResponse.venues;

            //check if the Venues are not null
            /*
            if (venues != null) {
                for (Venue venue : venues) {

                    Log.d("Venue mypapit", venue.name + " - " + venue.location.distance + " meter" + "Address: " + venue.location.address);
                }
            }*/

        } catch (IOException ioex) {
            Log.e("mypapit", "Error client API URL request " + ioex.getMessage());

        }


        return venues;
    }

    protected void onPostExecute(Venue[] venue) {

        fourSquareDoneListener.doneLoading(venue);


    }
}



