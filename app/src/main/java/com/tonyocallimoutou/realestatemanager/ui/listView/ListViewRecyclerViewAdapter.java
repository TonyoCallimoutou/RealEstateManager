package com.tonyocallimoutou.realestatemanager.ui.listView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private List<RealEstate> mRealEstate;
    private final ListItemClickListener mListItemClickListener;

    public ListViewRecyclerViewAdapter(List<RealEstate> residences,
                                       ListItemClickListener listItemClickListener) {
        mRealEstate = residences;
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

        holder.realEstateType.setText(realEstate.getType());
        holder.realEstatePrice.setText(realEstate.getStringPriceUSD());
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
