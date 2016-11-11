package com.cmpe277.android.sjsumap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

/**
 * Created by Asvin on 10/31/2016.
 */

public class SPGSV extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sv_spg);

        //Set action bar back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up SV
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanoramaeng);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(new LatLng(37.3326232, -121.880525));

    }



}
