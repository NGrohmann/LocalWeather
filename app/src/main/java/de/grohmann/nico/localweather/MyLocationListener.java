package de.grohmann.nico.localweather;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by nicogrohmann on 21.03.16.
 */

public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        location.getLatitude();
        location.getLongitude();

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}