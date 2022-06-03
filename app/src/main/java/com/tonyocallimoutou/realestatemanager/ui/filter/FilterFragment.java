package com.tonyocallimoutou.realestatemanager.ui.filter;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.ui.listView.ListViewRecyclerViewAdapter;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;

import java.util.ArrayList;
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

    @BindView(R.id.filter_location)
    TextInputEditText inputLocation;
    @BindView(R.id.filter_min_price)
    EditText minPrice;
    @BindView(R.id.filter_max_price)
    EditText maxPrice;
    @BindView(R.id.filter_spinner_room)
    Spinner spinnerRoom;
    @BindView(R.id.filter_checkbox_is_sold)
    CheckBox checkBoxIsSold;
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

    private static FragmentActivity activity;
    private static FilterFragment fragment;
    public static boolean isOpen;

    private static List<Filter> filters;
    private static ViewModelRealEstate viewModelRealEstate;

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
                String type = filters.get(position).getType();
                filters.remove(position);
                applyFilter();
                setDataVisible(type);
            }
        });

        recyclerView.setAdapter(adapter);

        isOpen = true;
        activity = getActivity();

        initMoreFilter();

        initPriceFilter();

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

    private void setDataVisible(String type) {
        if (type.equals(Filter.TYPE_MIN_PRICE)) {
            minPrice.setText("");
        }
        else if (type.equals(Filter.TYPE_MAX_PRICE)) {
            maxPrice.setText("");
        }
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

    public static void changeVisibilityOfFragment() {
        if (mainLayout.getVisibility() == View.VISIBLE) {
            if (filters.size() != 0) {
                Log.d("TAG", "closeFragment: ");
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

}