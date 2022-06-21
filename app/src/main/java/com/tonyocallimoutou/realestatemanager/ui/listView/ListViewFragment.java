package com.tonyocallimoutou.realestatemanager.ui.listView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.ui.create.CreateOrEditRealEstateActivity;

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
    private static final List<RealEstate> mRealEstateSync = new ArrayList<>();
    private static final List<RealEstate> mNotSyncList = new ArrayList<>();
    private static final List<RealEstate> mDrafts = new ArrayList<>();

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

                if (realEstate.isDraft()) {
                    CreateOrEditRealEstateActivity.startActivity(getActivity(),realEstate);
                }
                else {
                    MainActivity.initDetailFragment(realEstate);
                }
            }
        });

        mRecyclerView.setAdapter(adapter);

        initList();

        return view;
    }

    private static void initList(){
        mRealEstate.clear();
        mRealEstate.addAll(mRealEstateSync);
        mRealEstate.addAll(mNotSyncList);
        mRealEstate.addAll(mDrafts);

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

    public static void initRealEstateSyncList(List<RealEstate> result) {
        mRealEstateSync.clear();
        mRealEstateSync.addAll(result);
        initList();
    }

    public static void initNotSyncList(List<RealEstate> notSyncList) {
        mNotSyncList.clear();
        mNotSyncList.addAll(notSyncList);
        initList();
    }

    public static void initDraftList(List<RealEstate> drafts) {
        mDrafts.clear();
        mNotSyncList.addAll(drafts);
        initList();
    }

    public static void setConnection() {
        initList();
    }
}