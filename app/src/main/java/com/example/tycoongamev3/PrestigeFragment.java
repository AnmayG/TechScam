package com.example.tycoongamev3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tycoongamev3.databinding.PrestigeFragmentBinding;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrestigeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static SaveViewModel viewModel;
    private PrestigeFragmentBinding binding;
    private BigDecimal amountRep = BigDecimal.ZERO;
    private ArrayList<Business> businesses = MainActivity.getBusinesses();
    private BigDecimal repGain = BigDecimal.ZERO;

    public PrestigeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SaveViewModel.class);
        if(viewModel.getRep().getValue() == null) viewModel.addRep(BigDecimal.ZERO);
        amountRep = viewModel.getRep().getValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = PrestigeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(view1 -> NavHostFragment.findNavController(PrestigeFragment.this)
                .navigate(R.id.action_PrestigeFragment_to_SecondFragment));
        TextView prestigeView = binding.prestigeView;
        TextView repView = binding.repView;
        repView.setText(Business.toCurrencyNotation(viewModel.getRep().getValue(), true)
                            .substring(1));

        viewModel.getMoney().observe(getViewLifecycleOwner(), money -> {
            // Update the selected filters UI
            BigDecimal levelSum = BigDecimal.ZERO;
            for (int i = 0; i < businesses.size(); i++) {
                levelSum = levelSum.add(BigDecimal.valueOf(businesses.get(i).getLevel() - 1));
            }
            levelSum = levelSum.add(BigDecimal.ONE);
            repGain = money.divide(levelSum, BigDecimal.ROUND_DOWN);
            prestigeView.setText(Business.toCurrencyNotation(repGain, true).substring(1));
        });

        binding.button2.setOnClickListener(view12 -> {
            viewModel.addRep(repGain);
            repView.setText(Business.toCurrencyNotation(viewModel.getRep().getValue(), true)
                                .substring(1));
            prestigeView.setText(Business.toCurrencyNotation(BigDecimal.ZERO, true)
                                    .substring(1));

            // TODO: Add a reset function that'll reset everything
            //  Right now it's just zeroing the money and leaving it.
            //  You can do this by setting all of the unlockables up again
            viewModel.setMoneyZero();
        });
    }
}