package com.tonyocallimoutou.realestatemanager.ui.listView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private List<RealEstate> mRealEstate;
    private final ListItemClickListener mListItemClickListener;
    private final Context mContext;

    public ListViewRecyclerViewAdapter(Context context, List<RealEstate> realEstate,
                                       ListItemClickListener listItemClickListener) {
        mContext = context;
        mRealEstate = realEstate;
        mListItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public ListViewRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewRecyclerViewAdapter.ViewHolder holder, int position) {
        RealEstate realEstate = mRealEstate.get(position);


        int mainPicturePosition = realEstate.getMainPicturePosition();
        Glide.with(mContext)
                .load(realEstate.getPhotos().get(mainPicturePosition).getReference())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.realEstateImage);

        holder.realEstateType.setText(realEstate.getType());
        holder.realEstateLocation.setText(realEstate.getPlace().getAddress());
        holder.realEstatePrice.setText(realEstate.getStringPriceUSD(mContext));

        if (realEstate.isSold()) {
            holder.soldBanner.setVisibility(View.VISIBLE);
        }
        else {
            holder.soldBanner.setVisibility(View.GONE);
        }

        holder.progressBar.setVisibility(View.GONE);
        holder.errorProgressBar.setVisibility(View.GONE);

        if (realEstate.getProgressSync() != 100) {
            if (Utils.isInternetAvailable(mContext)) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.progressBar.setProgress((int) Math.round(realEstate.getProgressSync()));
            }
            else {
                holder.errorProgressBar.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mRealEstate.size()  ;
    }

    public interface ListItemClickListener{
        void onListItemClick(int position);
    }

    public void initAdapter(List<RealEstate> residences) {
        mRealEstate = residences;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_view_image_real_estate)
        ImageView realEstateImage;
        @BindView(R.id.list_view_type_real_estate)
        TextView realEstateType;
        @BindView(R.id.list_view_location_real_estate)
        TextView realEstateLocation;
        @BindView(R.id.list_view_price_real_estate)
        TextView realEstatePrice;
        @BindView(R.id.list_view_image_sold_banner)
        ImageView soldBanner;
        @BindView(R.id.progressBar)
        ContentLoadingProgressBar progressBar;
        @BindView(R.id.error_progress)
        TextView errorProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mListItemClickListener.onListItemClick(position);
                }
            });
        }
    }
}
