package com.tonyocallimoutou.realestatemanager.ui.filter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.util.Filter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder> {

    private List<Filter> mFilters;
    private FilterItemClickListener mListener;

    public FilterRecyclerViewAdapter(List<Filter> filter,
                                     FilterRecyclerViewAdapter.FilterItemClickListener filterItemClickListener) {
        mFilters = filter;
        mListener = filterItemClickListener;
    }

    @NonNull
    @Override
    public FilterRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_filter_item, null);
        return new FilterRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterRecyclerViewAdapter.ViewHolder holder, int position) {
        Filter filter = mFilters.get(position);

        holder.filterTitle.setText(filter.toString());

        holder.filterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilters.size()  ;
    }

    public interface FilterItemClickListener{
        void onListItemClick(int position);
    }

    public void initAdapter(List<Filter> filters) {
        mFilters = filters;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.filter_text_recycler)
        Button filterTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                }
            });
        }
    }
}
