package com.tonyocallimoutou.realestatemanager.ui.filter;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.ui.create.CreateOrEditRealEstateActivity;
import com.tonyocallimoutou.realestatemanager.ui.listView.ListViewRecyclerViewAdapter;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    private static LinearLayout mainLayout;

    @BindView(R.id.filter_spinner_country)
    AutoCompleteTextView editTextCountry;
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

    private FilterRecyclerViewAdapter adapter;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;

    private static FragmentActivity activity;
    private static FilterFragment fragment;
    public static boolean isOpen;

    private static List<Filter> filters;
    private static ViewModelRealEstate viewModelRealEstate;
    private static List<String> countries = new ArrayList<>();
    private static Map<String,List<String>> mapListCountryCity = new HashMap<>();

    private static User currentUser;

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

        mainLayout = view.findViewById(R.id.filter_main_layout);

        filters = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new FilterRecyclerViewAdapter(filters, new FilterRecyclerViewAdapter.FilterItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                String filterType = filters.get(position).getFilterType();
                setDataVisible(filterType);
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
                    textMoreOrLessFilter.setText(getString(R.string.filter_more_filter));
                    imageViewMoreFilter.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_add_black_24dp, null));
                }
                else {
                    moreFilter.setVisibility(View.GONE);
                    textMoreOrLessFilter.setText(getString(R.string.filter_less_filter));
                    imageViewMoreFilter.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_minus_24dp, null));
                }
            }
        });
    }

    private void setDataVisible(String filterType) {
        if (filterType.equals(Filter.TYPE_MIN_PRICE)) {
            minPrice.setText("");
        }
        else if (filterType.equals(Filter.TYPE_MAX_PRICE)) {
            maxPrice.setText("");
        }
        else if (filterType.equals(Filter.TYPE_TYPE)) {
            spinnerType.setSelection(0);
        }
        else if (filterType.equals(Filter.TYPE_ROOM)) {
            spinnerRoom.setSelection(0);
        }
        else if (filterType.equals(Filter.TYPE_CREATION)) {
            spinnerCreationDate.setSelection(0);
        }
        else if (filterType.equals(Filter.TYPE_PICTURE)) {
            spinnerNbrPicture.setSelection(0);
        }
        else if (filterType.equals(Filter.TYPE_SOLD)) {
            checkBoxIsSold.setChecked(false);
        }
        else if (filterType.equals(Filter.TYPE_MINE)) {
            checkBoxIsMine.setChecked(false);
        }
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
    }

    private void initPlaceFilter() {


    }

    private void initPriceFilter() {
        Filter filterMinPrice = new Filter(Filter.TYPE_MIN_PRICE);
        Filter filterMaxPrice = new Filter(Filter.TYPE_MAX_PRICE);
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

                    applyFilter();
                }
                else {
                    filters.remove(filterMinPrice);
                    adapter.initAdapter(filters);
                }
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

                    applyFilter();
                }
                else {
                    filters.remove(filterMaxPrice);
                    adapter.initAdapter(filters);
                }
            }
        });
    }

    private void initTypeSpinner() {
        Filter filterType = new Filter(Filter.TYPE_TYPE);

        String[] brut = getResources().getStringArray(R.array.SpinnerTypeOfResidence);
        String[] type = new String[brut.length+1];

        type[0] = "All type";
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
                    filterType.setType(type[i]);

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
        Filter filterRoom = new Filter(Filter.TYPE_ROOM);

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
        Filter filterCreationDate = new Filter(Filter.TYPE_CREATION);

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
        Filter filterPicture = new Filter(Filter.TYPE_PICTURE);

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
        Filter filterMine = new Filter(Filter.TYPE_MINE);

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
        Filter filterSold = new Filter(Filter.TYPE_SOLD);

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


    // STATIC VOID

    public static void changeVisibilityOfFragment() {
        if (mainLayout.getVisibility() == View.VISIBLE) {
            if (filters.size() != 0) {
                mainLayout.setVisibility(View.GONE);
            }
            else {
                isOpen = false;
                activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        else {
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    public void applyFilter() {
        adapter.initAdapter(filters);
        viewModelRealEstate.setListWithFilter(filters, null);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

}