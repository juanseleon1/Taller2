package com.leon.taller2;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText direc;
    private SensorManager manager;
    private Sensor luz;
    private SensorEventListener list;
    private static final int REQUEST_CHECK_SETTINGS=5,RADIUS_OF_EARTH_KM = 6371;;
    private Geocoder geo;
    private FusedLocationProviderClient mfusedLoc;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location current;
    private Marker marker;
    private Marker myLocM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        manager= (SensorManager) getSystemService(SENSOR_SERVICE);
        luz =manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        geo= new Geocoder(getBaseContext());
        direc=findViewById(R.id.dir);
        inicializarTextoDir();
        inicializarLoc();

        list= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(mMap!=null && luz!=null)
                {
                    if(event.values[0]<500)
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.dark_style_map));
                    else
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.light_style_map));

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startSetLocation();
    }

    private void inicializarLoc() {
        mfusedLoc= LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    current=location;
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Mi posicion").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                }
            }
        };
    }

    private LocationRequest createLocationRequest() {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
            mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            return mLocationRequest;
    }

    private void inicializarTextoDir() {
        direc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_GO) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_SEARCH)|| (actionId == EditorInfo.IME_ACTION_SEND)){
                    String addressString = direc.getText().toString();
                    if (!addressString.isEmpty()) {
                        try {
                            List<Address> addresses = geo.getFromLocationName(addressString, 2);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    MarkerOptions myMarkerOptions = new MarkerOptions();
                                    myMarkerOptions.position(position);
                                    myMarkerOptions.title(getNombre(position));
                                    myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    if(marker!=null)
                                        marker.remove();
                                    marker=mMap.addMarker(myMarkerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
                                    toastPosicion(position);
                                }
                            } else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {Toast.makeText(MapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng myLoc = new LatLng(4.65, -74.05);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                if(marker!=null)
                   marker.remove();
                try {
                    marker=mMap.addMarker( new MarkerOptions().position(latLng).title(getNombre(latLng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    toastPosicion(latLng);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void toastPosicion(LatLng latLng) throws IOException {
        List<Address> lista=geo.getFromLocation(latLng.latitude,latLng.longitude,1);
        Toast.makeText(getBaseContext(),
                "La distancia entre su ubicacion y "+lista.get(0).getAddressLine(0)+" es:"+distance(latLng.latitude,latLng.longitude,current.getLatitude(),current.getLongitude()),
                Toast.LENGTH_SHORT).show();
    }


    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    private String getNombre(LatLng latLng) throws IOException {
        List<Address> ad = geo.getFromLocation(latLng.latitude,latLng.longitude,1);
        return ad.get(0).getAddressLine(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSetLocation();
        manager.registerListener(list, luz,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationS();
        manager.unregisterListener(list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocation(); //Todas las condiciones para recibir localizaciones
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapsActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (SendIntentException sendEx) {
                        } break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocation(); //Se encendió la localización!!!
                } else {
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
            break;
            }
        }
    }

    private void startSetLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    startLocation(); //Todas las condiciones para recibir localizaciones
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MapsActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (SendIntentException sendEx) {
                            } break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
        else
        {
            permisos.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,"Es necesario para poder mostrarle la distancia",permisos.PERMISSION_LOCATION_ID.ordinal());
        }

    }

    private void startLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mfusedLoc.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }

    }

    private void stopLocationS() {
        mfusedLoc.removeLocationUpdates(mLocationCallback);
    }

}
