package com.example.tycoongamev3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tycoongamev3.databinding.MainFragmentBinding;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    private MainFragmentBinding binding;
    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected BusinessRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Business> mDataset;
    private static SaveViewModel viewModel;
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();

    public static void addMoney(BigDecimal money2) {
        viewModel.addMoney(money2);
    }

    public static BigDecimal getMoney() {
        return viewModel.getMoney().getValue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDataset = MainActivity.getBusinesses();
        viewModel = new ViewModelProvider(requireActivity()).get(SaveViewModel.class);

        // TODO: Get rid of max money mode
        viewModel.addMoney(BigDecimal.valueOf(Long.MAX_VALUE));
    }

    // RecyclerView Code used from:
    // https://github.com/android/views-widgets-samples/blob/main/RecyclerView/Application/src/main/java/com/example/android/recyclerview/RecyclerViewFragment.java#L86
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mAdapter = new BusinessRecyclerViewAdapter(mDataset, viewModel, binding, getViewLifecycleOwner());
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        for (Business b:mDataset) {
            b.setTopBar(binding.topLayout);
        }

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to a linear layout manager
     */
    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the buttons so that they navigate between the screens
        binding.buttonManager.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_SecondFragment_to_ManagerFragment));
        binding.buttonUpgrade.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_SecondFragment_to_UpgradeFragment));
        binding.buttonPrestige.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_SecondFragment_to_PrestigeFragment));

        TextView topView = binding.topLayout.findViewById(R.id.topTextView);
        topView.setText(Business.toCurrencyNotation(getMoney(), true));

        viewModel.getMoney().observe(getViewLifecycleOwner(), set -> {
            // Update the selected filters UI
            deactivateButtons();
            topView.setText(Business.toCurrencyNotation(getMoney(), true));
        });
    }

    public void deactivateButtons(){
        for (int i = 0; i < businesses.size(); i++) {
            Business business = businesses.get(i);
            if(business.getCostStorage().compareTo(MainFragment.getMoney()) <= 0) {
                business.setLevelable(true);
                // TODO: UI changes
            } else {
                business.setLevelable(false);
                // TODO: UI changes
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}