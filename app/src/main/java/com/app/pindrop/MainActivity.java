package com.app.pindrop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView map;

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
}