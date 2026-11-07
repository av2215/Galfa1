package com.example.galfa1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements LocationListener {
    private TextView tv;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void button(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        startGpsLocationUpdates();
    }

    public void openActivity2(View view) {
        startActivity(new Intent(this, Activity2.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startGpsLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startGpsLocationUpdates() {
        if (lm == null) return;

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tv.setText("GPS is disabled. Please turn on GPS in device settings.");
            return;
        }

        lm.removeUpdates(this);

        // Request updates directly from GPS Provider
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this, getMainLooper());

        Location lastGpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastGpsLoc != null) {
            displayGpsLocation(lastGpsLoc);
        } else {
            tv.setText("Acquiring GPS location fix...");
        }
    }

    private void displayGpsLocation(Location loc) {
        tv.setText("GPS Coordinates:\nLat: " + loc.getLatitude() + "\nLon: " + loc.getLongitude() + "\nAlt: " + loc.getAltitude() + " m");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        displayGpsLocation(location);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Stub required for API < 30 compatibility to prevent AbstractMethodError crash
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        // Stub required for API < 30 compatibility
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Stub required for API < 30 compatibility
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lm != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.removeUpdates(this);
        }
    }
}