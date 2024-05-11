package com.mirea.pershinadv.mireaproject;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mirea.pershinadv.mireaproject.databinding.FragmentEstablishmentsBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Establishments extends Fragment {
    private MapView mapView;
    private EditText search;
    List<Place> places = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue("com.mirea.pershinadv.mireaproject;");

        places.add(new Place("Красная площадь", "Красная площадь", "Красная пл., Москва", new GeoPoint(55.75391, 37.62079)));
        places.add(new Place("Большой Театр", "Большой Театр", "Театральная пл., 1, Москва", new GeoPoint(55.76019, 37.61859)));
        places.add(new Place("Третьяковская галерея", "Третьяковская галерея", "Лаврушинский пер., 10, Москва", new GeoPoint(55.74139, 37.62089)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_establishments, container, false);
        mapView = view.findViewById(R.id.mapView);
        search = view.findViewById(R.id.search);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(55.72945, 37.60117);
        mapController.setCenter(startPoint);

        addMarkersToMap();

        search.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = v.getText().toString();
            searchPlaces(searchText);
            return true;
        });

        return view;
    }

    private void addMarkersToMap() {
        for (Place place : places) {
            addMarker(place.getLocation(), place.getName(), place.getDescription(), place.getAddress());
        }
    }


    private void addMarker(GeoPoint point, String title, String description, String address) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title + "\n" + description + "\nАдрес: " + address);
        mapView.getOverlays().add(marker);
    }


    private void searchPlaces(String query) {
        mapView.getOverlays().clear();

        if (query.isEmpty()) {
            addMarkersToMap();
        } else {
            for (Place place : places) {
                if (place.getName().toLowerCase().contains(query.toLowerCase())) {
                    addMarker(place.getLocation(), place.getName(), place.getDescription(), place.getAddress());
                }
            }
        }

        mapView.invalidate();
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}