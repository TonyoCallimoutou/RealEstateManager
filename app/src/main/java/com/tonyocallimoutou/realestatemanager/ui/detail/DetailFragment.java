package com.tonyocallimoutou.realestatemanager.ui.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.ui.mapview.MiniMapFragment;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.create.ListPictureRecyclerViewAdapter;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements ListPictureRecyclerViewAdapter.ListPictureClickListener {


    @BindView(R.id.lbl_no_information)
    TextView lblNoInformation;
    @BindView(R.id.recycler_view_add_picture_real_estate)
    RecyclerView recyclerViewPicture;
    @BindView(R.id.detail_information)
    RelativeLayout relativeLayoutInformation;
    @BindView(R.id.detail_description)
    TextView textDescription;
    @BindView(R.id.detail_type)
    TextView textType;
    @BindView(R.id.detail_number_bathroom)
    TextView textNumberOfBathroom;
    @BindView(R.id.detail_price)
    TextView textPrice;
    @BindView(R.id.detail_surface)
    TextView textSurface;
    @BindView(R.id.detail_number_bedroom)
    TextView textNumberOfBedroom;
    @BindView(R.id.detail_number_rooms)
    TextView textNumberOfRoom;
    @BindView(R.id.detail_location)
    TextView textLocation;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_email)
    TextView userEmail;
    @BindView(R.id.profile_picture_information)
    ImageView userProfilePicture;
    @BindView(R.id.detail_creation_date)
    TextView creationDate;
    @BindView(R.id.detail_sold_date)
    TextView soldDate;
    @BindView(R.id.detail_image_sold_banner)
    ImageView soldBanner;

    private static RelativeLayout relativeLayoutFragment;
    private static View view;

    private static final String BUNDLE_REAL_ESTATE = "BUNDLE_REAL_ESTATE";
    private static RealEstate mRealEstate;

    private static ViewModelRealEstate viewModelRealEstate;

    private ListPictureRecyclerViewAdapter adapter;

    private List<Photo> photos = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static User currentUser;

    private static FragmentActivity activity;
    private static DetailFragment fragment;

    public DetailFragment() {
    }


    public static DetailFragment newInstance(@Nullable RealEstate realEstate) {
        fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_REAL_ESTATE, realEstate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRealEstate = (RealEstate) getArguments().getSerializable(BUNDLE_REAL_ESTATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,view);

        viewModelRealEstate = new ViewModelProvider(requireActivity()).get(ViewModelRealEstate.class);

        relativeLayoutFragment = view.findViewById(R.id.detail_fragment);

        activity = getActivity();

        recyclerViewPicture.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        recyclerViewPicture.setLayoutManager(horizontalLayoutManager);
        adapter = new ListPictureRecyclerViewAdapter(getContext(), photos,this, null);

        recyclerViewPicture.setAdapter(adapter);

        initInformation();
        return view;
    }

    private void initInformation() {
        if (canCloseFragment()) {
            MainActivity.setVisibilityAddAndSearchMenuItem(false);
        }

        if (mRealEstate != null) {
            lblNoInformation.setVisibility(View.GONE);
            relativeLayoutInformation.setVisibility(View.VISIBLE);

            MiniMapFragment.initMiniMap(getActivity(),mRealEstate.getPlace());

            photos = mRealEstate.getPhotos();
            adapter.initAdapter(mRealEstate.getPhotos());

            textDescription.setText(mRealEstate.getDescription());
            textType.setText(mRealEstate.getType());
            textSurface.setText(mRealEstate.getStringSurface());
            textNumberOfRoom.setText(mRealEstate.getStringNumberOfRooms());
            textNumberOfBathroom.setText(mRealEstate.getStringNumberOfBathrooms());
            textNumberOfBedroom.setText(mRealEstate.getStringNumberOfBedrooms());
            textLocation.setText(mRealEstate.getPlace().getName());
            textPrice.setText(mRealEstate.getStringPriceUSD());
            String strCreationDate = getString(R.string.detail_date_creation) + " " + mRealEstate.getCreationDate();
            creationDate.setText(strCreationDate);

            if (mRealEstate.isSold()) {
                soldBanner.setVisibility(View.VISIBLE);
                String strSoldDate = getString(R.string.detail_date_sold) + " " + mRealEstate.getSoldDate();
                soldDate.setText(strSoldDate);
            }
            else {
                soldBanner.setVisibility(View.GONE);
            }

            for (User user : users) {
                if (user.getUid().equals(mRealEstate.getUserId())) {
                    Glide.with(getContext())
                            .load(user.getUrlPicture())
                            .apply(RequestOptions.circleCropTransform())
                            .into(userProfilePicture);

                    userEmail.setText(user.getEmail());

                    userName.setText(user.getUsername());
                }
            }

            if (mRealEstate.getUserId().equals(currentUser.getUid())) {
                MainActivity.setVisibilityEditMenuItem(true);
            }

            if (mRealEstate.isSold()){
                MainActivity.setVisibilityGoToEditMenuItem(false);
            }
            else {
                MainActivity.setVisibilityGoToEditMenuItem(true);
            }


        }
        else {
            lblNoInformation.setVisibility(View.VISIBLE);
            relativeLayoutInformation.setVisibility(View.GONE);
            if (canCloseFragment()) {
                closeFragment();
            }
        }
    }

    @Override
    public void onClick(int position) {
        View view = getLayoutInflater().inflate(R.layout.alert_real_estate_picture_description, null);

        ImageView picture = view.findViewById(R.id.alert_dialog_picture_image_description);
        TextView pictureDescription = view.findViewById(R.id.picture_description);

        Glide.with(this)
                .load(photos.get(position).getReference())
                .into(picture);

        pictureDescription.setText(photos.get(position).getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.detail_close_button)
    public void closeDetailFragment() {
        closeFragment();
    }

    public static boolean canCloseFragment() {
        return (view.findViewById(R.id.detail_close_button) != null && relativeLayoutFragment.getVisibility() == View.VISIBLE);
    }

    public static void closeFragment() {
        MainActivity.setVisibilityEditMenuItem(false);
        MainActivity.setVisibilityAddAndSearchMenuItem(true);
        activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public static void setListUser(List<User> data) {
        users = data;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static RealEstate getActualRealEstate() {
        return mRealEstate;
    }
}