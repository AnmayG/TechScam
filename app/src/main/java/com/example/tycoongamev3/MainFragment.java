package com.example.tycoongamev3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tycoongamev3.databinding.MainFragmentBinding;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private MainFragmentBinding binding;
    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected BusinessRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Business> mDataset;
    public static long[] money = {0};

    public static void addMoney(long money2) { MainFragment.money[0] += money2; }
    public static long getMoney() { return MainFragment.money[0]; }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDataset = MainActivity.getBusinesses();
    }

    // RecyclerView Code used from:
    // https://github.com/android/views-widgets-samples/blob/main/RecyclerView/Application/src/main/java/com/example/android/recyclerview/RecyclerViewFragment.java#L86
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mAdapter = new BusinessRecyclerViewAdapter(mDataset);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}