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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.location.LocationManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

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
        final TextView currentLocationView = (TextView) findViewById(R.id.currentCity);
        final TextView currentTemperatureValue = (TextView) findViewById(R.id.temperatureValue);
        final TextView currentHumidityValue = (TextView) findViewById(R.id.humidityValue);
        final TextView currentWindValue = (TextView) findViewById(R.id.windValue);
        final TextView currentRainValue = (TextView) findViewById(R.id.rainValue);

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
              locality = address.get(0).getLocality();
          }
        } catch (IOException io){
            currentLocationView.setText("FAIL");
        }
        class WeatherHttpClient {

            private String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

            public String getWeatherData(String location) {
                HttpURLConnection con = null;
                InputStream is = null;

                try {
                    con = (HttpURLConnection) (new URL(BASE_URL + location +"&units=metric&APPID=253ae690b051b772bb48d8b5eed6693b")).openConnection();
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.connect();
                    Log.d("API URL", con.getURL().toString());
                    // Let's read the response
                    StringBuffer buffer = new StringBuffer();
                    is = con.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null)
                        buffer.append(line + "\r\n");

                    is.close();
                    con.disconnect();
                    Log.d("IsEmpty", buffer.length()+"Hallo");
                    return buffer.toString();
                } catch (Throwable t) {
                    t.printStackTrace();
                    currentLocationView.setText("FEHLER");
                } finally {
                    try {
                        is.close();
                    } catch (Throwable t) {
                    }
                    try {
                        con.disconnect();
                    } catch (Throwable t) {
                    }
                }

                return null;

            }
        }



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    WeatherHttpClient client = new WeatherHttpClient();
                    locality = locality.replaceAll(" ", "");
                    weather = client.getWeatherData(locality);
                    try {
                        JSONObject json = new JSONObject(weather);
                        currentTemperatureValue.setText(json.getJSONObject("main").getString("temp")+" \u00b0C");
                        currentHumidityValue.setText(json.getJSONObject("main").getString("humidity")+" %");
                        currentWindValue.setText(json.getJSONObject("wind").get("speed")+ " km/h");


                        interrupt();
                    } catch (JSONException ex){
                        ex.printStackTrace();
                    }
                }

            public synchronized void interrupt(){
                if (Thread.currentThread()!= null)
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
