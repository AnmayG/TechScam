package com.example.tycoongamev3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tycoongamev3.databinding.MainFragmentBinding;

import java.math.BigDecimal;
import java.util.ArrayList;

// Code used from here: https://github.com/android/views-widgets-samples/tree/main/RecyclerView

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class BusinessRecyclerViewAdapter extends RecyclerView.Adapter<BusinessRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<Business> mDataSet;
    private SaveViewModel viewModel;
    private BigDecimal money = BigDecimal.ZERO;
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView blocker;
        private final TextView textView;
        private final ImageView imageView;
        private final ProgressBar progressBar;
        private final Button button;
        private final TextView levelView;
        private final TextView revView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(v1 -> {

            });
            blocker = v.findViewById(R.id.blocker);
            textView = v.findViewById(R.id.textView);
            imageView = v.findViewById(R.id.imageView);
            progressBar = v.findViewById(R.id.progressBar);
            button = v.findViewById(R.id.button);
            levelView = v.findViewById(R.id.levelTextView);
            revView = v.findViewById(R.id.revView);
        }

        public TextView getTextView() {
            return textView;
        }
        public TextView getBlocker() {
            return blocker;
        }
        public ImageView getImageView(){return imageView;}
        public ProgressBar getProgressBar(){return progressBar;}
        public Button getButton(){return button;}
        public TextView getLevelView() {
            return levelView;
        }
        public TextView getRevView() {
            return revView;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)



    /**
     * Initialize the dataset of the Adapter.
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public BusinessRecyclerViewAdapter(ArrayList<Business> dataSet, SaveViewModel viewModel,
                                       MainFragmentBinding binding, LifecycleOwner viewLifecycleOwner) {
        mDataSet = dataSet;

        this.viewModel = viewModel;
        this.viewModel.getMoney().observe(viewLifecycleOwner, money -> {
            TextView moneyView = binding.topLayout.findViewById(R.id.topTextView);
            moneyView.setText(Business.toCurrencyNotation(money, true));
            this.money = money;
            for (int i = 0; i < businesses.size(); i++) {
                Business business = businesses.get(i);
                if(business.isPurchasable()) continue;
                if(money.subtract(business.getUnlockCost().multiply(BigDecimal.valueOf(100)))
                        .compareTo(BigDecimal.ZERO) >= 0) {
                    business.setPurchasable(true);
                    // TODO: Add UI change here
                } else {
                    business.setPurchasable(false);
                    // TODO: Add UI change here
                }
            }
        });
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.business_ui_layout, viewGroup, false);
        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Business business = mDataSet.get(position);

        // Get Business at this position and set the views so that it can edit them later.
        business.setViews(viewHolder.getBlocker(), viewHolder.getTextView(), viewHolder.getImageView(),
                viewHolder.getProgressBar(), viewHolder.getButton(), viewHolder.getLevelView(),
                viewHolder.getRevView());

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if(business.isUnlocked()){
            viewHolder.getTextView().setVisibility(View.VISIBLE);
            viewHolder.getImageView().setVisibility(View.VISIBLE);
            viewHolder.getProgressBar().setVisibility(View.VISIBLE);
            viewHolder.getButton().setVisibility(View.VISIBLE);
            viewHolder.getLevelView().setVisibility(View.VISIBLE);
            viewHolder.getRevView().setVisibility(View.VISIBLE);
            viewHolder.getBlocker().setVisibility(View.GONE);
        }

        String blockerText = business.getName() + ": " +
                             Business.toCurrencyNotation(business.getUnlockCost().multiply(BigDecimal.valueOf(100)), false);
        viewHolder.getBlocker().setText(blockerText);
        viewHolder.getTextView().setText(business.formatSecondsToTimeStamp(business.getCooldown()));
        viewHolder.getLevelView().setText(String.valueOf(business.getLevel()));
        viewHolder.getButton().setText(Business.toCurrencyNotation(business.getCostStorage(), false));
        viewHolder.getProgressBar().setMax(business.getCooldown() * 1000);
        viewHolder.getProgressBar().setProgress(business.getProgressValue());
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}