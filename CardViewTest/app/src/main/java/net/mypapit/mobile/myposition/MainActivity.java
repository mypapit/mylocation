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
 * MainActivity.java
 * MainActivity Class - this is the starting point for the application
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Picasso;

import net.mypapit.mobile.myposition.db.VenueBookmark;
import net.mypapit.mobile.myposition.model.PhotoRetrievedListener;
import net.mypapit.mobile.myposition.model.Photos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Toolbar.OnMenuItemClickListener, PhotoRetrievedListener {

    static final String TAG = "net.mypapit.demo";
    static final boolean mRequestingLocationUpdates = true;
    GoogleApiClient mGoogleApiClient;

    TextView txtAddress;


    Location globalLocation;

    TextView txtDecimalCoord, txtDegreeCoord;
    LocationRequest mLocationRequest;
    Location mCurrentLocation, mLastLocation;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar, toolbarAddress, toolbarDecimalCoord;
    AppCompatImageView image;
    boolean displayAds;
    FloatingActionButton fab;
    String mLocality, mAddress;
    int mLocalityStatus = Constants.UNKNOWN_RESULT;
    private AddressResultReceiver mResultReceiver;


    //private FusedLocationProviderClient mfusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ButterKnife.bind(this);
        initToolbar();

        mLocality = getString(R.string.unknown);
        mAddress = getString(R.string.unknown);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_adview_id));
        final AdView mAdView = findViewById(R.id.adView);
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


        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                if (globalLocation != null) {
                    bundle.putString("locality", mLocality);
                    bundle.putString("address", mAddress);
                    bundle.putDouble("lat", globalLocation.getLatitude());
                    bundle.putDouble("lng", globalLocation.getLongitude());
                } else {
                    bundle.putDouble("lat", 0.0);
                    bundle.putDouble("lng", 0.0);

                }
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, ShareActivity.class);
                startActivity(intent);

            }
        });

        mResultReceiver = new AddressResultReceiver(new Handler());
        // displayAds = false;

        globalLocation = null;


        toolbarAddress = findViewById(R.id.toolbarAddress);
        toolbarAddress.inflateMenu(R.menu.location_menu);
        toolbarAddress.setOnMenuItemClickListener(this);
        toolbarAddress.setTitle(getString(R.string.unknown));

        toolbarDecimalCoord = findViewById(R.id.toolbarDecimalCoord);
        toolbarDecimalCoord.setTitle("Coordinate");
        toolbarDecimalCoord.inflateMenu(R.menu.coord_menu);
        toolbarDecimalCoord.setOnMenuItemClickListener(this);


        txtDecimalCoord = findViewById(R.id.txtDecimalCoord);
        txtDegreeCoord = findViewById(R.id.txtDegreeCoord);

        txtAddress = findViewById(R.id.txtAddress);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar snackbar = Snackbar.make(findViewById(R.id.clayout), R.string.need_fine_location_access, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.grant_access_snackbar, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_CODE);
                    }
                });
                snackbar.show();

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_CODE);


            }

        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            Log.e(TAG, "GoogleApiClient != null");
        }

    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {

                handleNewLocation(mLastLocation);
                startIntentService(mLastLocation);


            }

            if (mLocationRequest == null) {
                createLocationRequest();
            }
            startLocationUpdates();


            Log.e(TAG, "Last Location == null");
        } catch (SecurityException sex) {

            Snackbar snackbar = Snackbar.make(findViewById(R.id.clayout), R.string.need_fine_location_access,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.grant_access_snackbar, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_CODE);
                }
            });
            snackbar.show();


        }
    }

    protected void startLocationUpdates() throws SecurityException {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }

    private void handleNewLocation(Location location, long time) {

        Date date = new Date(time);
        toolbar.setSubtitle(FuzzyDateTimeFormatter.getTimeAgo(getApplicationContext(), date));
        handleNewLocation(location);


    }

    private void handleNewLocation(Location newLocation) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(5);
        StringBuilder sb = new StringBuilder(nf.format(newLocation.getLatitude()));
        sb.append(" , ");
        sb.append(nf.format(newLocation.getLongitude()));


        txtDecimalCoord.setText(sb.toString());
        txtDegreeCoord.setText(this.toDegree(newLocation.getLatitude(), newLocation.getLongitude()));


        globalLocation = newLocation;
        Picasso.with(this).load(Constants.RANDOM_URL).into(image);


        toolbar.setSubtitle(FuzzyDateTimeFormatter.getTimeAgo(getApplicationContext(), new Date(newLocation.getTime())));

        if (newLocation != null && newLocation.distanceTo(mLastLocation) > 5000) {
            //new GetPhotoHeader(this, Constants.PANO_URL, newLocation.getLatitude(),newLocation.getLongitude()).execute();
            Picasso.with(this).load(Constants.RANDOM_URL).into(image);


        }


        //Picasso.with(this).load(Constants.RANDOM_URL).into(image);

        //new GetPhotoHeader(this, Constants.PANO_URL, newLocation.getLatitude(),newLocation.getLongitude()).execute();


        mLastLocation = newLocation;

    }


    @Override
    public void onConnectionSuspended(int i) {
        updateLocationTime();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        updateLocationTime();


    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.INTERVAL);
        mLocationRequest.setFastestInterval(Constants.STANDARD_INTERVAL);
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                          /*  status.startResolutionForResult(
                                    OuterClass.this,
                                    REQUEST_CHECK_SETTINGS);

                                    */
                            throw new IntentSender.SendIntentException();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;

                }
            }

        });


    }

    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {

            try {
                startLocationUpdates();
            } catch (SecurityException sex) {


            }


        }


    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        startIntentService(location);

        handleNewLocation(location, location.getTime());
    }

    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    public void onPhotoRetrieved(Photos photos) {
        //Picasso.with(getApplicationContext()).load(photos.photo_url).into(image);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
/*
            case R.id.shareLocation:
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.this_is_mylocation) + "\n" + txtAddress.getText());
                intent.setType("text/plain");
                startActivity(intent);
                return false;
  */

            case R.id.shareCoord:
                intent.setAction(Intent.ACTION_SEND);
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
                nf.setMaximumFractionDigits(5);
                String slat = nf.format(mLastLocation.getLatitude());
                String slng = nf.format((mLastLocation.getLongitude()));

                String sAll = slat + " N - " + slng + " E";
                String sURL = "https://maps.google.com/?q=" + slat + "," + slng;

                intent.putExtra(Intent.EXTRA_TEXT, "My Position: " + sAll + "\n" + sURL);
                intent.setType("text/plain");
                startActivity(intent);
                return false;
            case R.id.saveLocation:

                showBookmarkDialog();
                return false;

            case R.id.bookmark:
                //Toast.makeText(this, "Bookmark button", Toast.LENGTH_SHORT).show();
                intent.setClass(this, BookmarkActivity.class);

                Bundle bundle = new Bundle();

                if (globalLocation != null) {
                    bundle.putDouble("lat", globalLocation.getLatitude());
                    bundle.putDouble("lng", globalLocation.getLongitude());
                } else {
                    bundle.putDouble("lat", 0.0);
                    bundle.putDouble("lng", 0.0);

                }
                intent.putExtras(bundle);

                startActivity(intent);
                return false;


        }
        return false;
    }

    public void displayAddressOutput(String address, String locality) {

        toolbarAddress.setTitle(locality);
        txtAddress.setText(address);

//       getSupportActionBar().setTitle(locality);
        collapsingToolbarLayout.setTitle(locality);

        updateLocationTime();


    }

    public void updateLocationTime() {

        if (mCurrentLocation != null) {

            toolbar.setSubtitle(FuzzyDateTimeFormatter.getTimeAgo(getApplicationContext(), new Date(mCurrentLocation.getTime())));
        }

    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        image = findViewById(R.id.image);

        image.setImageResource(R.drawable.alorsetar);


        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    void setGlobalAddress(String mLocality, String mAddressOutput, int resultCode) {

        this.mLocality = mLocality;
        this.mAddress = mAddressOutput;
        this.mLocalityStatus = resultCode;


    }

    private void showBookmarkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bookmark title");


// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.bookmark_hint);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();

                if (m_Text.length() < 3) {
                    m_Text = toolbar.getTitle().toString();
                }
                VenueBookmark bookmark = new VenueBookmark(m_Text, txtAddress.getText().toString(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), 0.0);
                bookmark.save();


//                Toast.makeText(getApplicationContext(), "Bookmark saved", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.clayout), R.string.label_bookmark_saved,
                        Snackbar.LENGTH_SHORT).show();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        builder.show();

    }

    String toDegree(double lat, double lon) {
        StringBuilder stringb = new StringBuilder();

        LatLonConvert convert = new LatLonConvert(lat);

        stringb.append(new DecimalFormat("#").format(convert.getDegree())).append("\u00b0 ");
        stringb.append(new DecimalFormat("#").format(convert.getMinute())).append("\' ");
        stringb.append(new DecimalFormat("#.###").format(convert.getSecond())).append("\" , ");

        convert = new LatLonConvert(lon);

        stringb.append(new DecimalFormat("#").format(convert.getDegree())).append("\u00b0 ");
        stringb.append(new DecimalFormat("#").format(convert.getMinute())).append("\' ");
        stringb.append(new DecimalFormat("#.###").format(convert.getSecond())).append("\" , ");


        return stringb.toString();


    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            String mLocality = resultData.getString(Constants.RESULT_LOCALITY);
            displayAddressOutput(mAddressOutput, mLocality);


            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                setGlobalAddress(mLocality, mAddressOutput, resultCode);
                // Toast.makeText(getApplicationContext(), getString(R.string.address_found), Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.clayout), R.string.address_found,
                        Snackbar.LENGTH_SHORT).show();
            }


        }
    }


}






