package com.tonyocallimoutou.realestatemanager.ui.detail;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.create.ListPictureRecyclerViewAdapter;

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
public class DetailFragment extends Fragment {

    private static RelativeLayout relativeLayoutFragment;
    private static TextView lblNoInformation;
    private static RelativeLayout relativeLayoutInformation;
    private static TextView textDescription;
    private static TextView textType;
    private static TextView textNumberOfBathroom;
    private static TextView textPrice;
    private static TextView textSurface;
    private static TextView textNumberOfBedroom;
    private static TextView textNumberOfRoom;
    private static TextView userName;
    private static TextView userEmail;
    private static ImageView userProfilePicture;

    private static Context context;

    @BindView(R.id.recycler_view_add_picture_real_estate)
    RecyclerView recyclerViewPicture;

    private static ListPictureRecyclerViewAdapter adapter;

    private List<Photo> photos = new ArrayList<>();
    private static List<User> users = new ArrayList<>();

    private static RealEstate mRealEstate;

    public DetailFragment() {
    }


    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,view);

        relativeLayoutFragment = view.findViewById(R.id.detail_fragment);
        lblNoInformation = view.findViewById(R.id.lbl_no_information);
        relativeLayoutInformation = view.findViewById(R.id.detail_information);
        textDescription = view.findViewById(R.id.detail_description);
        textType = view.findViewById(R.id.detail_type);
        textNumberOfBathroom = view.findViewById(R.id.detail_number_bathroom);
        textPrice = view.findViewById(R.id.detail_price);
        textSurface = view.findViewById(R.id.detail_surface);
        textNumberOfBedroom = view.findViewById(R.id.detail_number_bedroom);
        textNumberOfRoom = view.findViewById(R.id.detail_number_rooms);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        userProfilePicture = view.findViewById(R.id.profile_picture_header_side_view);

        context = getContext();

        recyclerViewPicture.setLayoutManager(new LinearLayoutManager(context));
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true);
        recyclerViewPicture.setLayoutManager(horizontalLayoutManager);
        adapter = new ListPictureRecyclerViewAdapter(context, photos,null);

        recyclerViewPicture.setAdapter(adapter);


        if (view.findViewById(R.id.detail_close_button) != null) {
            closeDetailFragment();
        }
        initInformation();
        return view;
    }

    private static void initInformation() {
        if (mRealEstate != null) {
            lblNoInformation.setVisibility(View.GONE);
            relativeLayoutInformation.setVisibility(View.VISIBLE);

            adapter.initAdapter(mRealEstate.getPhotos());

            textDescription.setText(mRealEstate.getDescription());
            textType.setText(mRealEstate.getType());
            textSurface.setText(mRealEstate.getStringSurface());
            textNumberOfRoom.setText(mRealEstate.getStringNumberOfRooms());
            textNumberOfBathroom.setText(mRealEstate.getStringNumberOfBathrooms());
            textNumberOfBedroom.setText(mRealEstate.getStringNumberOfBedrooms());
            textPrice.setText(mRealEstate.getStringPriceUSD());

            for (User user : users) {
                if (user.getUid().equals(mRealEstate.getUserId())) {
                    Glide.with(context)
                            .load(user.getPicture())
                            .apply(RequestOptions.circleCropTransform())
                            .into(userProfilePicture);

                    userEmail.setText(user.getEmail());
                    userEmail.setTextColor(context.getResources().getColor(R.color.text_color_default));

                    userName.setText(user.getUsername());
                    userName.setTextColor(context.getResources().getColor(R.color.text_color_default));
                }
            }

        }
        else {
            lblNoInformation.setVisibility(View.VISIBLE);
            relativeLayoutInformation.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.detail_close_button)
    public void closeDetailFragment() {
        relativeLayoutFragment.setVisibility(View.GONE);
    }

    public static void getDetailOf(RealEstate realEstate) {
        mRealEstate = realEstate;
        relativeLayoutFragment.setVisibility(View.VISIBLE);
        initInformation();
    }

    public static void setListUser(List<User> data) {
        users = data;
    }
}