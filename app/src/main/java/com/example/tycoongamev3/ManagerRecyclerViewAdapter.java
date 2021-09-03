package com.example.tycoongamev3;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tycoongamev3.ManagerContent.ManagerItem;
import com.example.tycoongamev3.databinding.ManagerFragmentBinding;
import com.example.tycoongamev3.databinding.ManagerFragmentListBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ManagerItem}.
 */
public class ManagerRecyclerViewAdapter extends RecyclerView.Adapter<ManagerRecyclerViewAdapter.ViewHolder> {
    private final List<ManagerItem> saveValues;
    private final List<ManagerItem> mValues;
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();
    private final SaveViewModel viewModel;
    private final Resources resources;
    private final String packageName;

    // I'm using Fragement Transactions on the money property so that I can update this fragment's textview
    // This seems like a pretty hacky way to do it but it lets me update all of the textviews at the same time whenever the money changes.

    public ManagerRecyclerViewAdapter(List<ManagerItem> items, SaveViewModel viewModel,
                                      ManagerFragmentListBinding binding, LifecycleOwner viewLifecycleOwner,
                                      Resources resources, String packageName) {
        this.viewModel = viewModel;
        this.resources = resources;
        this.packageName = packageName;

        mValues = items;
        // Check to see if the model save has the values, and if not then set it. If so pull from the save.
        if(viewModel.getSaveManagerValues().getValue() == null) {
            saveValues = new ArrayList<>(items);
            viewModel.setSaveManagerValues(saveValues);
        } else {
            saveValues = viewModel.getSaveManagerValues().getValue();
        }

        this.viewModel.getMoney().observe(viewLifecycleOwner, money -> {
            TextView moneyView = binding.topLayout.findViewById(R.id.moneyView);
            moneyView.setText(Business.toCurrencyNotation(money, true));
            for (int i = 0; i < mValues.size(); i++) {
                ManagerItem managerItem = mValues.get(i);
                if(money.subtract(managerItem.price).compareTo(BigDecimal.ZERO) >= 0) {
                    managerItem.activated = true;
                    // TODO: Add UI change here
                } else {
                    managerItem.activated = false;
                    // TODO: Add UI change here
                }
            }
        });
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
        managerItem.button = holder.buyButton;
        holder.mContentView.setText(managerItem.content);
        holder.mDetailsView.setText(managerItem.details);
        holder.mPriceView.setText(Business.toCurrencyNotation(managerItem.price, true));

        String image = businesses.get(getWorkingPosition(position)).getImg();
        holder.mImageView.setImageDrawable(ResourcesCompat.getDrawable(resources,
                resources.getIdentifier(image, "drawable", packageName), null));

        holder.buyButton.setOnClickListener(view -> {
            int p = getWorkingPosition(position);
            if(saveValues.get(p).activated && businesses.get(p).isUnlocked()) {
                p = removeAt(holder.getBindingAdapterPosition());
                businesses.get(p).setManager(true);
                this.viewModel.addMoney(saveValues.get(p).price.negate());
            } else if (!businesses.get(p).isUnlocked()){
                Toast toast = Toast.makeText(holder.mContentView.getContext(), "You need to own a business before hiring a manager.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public int removeAt(int position) {
        int origIndex = saveValues.indexOf(mValues.remove(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
        return origIndex;
    }

    public int getWorkingPosition(int position){
        return saveValues.indexOf(mValues.get(position));
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