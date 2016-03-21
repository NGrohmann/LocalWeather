package de.grohmann.nico.localweather;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView currentLocationView = (TextView) findViewById(R.id.currentCity);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        MyLocationListener locationListener = new MyLocationListener();
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, REQUEST_CODE_ASK_PERMISSIONS);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentLocationView.setText("SUCCESS");

        } catch (SecurityException e){
            currentLocationView.setText("FAIL");
        }

        currentLocationView.setText(currentLocation.getLatitude()+"");
        try {
            List<Address> address = geoCoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
          for (int i = 0; i < address.size(); i++){
              currentLocationView.setText(address.get(0).getLocality());
          }
        } catch (IOException io){
            currentLocationView.setText("FAIL");
        }


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
