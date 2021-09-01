package com.example.tycoongamev3;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tycoongamev3.UpgradeContent.UpgradeItem;
import com.example.tycoongamev3.databinding.UpgradeFragmentBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UpgradeItem}.
 */
public class UpgradeRecyclerViewAdapter extends RecyclerView.Adapter<UpgradeRecyclerViewAdapter.ViewHolder> {

    private final List<UpgradeItem> mValues;

    public UpgradeRecyclerViewAdapter(List<UpgradeItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(UpgradeFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public UpgradeItem mItem;

        public ViewHolder(UpgradeFragmentBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}