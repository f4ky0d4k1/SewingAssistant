package ru.dharatyan.sewingassistant.ui.positions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Currency;
import java.util.Locale;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.databinding.FragmentPositionsBinding;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.util.diffutil.PositionDiffUtilCallback;

public class PositionsFragment extends Fragment {

    private PositionsViewModel positionsViewModel;
    private FragmentPositionsBinding binding;

    private EditText positionNameEdit;
    private EditText positionCostEdit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        positionsViewModel =
                new ViewModelProvider(this).get(PositionsViewModel.class);

        binding = FragmentPositionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        positionNameEdit = binding.textPositionName;
        positionCostEdit = binding.textPositionCost;
        RecyclerView positionRecyclerView = binding.recyclerViewPositions;

        binding.currencyCost.setText(Currency.getInstance(Locale.getDefault()).getSymbol());
        binding.buttonPositionCreate.setOnClickListener(this::onButtonPositionSaveClick);

        positionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PositionAdapter positionAdapter = new PositionAdapter(new PositionDiffUtilCallback(), this);
        positionRecyclerView.setAdapter(positionAdapter);
        positionsViewModel.getAllPositions().observe(getViewLifecycleOwner(), positionAdapter::submitList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onButtonPositionSaveClick(View v) {
        double cost = 0;
        try {
            cost = Double.parseDouble(positionCostEdit.getText().toString());
        } catch (NumberFormatException ignored) { }
        positionsViewModel.savePosition(new Position(null, positionNameEdit.getText().toString(), cost));
    }
}