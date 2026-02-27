package com.app.pindrop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);

        // Enable high detail tiles
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setMultiTouchControls(true);

        // Very high zoom level for HD detail
        map.getController().setZoom(20.0);

        map.setMinZoomLevel(4.0);    // Good world overview
        map.setMaxZoomLevel(19.0);   // Stable high detail
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