package com.tonyocallimoutou.realestatemanager.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.navigation.NavigationView;
import com.tonyocallimoutou.realestatemanager.BuildConfig;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.create.AddNewRealEstateActivity;
import com.tonyocallimoutou.realestatemanager.ui.detail.DetailFragment;
import com.tonyocallimoutou.realestatemanager.ui.listView.ListViewFragment;
import com.tonyocallimoutou.realestatemanager.ui.mapview.MapViewFragment;
import com.tonyocallimoutou.realestatemanager.util.UtilsProfilePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;
import com.tonyocallimoutou.realestatemanager.model.User;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.switch_map_list)
    SwitchCompat switchMapList;

    private View sideView;

    private ViewModelUser viewModelUser;
    private ViewModelRealEstate viewModelRealEstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        // Check Google play service
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelUser.class);
            viewModelRealEstate = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelRealEstate.class);

            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);

            initSwitchAndFragment();
            initActionBar();
        } else {
            errorGooglePlayService(status);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(status == ConnectionResult.SUCCESS) {
            if (viewModelUser.isCurrentLogged()) {
                initData();
            } else {
                startSignInActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (DetailFragment.canCloseFragment()) {
            DetailFragment.closeFragment();
        }
        else {
            super.onBackPressed();
        }
    }

    // error Google PLay Service

    private void errorGooglePlayService(int status) {
        String message = "";
        if(status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            message =  getString(R.string.message_alertDialog_google_play_service_update);
        }
        else {
            message =  getString(R.string.message_alertDialog_google_play_service_download);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.title_alertDialog_google_play_service);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(getResources().getString(R.string.positive_button_alertDialog_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    // SIGN IN ACTIVITY

    public void startSignInActivity() {
        List<AuthUI.IdpConfig> provider =
                Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        startActivity(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(provider)
                        .setIsSmartLockEnabled(false,true)
                        .build()
        );
    }

    // Picture Manager

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UtilsProfilePictureManager.getImagePicture(requestCode, resultCode, data);
    }

    // INIT FRAGMENT

    private void initSwitchAndFragment() {

        ListViewFragment listViewFragment = ListViewFragment.newInstance();
        MapViewFragment mapViewFragment = MapViewFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_fragment, listViewFragment)
                .commit();

        switchMapList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.host_fragment, mapViewFragment)
                            .commit();
                }
                else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.host_fragment, listViewFragment)
                            .commit();
                }
            }
        });


        initDetailFragment(this, null);
    }

    public static void initDetailFragment(FragmentActivity activity, @Nullable RealEstate realEstate) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_fragment_2, DetailFragment.newInstance(realEstate))
                .commit();
    }


    // INIT ACTION BAR

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawer.isOpen()) {
                    mDrawer.close();
                } else {
                    mDrawer.open();
                }
                return true;
            case R.id.add_menu:
                Intent intent = new Intent(this, AddNewRealEstateActivity.class);
                startActivity(intent);
                return true;
            case R.id.edit_menu:
                Toast.makeText(this, getString(R.string.actionbar_edit), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.search_menu:
                Toast.makeText(this, getString(R.string.actionbar_search), Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //INIT SIDE VIEW

    private void initSideView(User currentUser) {

        NavigationView nav = findViewById(R.id.side_menu_nav_view);
        nav.setNavigationItemSelectedListener(this);
        sideView = nav.getHeaderView(0);

        setCurrentUserInformation(currentUser);

        ImageView profilePicture = sideView.findViewById(R.id.profile_picture_header_side_view);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsProfilePictureManager.createAlertDialog(MainActivity.this, viewModelUser);
            }
        });
    }

    private void setCurrentUserInformation(User currentUser) {
        ImageView profilePicture = sideView.findViewById(R.id.profile_picture_header_side_view);

        Glide.with(this)
                .load(currentUser.getUrlPicture())
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);


        TextView email = sideView.findViewById(R.id.user_email);
        TextView name = sideView.findViewById(R.id.user_name);

        email.setText(currentUser.getEmail());
        name.setText(currentUser.getUsername());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_setting:
                /*
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                 */
                break;

            case R.id.navigation_logout:
                mDrawer.close();
                viewModelUser.signOut(this);

                startSignInActivity();

                break;
            default:
                break;
        }
        return true;
    }

    // InitData

    public void initData() {

        viewModelUser.createUser(this);
        viewModelUser.setCurrentUserLiveData();
        viewModelUser.setListUser();
        viewModelRealEstate.setListRealEstate();

        viewModelUser.getCurrentUserLiveData().observe(this, currentUserResults -> {
            if (currentUserResults != null) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_username), currentUserResults.getUsername())
                        .apply();

                initSideView(currentUserResults);
            }
        });

        viewModelUser.getAllUser().observe(this, list -> {
            DetailFragment.setListUser(list);
        });

        viewModelRealEstate.getALlRealEstateLiveData().observe(this, listRealEstate -> {
            ListViewFragment.initResidenceList(listRealEstate);
            MapViewFragment.setRealEstateList(listRealEstate);
        });

    }
}