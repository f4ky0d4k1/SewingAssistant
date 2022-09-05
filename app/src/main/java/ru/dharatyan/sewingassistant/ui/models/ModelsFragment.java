package ru.dharatyan.sewingassistant.ui.models;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.dharatyan.sewingassistant.databinding.FragmentModelsBinding;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.util.diffutil.ModelDiffUtilCallback;

public class ModelsFragment extends Fragment {

    private ModelsViewModel modelsViewModel;
    private FragmentModelsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        modelsViewModel =
                new ViewModelProvider(this).get(ModelsViewModel.class);

        binding = FragmentModelsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView modelsRecyclerView = binding.recyclerViewModels;
        modelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final ModelsAdapter modelsAdapter = new ModelsAdapter(new ModelDiffUtilCallback(), this);
        modelsRecyclerView.setAdapter(modelsAdapter);
        modelsViewModel.getAllModels().observe(getViewLifecycleOwner(), modelsAdapter::submitList);

        final EditText modelNameText = binding.textModelName;
        binding.buttonModelCreate.setOnClickListener(view ->
                modelsViewModel.saveModel(new Model(null, modelNameText.getText().toString())));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}