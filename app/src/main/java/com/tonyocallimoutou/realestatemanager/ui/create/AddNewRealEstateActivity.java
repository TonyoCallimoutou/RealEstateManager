package com.tonyocallimoutou.realestatemanager.ui.create;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.UtilsRealEstatePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewRealEstateActivity extends AppCompatActivity implements ListPictureRecyclerViewAdapter.ListPictureClickListener {

    @BindView(R.id.recycler_view_add_picture_real_estate)
    RecyclerView recyclerView;
    @BindView(R.id.input_spinner_type)
    Spinner realEstateType;
    @BindView(R.id.input_price)
    EditText realEstatePrice;
    @BindView(R.id.input_description)
    EditText realEstateDescription;
    @BindView(R.id.input_surface)
    EditText realEstateSurface;
    @BindView(R.id.input_room)
    EditText realEstateRoom;
    @BindView(R.id.input_bedroom)
    EditText realEstateBedroom;
    @BindView(R.id.input_bathroom)
    EditText realEstateBathroom;
    @BindView(R.id.add_picture)
    Button addPicture;

    private ViewModelRealEstate viewModelRealEstate;
    private ViewModelUser viewModelUser;

    private ListPictureRecyclerViewAdapter adapter;
    private List<Photo> photos = new ArrayList<>();
    private int mainPicturePosition = 0;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_real_estate);
        ButterKnife.bind(this);

        viewModelRealEstate = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelRealEstate.class);
        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelUser.class);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new ListPictureRecyclerViewAdapter(this, photos, this, new ListPictureRecyclerViewAdapter.ListRemoveClickListener() {
            @Override
            public void onClick(int position) {
                photos.remove(position);
                adapter.initAdapter(photos);
            }
        });

        recyclerView.setAdapter(adapter);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsRealEstatePictureManager.createAlertDialog(AddNewRealEstateActivity.this);
            }
        });

        initData();
    }

    // Picture Manager

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photos.addAll(UtilsRealEstatePictureManager.getImagePicture(requestCode, resultCode, data));
        adapter.initAdapter(photos);
    }

    // Add Description to picture

    @Override
    public void onClick(int position) {
        View view = getLayoutInflater().inflate(R.layout.alert_real_estate_add_picture_description, null);

        ImageView picture = view.findViewById(R.id.alert_dialog_picture_to_modify);
        CheckBox checkBox = view.findViewById(R.id.checkbox_main_picture);
        EditText newDescription = view.findViewById(R.id.picture_input_description);
        Button saveButtonPicture = view.findViewById(R.id.alert_dialog_save_button);

        Glide.with(this)
                .load(photos.get(position).getReference())
                .into(picture);

        if (position == mainPicturePosition) {
            checkBox.setChecked(true);
        }

        newDescription.setText(photos.get(position).getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();

        saveButtonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.get(position).setDescription(newDescription.getText().toString());
                if (checkBox.isChecked()) {
                    mainPicturePosition = position;
                }
                adapter.initAdapter(photos);
                alert.cancel();
            }
        });
    }


    @OnClick(R.id.new_element_save_button)
    public void save() {

        List<EditText> listEditText = new ArrayList<>();
        listEditText.add(realEstatePrice);
        listEditText.add(realEstateDescription);
        listEditText.add(realEstateSurface);
        listEditText.add(realEstateRoom);
        listEditText.add(realEstateBedroom);
        listEditText.add(realEstateBathroom);

        boolean canSave = true;

        for (EditText editText : listEditText) {
            if (editText.length() == 0) {
                editText.setError(getString(R.string.new_error_enter_data));
                canSave = false;
            }
        }

        if (! canSave) {
            Toast.makeText(this, getString(R.string.new_toast_unable_to_save), Toast.LENGTH_SHORT).show();
        }

        else {
            String type = (String) realEstateType.getSelectedItem();
            if (photos.size() == 0) {
                photos.add(new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null));
            }

            int price = Integer.parseInt(realEstatePrice.getText().toString());
            String description = realEstateDescription.getText().toString();
            int surface = Integer.parseInt(realEstateSurface.getText().toString());
            int room = Integer.parseInt(realEstateRoom.getText().toString());
            int bedroom = Integer.parseInt(realEstateBedroom.getText().toString());
            int bathroom = Integer.parseInt(realEstateBathroom.getText().toString());

            RealEstate realEstateToCreate = new RealEstate(price,currentUser, type,photos,mainPicturePosition,description,surface,room,bathroom,bedroom);
            viewModelRealEstate.createRealEstate(realEstateToCreate);

            finish();
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.new_title_alert_dialog);
        builder.setMessage(R.string.new_message_alert_dialog);

        builder.setPositiveButton(getString(R.string.new_button_alert_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initData() {
        viewModelUser.setCurrentUserLiveData();

        viewModelUser.getCurrentUserLiveData().observe(this, currentUserLiveData -> {
            currentUser = currentUserLiveData;
        });
    }
}