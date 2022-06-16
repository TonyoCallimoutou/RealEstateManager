package com.tonyocallimoutou.realestatemanager.ui.create;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.BaseActivity;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.ui.detail.DetailFragment;
import com.tonyocallimoutou.realestatemanager.ui.filter.FilterFragment;
import com.tonyocallimoutou.realestatemanager.ui.mapview.MiniMapFragment;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.util.UtilsRealEstatePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateOrEditRealEstateActivity extends BaseActivity implements ListPictureRecyclerViewAdapter.ListPictureClickListener {

    @BindView(R.id.recycler_view_add_picture_real_estate)
    RecyclerView recyclerView;
    @BindView(R.id.input_spinner_type)
    Spinner realEstateType;
    @BindView(R.id.create_text_view_price)
    TextView textViewPrice;
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
    @BindView(R.id.textview_result_location)
    TextView realEstateLocation;
    @BindView(R.id.add_picture)
    Button addPicture;
    @BindView(R.id.detail_image_sold_banner)
    ImageView soldBanner;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private String moneyKey;

    private ViewModelRealEstate viewModelRealEstate;
    private ViewModelUser viewModelUser;

    private ListPictureRecyclerViewAdapter adapter;
    private final List<Photo> photos = new ArrayList<>();
    private int mainPicturePosition = 0;
    private RealEstateLocation place;

    private static final String BUNDLE_REAL_ESTATE = "BUNDLE_REAL_ESTATE";
    private RealEstate realEstate;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_real_estate);
        ButterKnife.bind(this);

        viewModelRealEstate = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelRealEstate.class);
        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelUser.class);

        initPrice();

        soldBanner.setVisibility(View.GONE);


        initPhotoManager();

        realEstatePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (! editable.toString().isEmpty()) {
                    int actualPrice = Utils.getIntOfStringPrice(editable.toString());
                    String str = Utils.getStringOfPrice(actualPrice);

                    if (! str.equals(realEstatePrice.getText().toString())) {
                        realEstatePrice.setText(str);
                        realEstatePrice.setSelection(realEstatePrice.getText().length());
                    }
                }

            }
        });


        // Edit Real Estate
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            realEstate = (RealEstate) extras.getSerializable(BUNDLE_REAL_ESTATE);
            initInformation();
        }
    }

    // INIT ACTION BAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu_create_activity, menu);
        MenuItem deleteItem = menu.findItem(R.id.delete_draft);
        MenuItem draftItem = menu.findItem(R.id.save_draft);

        if (realEstate == null ) {
            deleteItem.setVisible(false);
        }
        else if (!realEstate.isDraft()) {
            draftItem.setVisible(false);
            deleteItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_draft:

                DialogInterface.OnClickListener listenerDraft = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createDraft();
                    }
                };

                int titleDraft = R.string.new_title_alert_dialog_draft;
                int messageDraft= R.string.new_message_alert_dialog_draft;
                int buttonDraft = R.string.new_button_alert_dialog_draft;

                createAlertDialog(titleDraft, messageDraft, buttonDraft, listenerDraft );


                return true;

            case R.id.delete_draft:

                DialogInterface.OnClickListener listenerDelete = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDraft();
                    }
                };

                int titleDelete = R.string.new_title_alert_dialog_delete_draft;
                int messageDelete = R.string.new_message_alert_dialog_delete_draft;
                int buttonDelete = R.string.new_button_alert_dialog_delete_draft;

                createAlertDialog(titleDelete, messageDelete , buttonDelete, listenerDelete );


        }
        return super.onOptionsItemSelected(item);
    }

    // Init Price

    private void initPrice() {
        moneyKey = Utils.getKeyMoney(this);

        String str = getString(R.string.detail_price) + " " + moneyKey;

        textViewPrice.setText(str);
    }

    // Picture Manager

    private void initPhotoManager() {

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
                UtilsRealEstatePictureManager.createAlertDialog(CreateOrEditRealEstateActivity.this);
            }
        });

        initData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UtilsRealEstatePictureManager.REQUEST_IMAGE_CAMERA || requestCode == UtilsRealEstatePictureManager.REQUEST_IMAGE_FOLDER) {
            photos.addAll(UtilsRealEstatePictureManager.getImagePicture(requestCode, resultCode, data));
            adapter.initAdapter(photos);
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            place = new RealEstateLocation(this,Autocomplete.getPlaceFromIntent(data));

            initPlaceInformation();
        }
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

    // EDIT INIT INFORMATION

    private void initInformation() {
        photos.addAll(realEstate.getPhotos());
        adapter.initAdapter(photos);

        realEstateDescription.setText(realEstate.getDescription());
        realEstateType.setSelection(realEstate.getTypeId());
        int price = Utils.convertPriceUSDInMoneyKey(this,realEstate.getPriceUSD(),moneyKey);
        realEstatePrice.setText(String.valueOf(price));
        realEstateSurface.setText(String.valueOf(realEstate.getSurface()));
        realEstateRoom.setText(String.valueOf(realEstate.getNumberOfRooms()));
        realEstateBedroom.setText(String.valueOf(realEstate.getNumberOfBedrooms()));
        realEstateBathroom.setText(String.valueOf(realEstate.getNumberOfBathrooms()));
        if (realEstate.getPlace() != null) {
            place = realEstate.getPlace();
            initPlaceInformation();
        }

        if (realEstate.isSold()) {
            soldBanner.setVisibility(View.VISIBLE);
        }
        else {
            soldBanner.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.button_location)
    public void chooseAddress() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(CreateOrEditRealEstateActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void initPlaceInformation() {
        realEstateLocation.setText(place.getName());
        MiniMapFragment.initMiniMap(this,place);
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

        if (realEstateLocation.getText() == "") {
            canSave = false;
            realEstateLocation.setError(getString(R.string.new_error_enter_data));
        }

        try {
            int price = Utils.getIntOfStringPrice(realEstatePrice.getText().toString());
        }
        catch (NumberFormatException e) {
            canSave = false;
            realEstatePrice.setError(getString(R.string.new_error_price_format));
        }

        if (! canSave) {
            Toast.makeText(this, getString(R.string.new_toast_unable_to_save), Toast.LENGTH_SHORT).show();
        }

        else {
            int type = realEstateType.getSelectedItemPosition();
            if (photos.size() == 0) {
                photos.add(new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null));
            }

            int price = Utils.getIntOfStringPrice(realEstatePrice.getText().toString());
            String description = realEstateDescription.getText().toString();
            int surface = Integer.parseInt(realEstateSurface.getText().toString());
            int room = Integer.parseInt(realEstateRoom.getText().toString());
            int bedroom = Integer.parseInt(realEstateBedroom.getText().toString());
            int bathroom = Integer.parseInt(realEstateBathroom.getText().toString());

            int priceUsd = Utils.getPriceInUSD(this,price, moneyKey);


            RealEstate realEstateToCreate = new RealEstate(priceUsd, currentUser, type, photos, mainPicturePosition, description, surface, room, bathroom, bedroom, place);

            if (realEstate != null) {
                realEstateToCreate.setId(realEstate.getId());
            }

            viewModelRealEstate.createRealEstate(realEstateToCreate);

            finish();
        }
    }

    // DRAFT

    private void createAlertDialog(int title, int message, int button, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(getString(button), listener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteDraft() {
        viewModelRealEstate.deleteDraft(realEstate);
        finish();
    }

    private void createDraft() {
        if (photos.size() == 0) {
            photos.add(new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null));
        }

        int type = realEstateType.getSelectedItemPosition();
        String description = realEstateDescription.getText().toString();
        int surface = 0;
        int room = 0;
        int bedroom = 0;
        int bathroom = 0;
        int priceUsd = 0;
        try {
            surface = Integer.parseInt(realEstateSurface.getText().toString());
        }
        catch (Exception ignored) {}
        try {
            room = Integer.parseInt(realEstateRoom.getText().toString());
        }
        catch (Exception ignored) {}
        try {
            bedroom = Integer.parseInt(realEstateBedroom.getText().toString());
        }
        catch (Exception ignored) {}
        try {
            bathroom = Integer.parseInt(realEstateBathroom.getText().toString());
        }
        catch (Exception ignored) {}

        try {
            int price = Utils.getIntOfStringPrice(realEstatePrice.getText().toString());
            priceUsd = Utils.getPriceInUSD(this,price, moneyKey);
        }
        catch (Exception ignored) {}




        RealEstate draft = new RealEstate(priceUsd, currentUser, type, photos, mainPicturePosition, description, surface, room, bathroom, bedroom, place);

        if (realEstate != null) {
            draft.setId(realEstate.getId());
        }

        viewModelRealEstate.saveAsDraft(draft);
        finish();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.new_title_alert_dialog);
        builder.setMessage(R.string.new_message_alert_dialog);

        builder.setPositiveButton(getString(R.string.new_button_alert_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(CreateOrEditRealEstateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initData() {

        viewModelUser.getCurrentUserLiveData().observe(this, result -> {
            currentUser = result;
            viewModelUser.setCurrentUser(result);
        });
    }

    public static void startActivity(Activity activity, @Nullable RealEstate realEstate) {
        Intent intent = new Intent(activity, CreateOrEditRealEstateActivity.class);
        if (realEstate != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_REAL_ESTATE, realEstate);
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(CreateOrEditRealEstateActivity.this, MainActivity.class);
        startActivity(intent);
    }
}