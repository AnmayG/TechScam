package com.example.tycoongamev3;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tycoongamev3.ManagerContent.ManagerItem;
import com.example.tycoongamev3.databinding.ManagerFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ManagerItem}.
 */
public class ManagerRecyclerViewAdapter extends RecyclerView.Adapter<ManagerRecyclerViewAdapter.ViewHolder> {

    private final List<ManagerItem> mValues;
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();

    public ManagerRecyclerViewAdapter(List<ManagerItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ManagerFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mDetailsView.setText(mValues.get(position).details);
        holder.buyButton.setOnClickListener(view -> {
            int p = holder.getBindingAdapterPosition();
            businesses.get(p).setManager(true);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public final TextView mDetailsView;
        public final ImageView mImageView;
        public final Button buyButton;
        public ManagerItem mItem;

        public ViewHolder(ManagerFragmentBinding binding) {
            super(binding.getRoot());
            mContentView = binding.itemNumber;
            mDetailsView = binding.content;
            mImageView = binding.imageView2;
            buyButton = binding.buyButton;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}