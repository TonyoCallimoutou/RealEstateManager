package com.tonyocallimoutou.realestatemanager.ui.listView;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.ui.detail.DetailFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ListViewFragment extends Fragment {

    private static TextView lblNoRealEstate;
    private RecyclerView mRecyclerView;

    private static ListViewRecyclerViewAdapter adapter;

    private static final List<RealEstate> mRealEstate = new ArrayList<>();

    public ListViewFragment() {
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view_recycler_view);
        lblNoRealEstate = view.findViewById(R.id.lbl_no_real_estate);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new ListViewRecyclerViewAdapter(getContext(), mRealEstate, new ListViewRecyclerViewAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                RealEstate realEstate = mRealEstate.get(position);

                MainActivity.initDetailFragment(getActivity(),realEstate);
            }
        });

        mRecyclerView.setAdapter(adapter);

        initList();

        return view;
    }

    private static void initList(){
        if (lblNoRealEstate != null) {
            if (mRealEstate.size() == 0) {
                lblNoRealEstate.setVisibility(View.VISIBLE);
            } else {
                lblNoRealEstate.setVisibility(View.GONE);
            }
        }
        if (adapter!= null) {
            adapter.initAdapter(mRealEstate);
        }
    }

    public static void initResidenceList(List<RealEstate> result) {
        mRealEstate.clear();
        mRealEstate.addAll(result);
        initList();
    }
}