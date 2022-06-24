package com.tonyocallimoutou.realestatemanager.ui;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

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
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.create.CreateOrEditRealEstateActivity;
import com.tonyocallimoutou.realestatemanager.ui.detail.DetailFragment;
import com.tonyocallimoutou.realestatemanager.ui.filter.FilterFragment;
import com.tonyocallimoutou.realestatemanager.ui.listView.ListViewFragment;
import com.tonyocallimoutou.realestatemanager.ui.mapview.MapViewFragment;
import com.tonyocallimoutou.realestatemanager.ui.setting.SettingActivity;
import com.tonyocallimoutou.realestatemanager.util.UtilsProfilePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.switch_map_list)
    SwitchCompat switchMapList;

    public static Context context;
    private static FragmentActivity fragmentActivity;

    private View sideView;

    private ViewModelUser viewModelUser;
    private ViewModelRealEstate viewModelRealEstate;

    private static MenuItem editItem;
    private static MenuItem goToEditItem;
    private static MenuItem addItem;
    private static MenuItem filterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK
        if (! Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        // Check Google play service
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelUser.class);
            viewModelRealEstate = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelRealEstate.class);

            setContentView(R.layout.activity_main);

            fragmentActivity = this;
            context = this;
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

    @Override
    protected void onStop() {
        if (FilterFragment.isOpen) {
            FilterFragment.closeFragment();
        }

        super.onStop();
    }

    // error Google PLay Service

    private void errorGooglePlayService(int status) {
        String message;
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
                .replace(R.id.host_list_or_map, listViewFragment)
                .commit();

        switchMapList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.host_list_or_map, mapViewFragment)
                            .commit();
                }
                else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.host_list_or_map, listViewFragment)
                            .commit();
                }
            }
        });


        initDetailFragment( null);
    }

    public static void initDetailFragment(@Nullable RealEstate realEstate) {
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_detail_fragment, DetailFragment.newInstance(realEstate))
                .commit();
    }

    public static void initFilterFragment() {
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_filter_fragment, FilterFragment.newInstance())
                .commit();

        switchColorOfFilterItem(true);
    }

    public static void switchColorOfFilterItem(boolean isOpen) {
        if (isOpen) {
            filterItem.getIcon().setTint(fragmentActivity.getResources().getColor(R.color.colorSecondaryLight));
        }
        else {
            filterItem.getIcon().setTint(fragmentActivity.getResources().getColor(R.color.white));
        }
    }


    // INIT ACTION BAR

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        editItem = menu.findItem(R.id.edit_menu);
        goToEditItem = menu.findItem(R.id.go_to_edit);
        addItem = menu.findItem(R.id.add_menu);
        filterItem = menu.findItem(R.id.filter_menu);
        setVisibilityEditMenuItem(false);
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
                CreateOrEditRealEstateActivity.startActivity(this,null);

                return true;
            case R.id.go_to_edit:
                CreateOrEditRealEstateActivity.startActivity(this,DetailFragment.getActualRealEstate());

                DetailFragment.newInstance(null);

                return true;

            case R.id.sold_item:

                soldRealEstate(DetailFragment.getActualRealEstate());

            case R.id.filter_menu:
                FilterFragment.changeVisibilityOfFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setVisibilityEditMenuItem(boolean isVisible) {
        if (editItem != null) {
            editItem.setVisible(isVisible);
        }
    }

    public static void setVisibilityAddAndSearchMenuItem(boolean isVisible) {
        if (addItem != null && filterItem != null) {
            addItem.setVisible(isVisible);
            filterItem.setVisible(isVisible);
        }
    }

    public static void setVisibilityGoToEditMenuItem(boolean isVisible) {
        if (goToEditItem != null) {
            goToEditItem.setVisible(isVisible);
        }
    }

    private void soldRealEstate(RealEstate mRealEstate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.title_message_sold_real_estate);

        String message;
        if (mRealEstate.isSold()) {
            message = MainActivity.context.getString(R.string.message_cancel_sold_real_estate);
        }
        else {
            message = MainActivity.context.getString(R.string.message_sold_real_estate);
        }

        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(MainActivity.context.getString(R.string.button_message_sold_real_estate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initDetailFragment(viewModelRealEstate.soldRealEstate(mRealEstate));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //INIT SIDE VIEW

    private void initSideView(User currentUser) {

        NavigationView nav = findViewById(R.id.side_menu_nav_view);
        nav.setNavigationItemSelectedListener(this);
        sideView = nav.getHeaderView(0);

        setCurrentUserInformation(currentUser);


        ImageView profilePictureInformation = sideView.findViewById(R.id.profile_picture_information);
        ImageView profilePicture = sideView.findViewById(R.id.profile_picture_header_side_view);

        profilePictureInformation.setVisibility(View.GONE);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsProfilePictureManager.createAlertDialog(MainActivity.this, viewModelUser, currentUser);
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
        ImageView warning = sideView.findViewById(R.id.phone_number_warning);
        TextView phoneNumber = sideView.findViewById(R.id.user_phone_number);


        email.setText(currentUser.getEmail());
        name.setText(currentUser.getUsername());
        if (currentUser.getPhoneNumber() != null && ! currentUser.getPhoneNumber().isEmpty()) {
            warning.setVisibility(View.GONE);
            phoneNumber.setText(currentUser.getPhoneNumber());
        }
        else {
            warning.setVisibility(View.VISIBLE);
            phoneNumber.setText(getString(R.string.information_no_phone_number));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.navigation_logout:
                mDrawer.close();
                viewModelUser.signOut();

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
        viewModelRealEstate.syncFirebase();

        viewModelUser.getCurrentUserLiveData().observe(this, currentUserResults -> {
            if (currentUserResults != null) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_user_uid), currentUserResults.getUid())
                        .putString(getString(R.string.shared_preference_username), currentUserResults.getUsername())
                        .putString(getString(R.string.shared_preference_phone_number), currentUserResults.getPhoneNumber())
                        .apply();

                Log.d("TAG", "initData: ");

                viewModelRealEstate.setMyRealEstates(currentUserResults);

                initSideView(currentUserResults);
                DetailFragment.setCurrentUser(currentUserResults);
                FilterFragment.setCurrentUser(currentUserResults);
            }
        });

        viewModelRealEstate.getFilterListLiveData().observe(this, listFilter -> {
            Log.d(this.getClass().getSimpleName(), "");
            ListViewFragment.initRealEstateSyncList(listFilter);
            MapViewFragment.setRealEstateList(listFilter);
            if (! DetailFragment.canCloseFragment() && DetailFragment.getActualRealEstate() == null) {
                initDetailFragment(listFilter.get(0));
            }
        });
    }

    public static void connectionChanged() {
        ListViewFragment.setConnection();
    }
}