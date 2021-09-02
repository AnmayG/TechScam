package com.example.tycoongamev3;

import androidx.annotation.NonNull;
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
    private List<ManagerItem> saveValues = new ArrayList<>();
    private List<ManagerItem> mValues = new ArrayList<>();
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();

    // I'm using Fragement Transactions on the money property so that I can update this fragment's textview
    // This seems like a pretty hacky way to do it but it lets me update all of the textviews at the same time whenever the money changes.

    public ManagerRecyclerViewAdapter(List<ManagerItem> items) {
        if(saveValues.size() == mValues.size()) {
            mValues = items;
            saveValues = new ArrayList<>(items);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ManagerFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ManagerItem managerItem = mValues.get(position);
        holder.mItem = managerItem;
        holder.mContentView.setText(managerItem.content);
        holder.mDetailsView.setText(managerItem.details);
        holder.mPriceView.setText(Business.toCurrencyNotation(managerItem.price, true));
        // TODO: Deactivate buttons on bind if unaffordable

        holder.buyButton.setOnClickListener(view -> {
            int p = removeAt(holder.getBindingAdapterPosition());
            businesses.get(p).setManager(true);
        });
    }

    public int removeAt(int position) {
        int origIndex = saveValues.indexOf(mValues.remove(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
        return origIndex;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public final TextView mDetailsView;
        public final TextView mPriceView;
        public final ImageView mImageView;
        public final Button buyButton;
        public ManagerItem mItem;

        public ViewHolder(ManagerFragmentBinding binding) {
            super(binding.getRoot());
            mContentView = binding.itemNumber;
            mDetailsView = binding.content;
            mPriceView = binding.priceView;
            mImageView = binding.imageView2;
            buyButton = binding.buyButton;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}