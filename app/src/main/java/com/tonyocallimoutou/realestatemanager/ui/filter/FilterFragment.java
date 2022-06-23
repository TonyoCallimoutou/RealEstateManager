package com.tonyocallimoutou.realestatemanager.ui.filter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.slider.RangeSlider;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    private static LinearLayout mainLayout;

    @BindView(R.id.filter_range_distance)
    RangeSlider filterDistance;
    @BindView(R.id.filter_min_price)
    EditText minPrice;
    @BindView(R.id.filter_max_price)
    EditText maxPrice;
    @BindView(R.id.filter_spinner_room)
    Spinner spinnerRoom;
    @BindView(R.id.filter_spinner_type)
    Spinner spinnerType;
    @BindView(R.id.filter_checkbox_is_mine)
    CheckBox checkBoxIsMine;
    @BindView(R.id.filter_checkbox_is_sold)
    CheckBox checkBoxIsSold;
    @BindView(R.id.filter_spinner_sold_date)
    Spinner spinnerSoldDate;
    @BindView(R.id.filter_spinner_creation_date)
    Spinner spinnerCreationDate;
    @BindView(R.id.filter_spinner_nbr_picture)
    Spinner spinnerNbrPicture;
    @BindView(R.id.filter_more)
    LinearLayout moreFilter;
    @BindView(R.id.filter_more_or_less)
    LinearLayout buttonMoreFilter;
    @BindView(R.id.filter_text_more_or_less)
    TextView textMoreOrLessFilter;
    @BindView(R.id.filter_img_more_or_less)
    ImageView imageViewMoreFilter;
    @BindView(R.id.filter_recyclerview_list_filter)
    RecyclerView recyclerView;
    @BindView(R.id.filter_checkbox_draft)
    CheckBox draftCheckbox;
    @BindView(R.id.filter_checkbox_not_sync)
    CheckBox notSyncCheckbox;
    @BindView(R.id.filter_checkbox_next_school)
    CheckBox schoolCheckbox;
    @BindView(R.id.filter_checkbox_next_park)
    CheckBox parkCheckbox;
    @BindView(R.id.filter_checkbox_next_store)
    CheckBox storeCheckbox;

    private FilterRecyclerViewAdapter adapter;

    private View clearButtonAutocomplete;

    private static FragmentActivity activity;
    private static FilterFragment fragment;
    public static boolean isOpen;

    private static List<Filter> filters;
    private static ViewModelRealEstate viewModelRealEstate;

    private static User currentUser;

    private String moneyKey;

    public FilterFragment() {
    }

    public static FilterFragment newInstance() {
        fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this,view);

        viewModelRealEstate = new ViewModelProvider(requireActivity()).get(ViewModelRealEstate.class);

        moneyKey = Utils.getKeyMoney(getContext());

        mainLayout = view.findViewById(R.id.filter_main_layout);

        filters = new ArrayList<>();

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        adapter = new FilterRecyclerViewAdapter(filters, new FilterRecyclerViewAdapter.FilterItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                Filter filter = filters.get(position);
                setDataVisible(filter);
                applyFilter();
            }
        });

        recyclerView.setAdapter(adapter);

        isOpen = true;
        activity = getActivity();

        initMoreFilter();

        initAllFilter();

        return view;
    }

    private void initMoreFilter() {
        moreFilter.setVisibility(View.GONE);

        buttonMoreFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(moreFilter.getVisibility() == View.GONE) {
                    moreFilter.setVisibility(View.VISIBLE);
                    textMoreOrLessFilter.setText(getString(R.string.filter_less_filter));
                    imageViewMoreFilter.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_minus_24dp, null));
                }
                else {
                    moreFilter.setVisibility(View.GONE);
                    textMoreOrLessFilter.setText(getString(R.string.filter_more_filter));
                    imageViewMoreFilter.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_add_black_24dp, null));
                }
            }
        });
    }

    private void setDataVisible(Filter filter) {
        if (filter.getFilterType() == (Filter.TYPE_MIN_PRICE)) {
            minPrice.setText("");
        }
        else if (filter.getFilterType() == (Filter.TYPE_MAX_PRICE)) {
            maxPrice.setText("");
        }
        else if (filter.getFilterType() == (Filter.TYPE_TYPE)) {
            spinnerType.setSelection(0);
        }
        else if (filter.getFilterType() == (Filter.TYPE_ROOM)) {
            spinnerRoom.setSelection(0);
        }
        else if (filter.getFilterType() == (Filter.TYPE_CREATION)) {
            spinnerCreationDate.setSelection(0);
        }
        else if (filter.getFilterType() == (Filter.TYPE_PICTURE)) {
            spinnerNbrPicture.setSelection(0);
        }
        else if (filter.getFilterType() == (Filter.TYPE_SOLD)) {
            checkBoxIsSold.setChecked(false);
        }
        else if (filter.getFilterType() == (Filter.TYPE_MINE)) {
            checkBoxIsMine.setChecked(false);
        }
        else if (filter.getFilterType() == (Filter.TYPE_LOCATION)) {
            clearButtonAutocomplete.performClick();
        }
        else if (filter.getFilterType() == (Filter.TYPE_DRAFT)) {
            checkBoxIsMine.setChecked(false);
        }
        else if (filter.getFilterType() == (Filter.TYPE_NOT_SYNC)) {
            checkBoxIsMine.setChecked(false);
        }
        else if (filter.getFilterType() == (Filter.TYPE_NEXT_SCHOOL)) {
            schoolCheckbox.setChecked(false);
        }
        else if (filter.getFilterType() == (Filter.TYPE_NEXT_PARK)) {
            parkCheckbox.setChecked(false);
        }
        else if (filter.getFilterType() == (Filter.TYPE_NEXT_STORE)) {
            storeCheckbox.setChecked(false);
        }

        filters.remove(filter);
        applyFilter();
    }

    // INIT FILTER

    private void initAllFilter() {
        initPlaceFilter();
        initPriceFilter();
        initTypeSpinner();
        initRoomSpinner();
        initCreationDateSpinner();
        initPictureSpinner();
        initMineCheckbox();
        initSoldFilter();
        initDraftFilter();
        initNotSyncFilter();
        initProximityFilter();
    }

    private void initPlaceFilter() {
        Filter filterLocation = new Filter(getContext(), Filter.TYPE_LOCATION);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        autocompleteFragment.setHint(getString(R.string.filter_hint_location));
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(fields);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                filterLocation.setFilterCity(place);
                filterLocation.setDistance(filterDistance.getValues().get(0));
                filters.remove(filterLocation);
                filters.add(filterLocation);
                applyFilter();

                filterDistance.setVisibility(View.VISIBLE);

                clearButtonAutocomplete = autocompleteFragment
                        .getView()
                        .findViewById(com.google.android.libraries.places.R.id.places_autocomplete_clear_button);

                clearButtonAutocomplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                filterDistance.setVisibility(View.GONE);
                                filters.remove(filterLocation);
                                applyFilter();

                                autocompleteFragment.setText("");
                                view.setVisibility(View.GONE);

                            }
                        });

                filterDistance.addOnChangeListener(new RangeSlider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                        filterLocation.setDistance(value);
                        filters.remove(filterLocation);
                        filters.add(filterLocation);
                        applyFilter();
                    }
                });
            }
        });




    }

    private void initPriceFilter() {
        Filter filterMinPrice = new Filter(getContext(), Filter.TYPE_MIN_PRICE);
        Filter filterMaxPrice = new Filter(getContext(), Filter.TYPE_MAX_PRICE);

        filterMinPrice.setMoneyKey(moneyKey);
        filterMaxPrice.setMoneyKey(moneyKey);

        minPrice.addTextChangedListener(new TextWatcher() {
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

                    if (!str.equals(minPrice.getText().toString())) {
                        minPrice.setText(str);
                        minPrice.setSelection(minPrice.getText().length());
                    }

                    filterMinPrice.setMinPrice(Utils.getIntOfStringPrice(minPrice.getText().toString()));

                    filters.remove(filterMinPrice);
                    filters.add(filterMinPrice);

                }
                else {
                    filters.remove(filterMinPrice);
                }
                applyFilter();
            }
        });

        maxPrice.addTextChangedListener(new TextWatcher() {
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

                    if (!str.equals(maxPrice.getText().toString())) {
                        maxPrice.setText(str);
                        maxPrice.setSelection(maxPrice.getText().length());
                    }

                    filterMaxPrice.setMaxPrice(Utils.getIntOfStringPrice(maxPrice.getText().toString()));

                    filters.remove(filterMaxPrice);
                    filters.add(filterMaxPrice);

                }
                else {
                    filters.remove(filterMaxPrice);
                }
                applyFilter();
            }
        });
    }

    private void initTypeSpinner() {
        Filter filterType = new Filter(getContext(), Filter.TYPE_TYPE);

        String[] brut = getResources().getStringArray(R.array.SpinnerTypeOfResidence);
        String[] type = new String[brut.length+1];

        type[0] = getContext().getString(R.string.filter_all_type);
        for (int i=1; i<type.length; i++) {
            type[i] = brut[i-1];
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,type);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerType.setAdapter(arrayAdapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i== 0) {
                    filters.remove(filterType);
                }
                else {
                    filterType.setTypeId(i-1);

                    filters.remove(filterType);
                    filters.add(filterType);

                }
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initRoomSpinner() {
        Filter filterRoom = new Filter(getContext(), Filter.TYPE_ROOM);

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i== 0) {
                    filters.remove(filterRoom);
                }
                else {
                    filterRoom.setMinRoom(i+1);

                    filters.remove(filterRoom);
                    filters.add(filterRoom);
                }
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initCreationDateSpinner() {
        Filter filterCreationDate = new Filter(getContext(), Filter.TYPE_CREATION);

        spinnerCreationDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i== 0) {
                    filters.remove(filterCreationDate);
                }
                else {
                    filterCreationDate.setCreationDateLimit(i);

                    filters.remove(filterCreationDate);
                    filters.add(filterCreationDate);
                }
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initPictureSpinner() {
        Filter filterPicture = new Filter(getContext(), Filter.TYPE_PICTURE);

        spinnerNbrPicture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i== 0) {
                    filters.remove(filterPicture);
                }
                else {
                    filterPicture.setMinNbrPicture(i+1);

                    filters.remove(filterPicture);
                    filters.add(filterPicture);
                }
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initMineCheckbox() {
        Filter filterMine = new Filter(getContext(), Filter.TYPE_MINE);

        checkBoxIsMine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    filterMine.setUserId(currentUser.getUid());

                    filters.remove(filterMine);
                    filters.add(filterMine);

                }
                else {
                    filters.remove(filterMine);
                }

                applyFilter();
            }
        });
    }

    private void initSoldFilter() {
        Filter filterSold = new Filter(getContext(), Filter.TYPE_SOLD);

        checkBoxIsSold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    spinnerSoldDate.setSelection(0);
                    spinnerSoldDate.setVisibility(View.GONE);

                    filters.remove(filterSold);
                    applyFilter();
                }
                else {
                    spinnerSoldDate.setVisibility(View.VISIBLE);

                    filterSold.setCreationDateLimit(0);

                    filters.remove(filterSold);
                    filters.add(filterSold);

                    applyFilter();

                    spinnerSoldDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            filterSold.setDateSoldLimit(i);

                            filters.remove(filterSold);
                            filters.add(filterSold);

                            applyFilter();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            }
        });
    }

    private void initDraftFilter() {
        Filter filterDraft = new Filter(getContext(), Filter.TYPE_DRAFT);

        draftCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    filters.remove(filterDraft);
                    filters.add(filterDraft);

                }
                else {
                    filters.remove(filterDraft);
                }

                applyFilter();
            }
        });
    }

    private void initNotSyncFilter() {
        Filter filterNotSync = new Filter(getContext(), Filter.TYPE_NOT_SYNC);

        notSyncCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    filters.remove(filterNotSync);
                    filters.add(filterNotSync);

                }
                else {
                    filters.remove(filterNotSync);
                }

                applyFilter();
            }
        });
    }

    private void initProximityFilter() {
        Filter filterSchool = new Filter(getContext(),Filter.TYPE_NEXT_SCHOOL);
        Filter filterPark = new Filter(getContext(),Filter.TYPE_NEXT_PARK);
        Filter filterStore = new Filter(getContext(),Filter.TYPE_NEXT_STORE);

        schoolCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    filters.remove(filterSchool);
                    filters.add(filterSchool);

                }
                else {
                    filters.remove(filterSchool);
                }

                applyFilter();
            }
        });
        parkCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    filters.remove(filterPark);
                    filters.add(filterPark);

                }
                else {
                    filters.remove(filterPark);
                }

                applyFilter();
            }
        });
        storeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    filters.remove(filterStore);
                    filters.add(filterStore);

                }
                else {
                    filters.remove(filterStore);
                }

                applyFilter();
            }
        });
    }

    // STATIC VOID

    public static void changeVisibilityOfFragment() {
        if (isOpen) {
            Log.d("TAG", "changeVisibilityOfFragment: ");
            if (mainLayout.getVisibility() == View.VISIBLE) {
                Log.d("TAG", "main is Visible: ");
                if (filters.size() != 0) {
                    Log.d("TAG", "Filter size != 0 ");
                    mainLayout.setVisibility(View.GONE);
                } else {
                    Log.d("TAG", "Filter size == 0: ");
                    closeFragment();
                }
            } else {
                Log.d("TAG", "MAIN INVISIBLE: ");
                mainLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            MainActivity.initFilterFragment();
        }
    }

    public static void closeFragment(){
        isOpen = false;
        viewModelRealEstate.setFilterList(new ArrayList<>());
        MainActivity.switchColorOfFilterItem(false);
        activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public void applyFilter() {
        if (filters.size() == 0 && mainLayout.getVisibility() == View.GONE) {
            closeFragment();
        }
        adapter.initAdapter(filters);
        viewModelRealEstate.setFilterList(filters);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

}