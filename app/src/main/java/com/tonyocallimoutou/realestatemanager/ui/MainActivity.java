package com.tonyocallimoutou.realestatemanager.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;
import com.tonyocallimoutou.realestatemanager.model.User;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    private View sideView;

    private ViewModelUser viewModelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check Google play service
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelUser.class);

            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);

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
                viewModelUser.createUser();
                initData();
            } else {
                startSignInActivity();
            }
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
                Toast.makeText(this, getString(R.string.actionbar_add), Toast.LENGTH_SHORT).show();
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
    }

    private void setCurrentUserInformation(User currentUser) {
        ImageView profilePicture = sideView.findViewById(R.id.profile_picture_header_side_view);

        if (currentUser.getUrlPicture() != null) {

            Glide.with(this)
                    .load(currentUser.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);
        }


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
        viewModelUser.setCurrentUserLiveData();

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

    }
}