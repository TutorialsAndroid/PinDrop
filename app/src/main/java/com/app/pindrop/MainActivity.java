package com.app.pindrop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private MyLocationNewOverlay locationOverlay;
    private MapView map;
    private Marker userMarker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        map.setMinZoomLevel(4.0);
        map.setMaxZoomLevel(19.0);

        // Set default zoom
        map.getController().setZoom(15.0);

        // ✅ Set default location (New York)
        GeoPoint startPoint = new GeoPoint(40.7128, -74.0060);
        map.getController().setCenter(startPoint);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        map.setOnTouchListener((v, event) -> {
            if (locationOverlay != null) {
                locationOverlay.disableFollowLocation();
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            getUserLocation();

        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                showRationaleDialog();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location Permission Needed")
                .setMessage("This app requires location access to show your position on the map and provide location-based features.")
                .setPositiveButton("Allow", (dialog, which) -> {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    showPermissionRequiredDialog();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getUserLocation();

            } else {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    showSettingsDialog();

                } else {

                    showPermissionRequiredDialog();
                }
            }
        }
    }

    private void showPermissionRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Location permission is required to use this app.")
                .setPositiveButton("Retry", (dialog, which) -> checkLocationPermission())
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Permanently Denied")
                .setMessage("Please enable location permission from app settings to continue.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (locationOverlay == null) {

            locationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(this), map);

            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation(); // camera follows user

            map.getOverlays().add(locationOverlay);
        }
    }

    private void showOrUpdateUserMarker(GeoPoint geoPoint) {

        if (userMarker == null) {

            userMarker = new Marker(map);
            userMarker.setPosition(geoPoint);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            userMarker.setTitle("You are here");

            map.getOverlays().add(userMarker);

        } else {

            userMarker.setPosition(geoPoint);
        }

        map.invalidate();
    }

    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                String fullAddress = address.getAddressLine(0);

                userMarker.setTitle(fullAddress);
                userMarker.showInfoWindow();

                map.invalidate();
            }

        } catch (IOException e) {
            if (e.getMessage() != null) {
                Log.e("MainActivity", e.getMessage());
            }
            userMarker.setTitle("Location detected");
            userMarker.showInfoWindow();
        }
    }
}