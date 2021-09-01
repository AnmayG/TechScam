package com.example.tycoongamev3;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tycoongamev3.PlaceholderContent.PlaceholderItem;
import com.example.tycoongamev3.databinding.ManagerFragmentBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ManagerRecyclerViewAdapter extends RecyclerView.Adapter<ManagerRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public ManagerRecyclerViewAdapter(List<PlaceholderItem> items) {
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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public final TextView mDetailsView;
        public PlaceholderItem mItem;

        public ViewHolder(ManagerFragmentBinding binding) {
            super(binding.getRoot());
            mContentView = binding.itemNumber;
            mDetailsView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}