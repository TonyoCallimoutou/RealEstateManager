package com.tonyocallimoutou.realestatemanager.ui.mapview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.detail.DetailFragment;
import com.tonyocallimoutou.realestatemanager.ui.listView.ListViewFragment;
import com.tonyocallimoutou.realestatemanager.util.CompareRealEstate;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R.id.message_map_view)
    LinearLayout message_map_view;
    @BindView(R.id.fab_map_view)
    FloatingActionButton fabMap;

    private SupportMapFragment mapFragment;
    private static GoogleMap mGoogleMap;
    private CameraPosition cameraPosition;
    private static float cameraZoomDefault = 15;
    private View locationButton;

    private static ViewModelRealEstate viewModelRealEstate;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static Location userLocation;


    private static List<RealEstate> realEstatesList = new ArrayList<>();


    // Bundle
    private final String KEY_LOCATION = "KEY_LOCATION";
    private final String KEY_CAMERA_POSITION = "KEY_CAMERA_POSITION";

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        viewModelRealEstate = new ViewModelProvider(requireActivity()).get(ViewModelRealEstate.class);

        mGoogleMap = null;
        fabMap.setVisibility(View.INVISIBLE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // BUNDLE
        if (savedInstanceState != null) {
            userLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            initFragment();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, userLocation);
        }
        super.onSaveInstanceState(outState);
    }

    // WHEN PERMISSION IS GRANTED

    public void initFragment() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocationAndInitMap();
            message_map_view.setVisibility(View.GONE);
            fabMap.setVisibility(View.VISIBLE);
        }
    }


    // INIT PERMISSION

    @OnClick(R.id.button_message_map_view)
    public void initPermissionManager() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
            showAlertDialog();
        } else {
            askForPermission();
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.title_message_map_view);
        builder.setMessage(R.string.message_map_view);
        builder.setCancelable(false);

        builder.setPositiveButton(getContext().getResources().getString(R.string.positive_button_alertDialog_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                askForPermission();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void askForPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    initFragment();
                }
            });


    // GET DEVICE LOCATION
    @SuppressLint("MissingPermission")
    public void getDeviceLocationAndInitMap() {
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();

        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("FragmentLiveDataObserve")
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    userLocation = location;

                    if (mGoogleMap == null) {
                        mapFragment.getMapAsync(MapFragment.this);
                    }
                } else {
                    showAlertDialogErrorLocation();
                }
            }
        });
    }

    public void showAlertDialogErrorLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.title_alertDialog_error_location);
        builder.setMessage(R.string.message_alertDialog_error_location);
        builder.setCancelable(false);

        builder.setPositiveButton(getContext().getResources().getString(R.string.positive_button_alertDialog_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    // ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        // Camera
        if (cameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
        }

        mGoogleMap = googleMap;

        SetupForUserLocation();
        mGoogleMap.setOnMarkerClickListener(this);

        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        mapFragment.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        initListForMarker();
    }

    // SETUP USER LOCATION

    @SuppressLint({"MissingPermission", "ResourceType"})
    public void SetupForUserLocation() {
        mGoogleMap.setMyLocationEnabled(true);
        locationButton = mapFragment.getView().findViewById(0x2);
        if (locationButton != null) {
            locationButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fab_map_view)
    public void cameraOnLocation() {
        locationButton.callOnClick();
        getDeviceLocationAndInitMap();
    }


    // Restaurant

    private static void initListForMarker() {
        mGoogleMap.clear();

        LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraZoomDefault));
        addMarker();
    }

    private static void addMarker() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (RealEstate result : realEstatesList) {
            String placeName = result.getPlace().getName();
            String address = result.getPlace().getAddress();
            double lat = result.getPlace().getLat();
            double lng = result.getPlace().getLng();
            LatLng latLng = new LatLng(lat,lng);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_real_estate_map))
                    .title(placeName + " : " + address);

            builder.include(markerOptions.getPosition());

            mGoogleMap.addMarker(markerOptions)
                    .setTag(result);
        }

        // ZOOM WITH NEARBY PLACE

        if (realEstatesList.size() != 0) {
            builder.include(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mGoogleMap.moveCamera(cu);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        RealEstate markRealEstate = (RealEstate) marker.getTag();

        // CAMERA
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),cameraZoomDefault));
        DetailFragment.getDetailOf(markRealEstate);

        return true;
    }

    // INIT LIST
    public static void setRealEstateList(List<RealEstate> results) {
        if (! CompareRealEstate.compareListForMapIsEqual(realEstatesList, results)) {
            realEstatesList = results;
            if (mGoogleMap != null) {
                initListForMarker();
            }
        }
    }


}