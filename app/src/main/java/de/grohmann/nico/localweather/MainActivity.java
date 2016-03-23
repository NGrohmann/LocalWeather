package de.grohmann.nico.localweather;

import java.net.*;
import java.io.*;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.location.LocationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    //global variables
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    String locality = "";
    String weather = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //TextViews to set
        final TextView currentLocationView = (TextView) findViewById(R.id.currentCity);
        final TextView currentTemperatureValue = (TextView) findViewById(R.id.temperatureValue);
        final TextView currentHumidityValue = (TextView) findViewById(R.id.humidityValue);
        final TextView currentWindValue = (TextView) findViewById(R.id.windValue);
        final TextView currentPressureValue = (TextView) findViewById(R.id.pressureValue);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();

        //request permissions for location service and internet
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, REQUEST_CODE_ASK_PERMISSIONS);


        //current locations is requested and saved in variable currentLocation
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        currentLocationView.setText(currentLocation.getLatitude() + "");

        //longitude and latitude is converted to belonging city
        try {
            List<Address> address = geoCoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            for (int i = 0; i < address.size(); i++) {
                currentLocationView.setText(address.get(0).getLocality());
                locality = address.get(0).getLocality();
            }
        } catch (IOException io) {
            currentLocationView.setText("FAIL");
        }

        //Thread for request of weather data
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherHttpClient client = new WeatherHttpClient();
                locality = locality.replaceAll(" ", "");
                weather = client.getWeatherData(locality);
                try {
                    JSONObject json = new JSONObject(weather);
                    currentTemperatureValue.setText(json.getJSONObject("main").getString("temp") + " \u00b0C");
                    currentHumidityValue.setText(json.getJSONObject("main").getString("humidity") + " %");
                    currentWindValue.setText(json.getJSONObject("wind").get("speed") + " km/h");
                    currentPressureValue.setText(json.getJSONObject("main").getString("pressure") + " hpa");

                    interrupt();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

            public void interrupt() {
                if (Thread.currentThread() != null)
                    Thread.currentThread().interrupt();
            }

        });
        thread.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
