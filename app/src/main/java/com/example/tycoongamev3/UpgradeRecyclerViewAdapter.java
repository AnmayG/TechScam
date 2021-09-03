package com.example.tycoongamev3;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tycoongamev3.UpgradeContent.UpgradeItem;
import com.example.tycoongamev3.databinding.ManagerFragmentListBinding;
import com.example.tycoongamev3.databinding.UpgradeFragmentBinding;
import com.example.tycoongamev3.databinding.UpgradeFragmentListBinding;

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
    private BigDecimal money = BigDecimal.ZERO;
    private MoneyViewModel viewModel;

    public UpgradeRecyclerViewAdapter(List<UpgradeItem> items, MoneyViewModel viewModel,
                                      UpgradeFragmentListBinding binding, LifecycleOwner viewLifecycleOwner) {
        // If the working values (or at least its size) is equal to the save that we have, set the items to the new input.
        // This is because I want to have the latest copy of `items` but I don't want to change workingValues if it's already been bought.
        // That would lead to stuff reappearing and it's just generally not fun to deal with.
        // So if the workingValues and the saveValues are the same size (both empty or both full) give them the new items.
        if(workingValues.size() == saveValues.size()) {
            saveValues = items;
            // object reference is bad when you're duplicating
            workingValues = new ArrayList<>(items);
        }

        this.viewModel = viewModel;
        this.viewModel.getMoney().observe(viewLifecycleOwner, money -> {
            TextView moneyView = binding.topLayout.findViewById(R.id.moneyView);
            moneyView.setText(Business.toCurrencyNotation(money, true));
            this.money = money;
            for (int i = 0; i < workingValues.size(); i++) {
                UpgradeItem upgradeItem = workingValues.get(i);
                if(money.subtract(upgradeItem.price).compareTo(BigDecimal.ZERO) >= 0) {
                    upgradeItem.activated = true;
                    // TODO: Add UI change here
                } else {
                    upgradeItem.activated = false;
                    // TODO: Add UI change here
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UpgradeFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UpgradeItem upgradeItem = workingValues.get(position);
        holder.mItem = upgradeItem;
        holder.mIdView.setText(upgradeItem.content);
        holder.mContentView.setText(upgradeItem.details);
        holder.mPriceView.setText(Business.toCurrencyNotation(upgradeItem.price, true));

        holder.buyButton.setOnClickListener(view -> {
            int p = getWorkingPosition(position);
            if(saveValues.get(p).activated) {
                p = removeAt(holder.getBindingAdapterPosition());
                int multi = 10 + 10 * (p/businesses.size());
                businesses.get(p % businesses.size()).setMultiplier(multi);
                this.viewModel.addMoney(-1 * saveValues.get(p).price.longValueExact());
            }
        });
    }

    // https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
    public int removeAt(int position) {
        int origIndex = saveValues.indexOf(workingValues.remove(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, workingValues.size());
        return origIndex;
    }

    public int getWorkingPosition(int position){
        int p = saveValues.indexOf(workingValues.get(position));
        return saveValues.indexOf(workingValues.get(position));
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