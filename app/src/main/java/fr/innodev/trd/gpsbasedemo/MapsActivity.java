package fr.innodev.trd.gpsbasedemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;//= (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Log log;
    private Location ancienne;
    private double distance;

    ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
    AlertDialog.Builder builder;
    SensorManager sensorManager = null;
    Sensor accelerometre = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        builder = new AlertDialog.Builder(this);

        if(accelerometre != null)
        {
            sensorManager.registerListener(mSensorEventListener, accelerometre, SensorManager.SENSOR_DELAY_UI);
        }
        else
        {
            builder.setMessage("erreur pas de capteur")
                    .setPositiveButton("ok", null);
            final AlertDialog alert = builder.create();
            alert.show();
            new CountDownTimer(3000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    alert.dismiss();
                }
            }.start();
        }

        while (!permissionGranted()) ;

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            log.v("INFO", "Location Result" + location.toString());
                            updateMapDisplay(location);
                        }
                    }
                });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if(ancienne == null) {
                        ancienne = location;
                    }
                    // Update UI with location data
                    log.v("INFO", "Location Callback" + location.toString());
                    updateMapDisplay(location);
                    builder.setMessage("GPS: vous avez  parcouru "+location.distanceTo(ancienne)+ "m\r" +
                                                    "Acceleromettre vous avez parcouru "+distance+"m")
                        .setPositiveButton("ok", null);
                    final AlertDialog alert = builder.create();
                    alert.show();
                    new CountDownTimer(3000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFinish() {
                            // TODO Auto-generated method stub

                            alert.dismiss();
                        }
                    }.start();
                    ancienne = location;
                    distance = 0;
                }
            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

    }

    final SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Que faire en cas de changement de précision ?

        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            // Que faire en cas d'évènements sur le capteur ?
            //j'ai un doute, mais il parrait que ça suffit pour faire le calcul
            //si ça a pas l'air de fonctionner, supprimer distance
            distance = Math.abs(distance)+Math.abs(sensorEvent.values[0]) + Math.abs(sensorEvent.values[2]);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(mSensorEventListener, accelerometre, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mSensorEventListener, accelerometre);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mMap.setMyLocationEnabled(true);
    }

    private void updateMapDisplay(Location myLocation) {
        // Add a marker in Sydney and move the camera
        LatLng curPos = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(curPos).title("Position courante"));
        float zoom = mMap.getMaxZoomLevel();
        log.d("INFO", "Zoom Max = " + zoom);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, zoom - 3.0f));
    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
