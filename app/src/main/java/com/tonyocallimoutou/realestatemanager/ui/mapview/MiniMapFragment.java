package com.tonyocallimoutou.realestatemanager.ui.mapview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;

public class MiniMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String BUNDLE_LAT = "BUNDLE_LAT";
    private static final String BUNDLE_LNG = "BUNDLE_LNG";
    private double lat;
    private double lng;

    public MiniMapFragment() {
    }

    public static MiniMapFragment newInstance(RealEstateLocation realEstateLocation) {
        MiniMapFragment fragment = new MiniMapFragment();
        Bundle args = new Bundle();
        args.putDouble(BUNDLE_LAT, realEstateLocation.getLat());
        args.putDouble(BUNDLE_LNG, realEstateLocation.getLng());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(BUNDLE_LAT);
            lng = getArguments().getDouble(BUNDLE_LNG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mini_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mini_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        addMarker(googleMap);
    }

    private void addMarker(GoogleMap googleMap) {
        LatLng latLng = new LatLng(lat,lng);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);

        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public static void initMiniMap(FragmentActivity activity, RealEstateLocation realEstateLocation) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_mini_map, MiniMapFragment.newInstance(realEstateLocation))
                .commit();
    }
}