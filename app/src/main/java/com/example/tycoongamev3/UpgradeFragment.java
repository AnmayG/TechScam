package com.example.tycoongamev3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tycoongamev3.databinding.UpgradeFragmentListBinding;

/**
 * A fragment representing a list of Items.
 */
public class UpgradeFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private UpgradeFragmentListBinding binding;
    protected RecyclerView recyclerView;

    private SaveViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UpgradeFragment() {
    }

    @SuppressWarnings("unused")
    public static UpgradeFragment newInstance(int columnCount) {
        UpgradeFragment fragment = new UpgradeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UpgradeFragmentListBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        recyclerView = rootView.findViewById(R.id.upgrade_list);

        // Set the adapter
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

        viewModel = new ViewModelProvider(requireActivity()).get(SaveViewModel.class);
        recyclerView.setAdapter(new UpgradeRecyclerViewAdapter(UpgradeContent.ITEMS, viewModel, binding,
                getViewLifecycleOwner(), getResources(), context.getPackageName()));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(view1 -> NavHostFragment.findNavController(UpgradeFragment.this)
                .navigate(R.id.action_UpgradeFragment_to_SecondFragment));

        viewModel = new ViewModelProvider(requireActivity()).get(SaveViewModel.class);
        viewModel.getMoney().observe(getViewLifecycleOwner(), money -> {
            TextView moneyView = binding.topLayout.findViewById(R.id.moneyView);
            moneyView.setText("Money: " + Business.toCurrencyNotation(money, true));
        });
    }
}