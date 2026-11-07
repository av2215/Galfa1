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

public class MainActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
    }

    public void button(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location gpsLoc = lm != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : null;
        Location netLoc = lm != null ? lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) : null;
        Location loc = gpsLoc != null ? gpsLoc : netLoc;

        if (loc != null) {
            display(loc);
        } else if (lm != null) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location l) {
                    display(l);
                    lm.removeUpdates(this);
                }
            }, getMainLooper());
        }
    }

    private void display(Location loc) {
        tv.setText("Lat: " + loc.getLatitude() + "\nLon: " + loc.getLongitude() + "\nAlt: " + loc.getAltitude());
    }
}