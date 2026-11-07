package com.example.galfa1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class Activity2 extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    private TextView tv;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationManager lm;
    private Location currentLoc;

    // Defined target location (Gal Center: 32.0853, 34.7818)
    private static final LatLng GAL_CENTER = new LatLng(32.0853, 34.7818);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        tv = findViewById(R.id.tv);
        mapView = findViewById(R.id.mapView);

        try {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void button(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        showMapAndDistance();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showMapAndDistance();
        }
    }

    @SuppressLint("MissingPermission")
    private void showMapAndDistance() {
        if (lm != null) {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this, getMainLooper());
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) updateLocationAndDistance(loc);
            }
            if (currentLoc == null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this, getMainLooper());
                Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) updateLocationAndDistance(loc);
            }
        }
    }

    private void updateLocationAndDistance(Location loc) {
        this.currentLoc = loc;

        Location targetLoc = new Location("GalCenter");
        targetLoc.setLatitude(GAL_CENTER.latitude);
        targetLoc.setLongitude(GAL_CENTER.longitude);

        float distanceMeters = loc.distanceTo(targetLoc);
        float distanceKm = distanceMeters / 1000f;

        String text = String.format(Locale.US,
                "Current: Lat %.4f, Lon %.4f\nTarget: Gal Center (Lat %.4f, Lon %.4f)\nDistance: %.2f km (%.0f m)",
                loc.getLatitude(), loc.getLongitude(),
                GAL_CENTER.latitude, GAL_CENTER.longitude,
                distanceKm, distanceMeters);

        tv.setText(text);
        updateMap();
    }

    private void updateMap() {
        if (googleMap == null || currentLoc == null) return;

        try {
            googleMap.clear();

            LatLng currentLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            googleMap.addMarker(new MarkerOptions().position(GAL_CENTER).title("Gal Center"));

            try {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(currentLatLng)
                        .include(GAL_CENTER)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } catch (Exception e) {
                // Fallback if view bounds are not yet layout-measured
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        try {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateMap();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateLocationAndDistance(location);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    protected void onStart() {
        super.onStart();
        try { mapView.onStart(); } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try { mapView.onResume(); } catch (Exception ignored) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try { mapView.onPause(); } catch (Exception ignored) {}
        if (lm != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.removeUpdates(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try { mapView.onStop(); } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { mapView.onDestroy(); } catch (Exception ignored) {}
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try { mapView.onLowMemory(); } catch (Exception ignored) {}
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try { mapView.onSaveInstanceState(outState); } catch (Exception ignored) {}
    }
}