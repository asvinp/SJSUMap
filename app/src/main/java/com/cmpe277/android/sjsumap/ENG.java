package com.cmpe277.android.sjsumap;

/**
 * Created by Asvin on 10/24/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ENG extends AppCompatActivity implements GeoTask.Geo, LocationListener{

    protected LocationManager locationManager;

    Button btn_get;
    String str_from, str_to;
    TextView tv_result1, tv_result2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eng);

        //Set action bar back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize
        btn_get = (Button) findViewById(R.id.engbutton);
        tv_result1 = (TextView) findViewById(R.id.duration);
        tv_result2 = (TextView) findViewById(R.id.distance);

        //getLocation
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

        //Listener on SVbutton
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.engbutton) {
                    //SV
                    Intent Intent = new Intent("com.cmpe277.android.sjsumap.ENGSV");
                    startActivity(Intent);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        str_from="" + location.getLatitude() + "," + location.getLongitude();
        str_to="37.337370,-121.881756";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + str_from + "&destinations=" + str_to + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyD00SLC1oLzNwpriLT2SeEMwpeQ2h7NMvM";
        new GeoTask(ENG.this).execute(url);
        //stop updating in order to save on limited API resources
        locationManager.removeUpdates(this);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void setValues(String result) {
        String res[]=result.split(",");
        Double min=Double.parseDouble(res[0])/60;
        Double dist=Double.parseDouble(res[1])/1609;
        tv_result1.setText(" Walking Duration= " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins");
        tv_result2.setText(" Walking Distance= " + String.format("%.2f", dist) + " miles");

    }

}