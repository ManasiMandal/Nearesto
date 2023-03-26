package com.manasi.nearesto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.manasi.nearesto.databinding.ActivityMapsBinding;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // permission to request
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    final static int GRANTED = PackageManager.PERMISSION_GRANTED;
    final static int PERMISSION_CODE = 100;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // if permission not granted then requesting permissions on application start
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        }
    }

    // method to get current location i.e. latitude and longitude
    private void getCurrentLocation() {
        try {
            // if permission not granted then asking again for permission
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) != GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
                return;
            }
            // otherwise if permission granted then
            // requesting location in every 500ms or on 1 meter distance
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, (LocationListener) this);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getCurrentLocation();
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));


    }

    // override method to display location on change
    @Override
    public void onLocationChanged(@NonNull Location location) {
//        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    // method to call when provider is disabled
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(this, "Provider Enabled...", Toast.LENGTH_SHORT).show();
    }

    // method to call when provider is enabled
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Provider Disabled...", Toast.LENGTH_SHORT).show();
    }
}

