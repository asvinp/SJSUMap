package com.cmpe277.android.sjsumap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


/**
 * This activity displays an image on the screen.
 * The image has six different regions that can be touched in order to open a new activity.
 * A button when pressed shows the current location of the user.
 * A search box is available to search for 6 buildings by name.
 *
 */

public class MapScreen extends AppCompatActivity implements View.OnTouchListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult>
{
    //Search bar
    String[] name = {"King Library", "Engineering Building", "Yoshihiro Uchida Hall", "Student Union", "BBC", "South Parking Garage"};
    AutoCompleteTextView search;

    //Pins for when searched
    private ImageView klpin, engpin, yuhpin, supin, bbcpin, spgpin;

    //Initialize permissions
    private static final int REQUEST_ERROR = 0;
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    //Declare variables for location etc.
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    LocationSettingsRequest locationSettingsRequest;
    private Location realLocation;
    private ImageView campusImage;
    private Bitmap bitmap;
    private boolean userLocation = false;
    private int currentX;
    private int currentY;

    //Initialize rectangle of SJSU coordinates according to Gmaps and campusimage
    private double rectangle_a_x = 37.335821;
    private double rectangle_a_y = -121.886024;
    private double rectangle_b_x = 37.338845;
    private double rectangle_b_y = -121.879701;
    private double rectangle_c_x = 37.331567;
    private double rectangle_c_y = -121.882837;
    private double rectangle_d_x = 37.334562;
    private double rectangle_d_y = -121.876486;
    private double lat = 0.0;
    private double lon = 0.0;

    //Initialize rectangle on mapScreen
    private int rectangle_1_x = 95;
    private int rectangle_1_y = 720;
    private int rectangle_2_x = 1300;
    private int rectangle_2_y = 720;
    private int rectangle_3_x = 95;
    private int rectangle_3_y = 1735;
    private int rectangle_4_x = 1300;
    private int rectangle_4_y = 1735;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        ImageView iv = (ImageView) findViewById (R.id.image);
        if (iv != null) {
            iv.setOnTouchListener (this);
        }

        // Make canvas of campusimage
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.campusmap,myOptions);

        //Set imagepins
        klpin = (ImageView) findViewById(R.id.klpin);
        engpin = (ImageView) findViewById(R.id.engpin);
        yuhpin = (ImageView) findViewById(R.id.yuhpin);
        supin = (ImageView) findViewById(R.id.supin);
        bbcpin = (ImageView) findViewById(R.id.bbcpin);
        spgpin = (ImageView) findViewById(R.id.spgpin);

        //Hides auto keyboard popup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //hide pins
        hidePins();

        //Autocomplete names for searchbox
        search = (AutoCompleteTextView) findViewById(R.id.searchbox);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,android.R.layout.simple_dropdown_item_1line,name);
        search.setThreshold(2);
        search.setAdapter(adapter);

        //Check if google services are working
        if(googleServicesAvailable()){
            Toast.makeText(this, "All good with Gservices", Toast.LENGTH_LONG).show();
        }

        toast ("Touch a building to get detailed information");

        campusImage = (ImageView) findViewById(R.id.image);
        if (campusImage != null) {
            campusImage.setOnTouchListener(this);
        }

        //On click of currentlocation button
        ImageButton currentloc = (ImageButton) findViewById(R.id.currentloc);
        currentloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inBounds(realLocation.getLatitude(), realLocation.getLongitude())) {
                    // Check boundaries, if in boundaries, calculate current location on image
                    pointLocation(realLocation.getLatitude(), realLocation.getLongitude());
                    userLocation = !userLocation;

                } else {
                    userLocation = false;
                    Toast.makeText(getApplicationContext(), "Are you on campus right now? Can't show location on map if not on campus. ", Toast.LENGTH_LONG).show();
                }
                drawCurrentLocation();
            }
        });

        buildGoogleApiClient();
        createLocationRequest();


        //Check searchbox for text and make pins visible (Ignores case-sensitivity)
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable no) {
                if (no.length()==0)
                    hidePins();

                else if(search.getText().toString().equalsIgnoreCase("King Library"))
                    klpin.setVisibility(View.VISIBLE);

                else if (search.getText().toString().equalsIgnoreCase("Engineering Building"))
                    engpin.setVisibility(View.VISIBLE);

                else if (search.getText().toString().equalsIgnoreCase("Yoshihiro Uchida Hall"))
                    yuhpin.setVisibility(View.VISIBLE);

                else if (search.getText().toString().equalsIgnoreCase("Student Union"))
                    supin.setVisibility(View.VISIBLE);

                else if (search.getText().toString().equalsIgnoreCase("BBC"))
                    bbcpin.setVisibility(View.VISIBLE);

                else if (search.getText().toString().equalsIgnoreCase("South Parking Garage"))
                    spgpin.setVisibility(View.VISIBLE);

            }
        });
        buildLocationSettingsRequest();
        checkLocationSettings();
    }

    /**
     * Respond to user touching the map.
     */

    public boolean onTouch (View v, MotionEvent ev)
    {
        boolean handledHere = false;

        final int action = ev.getAction();

        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();
        int nextImage = -1;

        // If we can't find imageView, return.
        ImageView imageView = (ImageView) v.findViewById (R.id.image);
        if (imageView == null) return false;

        switch (action) {

            case MotionEvent.ACTION_DOWN :
                // On DOWN, do click action.
                // The hidden image (image_areas) has 6 different hotspots on it.
                // Yellow = kl
                // Red = eng
                // Blue = yuh
                // Green = su
                // Magenta = bbc
                // White = spg

                // Use image_areas to determine which region the user touched.
                int touchColor = getHotspotColor (R.id.image_areas, evX, evY);

                // Compare the touchColor to the expected values and start new activities depending on the color
                ColorTool ct = new ColorTool ();
                int tolerance = 100;
                nextImage = R.drawable.campusmap;
                if (ct.closeMatch (Color.YELLOW, touchColor, tolerance)){
                    Intent klIntent = new Intent(this, KingLibrary.class);
                    this.startActivity(klIntent);
                }
                else if (ct.closeMatch (Color.RED, touchColor, tolerance)){
                    Intent engIntent = new Intent(this, ENG.class);
                    this.startActivity(engIntent);
                }
                else if (ct.closeMatch (Color.BLUE, touchColor, tolerance)){
                    Intent yuhIntent = new Intent(this, YUH.class);
                    this.startActivity(yuhIntent);
                }
                else if (ct.closeMatch (Color.GREEN, touchColor, tolerance)){
                    Intent suIntent = new Intent(this, SU.class);
                    this.startActivity(suIntent);
                }
                else if (ct.closeMatch (Color.MAGENTA, touchColor, tolerance)){
                    Intent bbcIntent = new Intent(this, BBC.class);
                    this.startActivity(bbcIntent);
                }
                else if (ct.closeMatch (Color.WHITE, touchColor, tolerance)){
                    Intent spgIntent = new Intent(this, SPG.class);
                    this.startActivity(spgIntent);
                }

                handledHere = true;
                break;

            default:
                handledHere = false;
        } // end switch

        if (handledHere) {

            if (nextImage > 0) {
                imageView.setImageResource (nextImage);
                imageView.setTag (nextImage);
            }
        }
        return handledHere;
    }


    /**
     * Get the color from the hotspot image at point x-y.
     *
     */

    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById (hotspotId);
        if (img == null) {
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }

    public void toast (String msg)
    {
        Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_LONG).show ();
    }

    //Hides pins
    public void hidePins() {
        klpin.setVisibility(View.INVISIBLE);
        engpin.setVisibility(View.INVISIBLE);
        yuhpin.setVisibility(View.INVISIBLE);
        supin.setVisibility(View.INVISIBLE);
        bbcpin.setVisibility(View.INVISIBLE);
        spgpin.setVisibility(View.INVISIBLE);
    }

    //Check Gplay services
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Can't connect to Gplay services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void pointLocation(double p, double q){
        double aBearing = calcBearing(p,q);
        double bBearing = calcBearing(rectangle_b_x, rectangle_b_y);
        double finalBearing = aBearing - bBearing;
        double bX = calcDistance(rectangle_a_x, rectangle_a_y, rectangle_b_x, rectangle_b_y);
        double bY = calcDistance(rectangle_a_x, rectangle_a_y, rectangle_c_x, rectangle_c_y);
        double tmpDist = calcDistance(rectangle_a_x, rectangle_a_y, p, q);
        finalBearing = Math.toRadians(finalBearing);
        currentX = rectangle_1_x + (int)(((tmpDist*Math.cos(finalBearing))/(bX))*1205);
        currentY = rectangle_1_y + (int)(((tmpDist*Math.sin(finalBearing))/(bY))*1015);
    }

    public double calcDistance(double x, double y, double p, double q){
        Location startLocation = new Location("");
        Location finishLocation = new Location("");
        startLocation.setLatitude(x);
        startLocation.setLongitude(y);
        finishLocation.setLatitude(p);
        finishLocation.setLongitude(q);
        return startLocation.distanceTo(finishLocation);
    }

    public double calcBearing(double p , double q){
        Location startLocation = new Location("");
        Location finishLocation = new Location("");
        startLocation.setLatitude(rectangle_a_x);
        startLocation.setLongitude(rectangle_a_y);
        finishLocation.setLatitude(p);
        finishLocation.setLongitude(q);
        return startLocation.bearingTo(finishLocation);
    }

    public boolean inBounds(double x, double y){
        if((x >= rectangle_c_x)&&(x <= rectangle_b_x)&&(y >= rectangle_a_y)&&( y <= rectangle_d_y)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        googleServicesAvailable();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap= null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ToDo
        Toast.makeText(getApplicationContext(), "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // ToDo
        Toast.makeText(getApplicationContext(), "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        realLocation = location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    googleApiClient.disconnect();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
            return;
        }
        realLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (realLocation != null) {
                    }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); //5 seconds
        locationRequest.setFastestInterval(2000); //2 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient,
                        locationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {
                    status.startResolutionForResult(MapScreen.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {

                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    private void drawOnMap() {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle((int)(currentX/2.15), (int)((currentY - 300)/2.14), 15, paint);

        campusImage.setAdjustViewBounds(true);
        campusImage.setImageBitmap(mutableBitmap);
        campusImage.invalidate();
    }

    private boolean drawCurrentLocation() {

        refreshMap();
        if (userLocation) {
            drawOnMap();
        }
        return false;
    }

    private void refreshMap() {
        if (bitmap != null) {
            campusImage.setImageBitmap(bitmap);
            campusImage.invalidate();
        }
    }

} // end of class


