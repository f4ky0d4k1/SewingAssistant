package ru.dharatyan.sewingassistant.ui.positions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        positionsViewModel =
                new ViewModelProvider(this).get(PositionsViewModel.class);

        binding = FragmentPositionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView positionRecyclerView = binding.recyclerViewPositions;
        positionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final PositionAdapter positionAdapter = new PositionAdapter(new PositionDiffUtilCallback(), this);
        positionRecyclerView.setAdapter(positionAdapter);
        positionsViewModel.getAllPositions().observe(getViewLifecycleOwner(), positionAdapter::submitList);

        EditText positionNameEdit = binding.textPositionName;
        EditText positionCostEdit = binding.textPositionCost;
        Button buttonPositionSave = binding.buttonPositionCreate;

        binding.currencyCost.setText(Currency.getInstance(Locale.getDefault()).getSymbol());
        buttonPositionSave.setOnClickListener(v -> {
            try {
                double cost = Double.parseDouble(positionCostEdit.getText().toString());
                if (positionNameEdit.length() > 0 && cost > 0) positionsViewModel.savePosition(new Position(null, positionNameEdit.getText().toString(), cost));
            } catch (NumberFormatException e) {
                Toast.makeText(v.getContext(), R.string.toast_create_position_empty_cost, Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}