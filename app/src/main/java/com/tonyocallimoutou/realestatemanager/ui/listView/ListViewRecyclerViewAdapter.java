package com.tonyocallimoutou.realestatemanager.ui.listView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private List<RealEstate> mRealEstate;
    private final ListItemClickListener mListItemClickListener;
    private final Context mContext;
    private static Integer positionDetail;
    private boolean isConnected;

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

        if (positionDetail != null && position == positionDetail) {
            holder.layout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.grey_light));
        }
        else {
            holder.layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }

        int mainPicturePosition = realEstate.getMainPicturePosition();
        Glide.with(mContext)
                .load(realEstate.getPhotos().get(mainPicturePosition).getReference())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.realEstateImage);

        holder.soldBanner.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
        holder.errorProgressBar.setVisibility(View.GONE);
        holder.percentage.setVisibility(View.GONE);
        holder.draftText.setVisibility(View.GONE);
        holder.notSync.setVisibility(View.GONE);

        holder.realEstateType.setText(realEstate.getStringType(mContext));
        holder.realEstatePrice.setText(realEstate.getStringPriceUSD(mContext));

        if (realEstate.isDraft()) {
            holder.draftText.setVisibility(View.VISIBLE);
            if (realEstate.getPlace() != null) {
                holder.realEstateLocation.setText(realEstate.getPlace().getAddress());
            }
        }

        else {
            holder.realEstateLocation.setText(realEstate.getPlace().getAddress());

            if (realEstate.isSold()) {
                holder.soldBanner.setVisibility(View.VISIBLE);
            }

            if (! realEstate.isSync()) {
                if (isConnected) {
                    int percentage = (int) Math.round(realEstate.getProgressSync());
                    String percentageStr = percentage + "%";
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(percentage);
                    holder.percentage.setVisibility(View.VISIBLE);
                    holder.percentage.setText(percentageStr);
                } else {
                    holder.errorProgressBar.setVisibility(View.VISIBLE);
                }
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

    public void initConnection(boolean isConnected) {
        this.isConnected = isConnected;
        notifyDataSetChanged();
    }

    public void setBackgroundOf(int position) {
        positionDetail = position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_layout)
        RelativeLayout layout;
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
        @BindView(R.id.progress_bar)
        ContentLoadingProgressBar progressBar;
        @BindView(R.id.error_progress)
        TextView errorProgressBar;
        @BindView(R.id.progress_bar_percentage)
        TextView percentage;
        @BindView(R.id.list_view_draft)
        TextView draftText;
        @BindView(R.id.list_view_not_sync)
        TextView notSync;


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
