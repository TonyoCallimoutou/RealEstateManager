package com.tonyocallimoutou.realestatemanager.ui.create;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListPictureRecyclerViewAdapter extends RecyclerView.Adapter<ListPictureRecyclerViewAdapter.ViewHolder>{

    private List<Photo> mPictures;
    private final Context mContext;
    private final ListRemoveClickListener mRemoveListener;
    private final ListPictureClickListener mPictureListener;

    public ListPictureRecyclerViewAdapter(Context context, List<Photo> pictures, ListPictureClickListener pictureListener, @Nullable ListRemoveClickListener listener) {
        mContext = context;
        mPictures = pictures;
        mPictureListener = pictureListener;
        mRemoveListener = listener;
    }

    @NonNull
    @Override
    public ListPictureRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.include_real_estate_add_picture_item, null);
        return new ListPictureRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPictureRecyclerViewAdapter.ViewHolder holder, int position) {
        Photo picture = mPictures.get(position);

        Glide.with(mContext)
                .load(picture.getReference())
                .into(holder.addPicture);

        if (mRemoveListener == null) {
            holder.removePicture.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public interface ListRemoveClickListener{
        void onClick(int position);
    }

    public interface ListPictureClickListener {
        void onClick(int position);
    }

    public void initAdapter(List<Photo> pictures) {
        mPictures = pictures;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.add_picture_item)
        ImageView addPicture;
        @BindView(R.id.new_picture_remove_picture)
        FloatingActionButton removePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            removePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRemoveListener.onClick(getAdapterPosition());
                }
            });

            addPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPictureListener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
