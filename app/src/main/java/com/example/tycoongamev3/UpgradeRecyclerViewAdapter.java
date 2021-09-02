package com.example.tycoongamev3;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tycoongamev3.UpgradeContent.UpgradeItem;
import com.example.tycoongamev3.databinding.UpgradeFragmentBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UpgradeItem}.
 */
public class UpgradeRecyclerViewAdapter extends RecyclerView.Adapter<UpgradeRecyclerViewAdapter.ViewHolder> {
    private static List<UpgradeItem> workingValues = new ArrayList<>();
    /**
     * This is a list that contains the original items we saved.
     */
    private static List<UpgradeItem> saveValues = new ArrayList<>();
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();

    // TODO: Add money view that'll constantly be updated

    public UpgradeRecyclerViewAdapter(List<UpgradeItem> items) {
        // If the working values (or at least its size) is equal to the save that we have, set the items to the new input.
        // This is because I want to have the latest copy of `items` but I don't want to change workingValues if it's already been bought.
        // That would lead to stuff reappearing and it's just generally not fun to deal with.
        // So if the workingValues and the saveValues are the same size (both empty or both full) give them the new items.
        if(workingValues.size() == saveValues.size()) {
            saveValues = items;
            // object reference is bad when you're duplicating
            workingValues = new ArrayList<>(items);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UpgradeFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UpgradeItem upgrade = workingValues.get(position);
        holder.mItem = upgrade;
        holder.mIdView.setText(upgrade.content);
        holder.mContentView.setText(upgrade.details);
        holder.mPriceView.setText(Business.toCurrencyNotation(upgrade.price, true));

        holder.buyButton.setOnClickListener(view -> {
            // TODO: Deactivate buttons on bind if unaffordable
            int p = removeAt(holder.getBindingAdapterPosition());
            int multi = 10 + 10 * (p/businesses.size());
            businesses.get(p % businesses.size()).setMultiplier(multi);
        });
    }

    // https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
    public int removeAt(int position) {
        int origIndex = saveValues.indexOf(workingValues.remove(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, workingValues.size());
        return origIndex;
    }

    @Override
    public int getItemCount() {
        return workingValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mPriceView;
        public final ImageView mImageView;
        public final Button buyButton;
        public UpgradeItem mItem;

        public ViewHolder(UpgradeFragmentBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mPriceView = binding.priceView;
            mImageView = binding.imageView3;
            buyButton = binding.buyButton;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}