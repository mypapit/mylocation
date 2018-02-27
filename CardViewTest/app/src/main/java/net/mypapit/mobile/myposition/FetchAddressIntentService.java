package net.mypapit.mobile.myposition;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MohammadHafiz on 18/6/2016.
 */
public class FetchAddressIntentService extends IntentService {

    static final String TAG = "mypapitx";
    protected ResultReceiver mReceiver;
    String errorMessage = "Unknown";

    public FetchAddressIntentService() {

        super("test");
    }


    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;


        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);

            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage, getString(R.string.unknown));
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            Log.d(Constants.DEBUG_TAG, "Max Address Size Index: " + address.getMaxAddressLineIndex());


            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
                // Log.d(Constants.DEBUG_TAG,address.getAddressLine(i));
                //Log.d(Constants.DEBUG_TAG,address.getAddressLine(i).length()+" each line " +i);

            }


            Log.i(TAG, getString(R.string.address_found));

            String locality = address.getLocality();

            if (locality == null || locality.length() < 2) {
                locality = address.getCountryName();
            }
            String combine = TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);

            Log.d(Constants.DEBUG_TAG, combine.length() + " combined");
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    combine, locality);
        }


    }

    private void deliverResultToReceiver(int resultCode, String message, String locality) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putString(Constants.RESULT_LOCALITY, locality);
        mReceiver.send(resultCode, bundle);
    }
}
