package com.example.tycoongamev3;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tycoongamev3.databinding.ManagerFragmentListBinding;
import com.example.tycoongamev3.ManagerContent;

/**
 * A fragment representing a list of Items.
 */
public class ManagerFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private ManagerFragmentListBinding binding;
    protected RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ManagerFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ManagerFragment newInstance(int columnCount) {
        ManagerFragment fragment = new ManagerFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ManagerFragmentListBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.manager_list);

        // Set the adapter
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        recyclerView.setAdapter(new ManagerRecyclerViewAdapter(ManagerContent.ITEMS));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(view1 -> NavHostFragment.findNavController(ManagerFragment.this)
                .navigate(R.id.action_ManagerFragment_to_SecondFragment));
    }
}