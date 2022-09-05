package ru.dharatyan.sewingassistant.ui.operations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.databinding.FragmentOperationsBinding;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.util.diffutil.DateDiffUtilCallback;

public class OperationsFragment extends Fragment {

    private OperationsViewModel operationsViewModel;
    private FragmentOperationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        operationsViewModel = new ViewModelProvider(this).get(OperationsViewModel.class);

        binding = FragmentOperationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView dateRecyclerView = binding.recyclerViewDates;
        binding.buttonNewDate.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = this.getParentFragmentManager().beginTransaction();
            Fragment fragment = new DateEditFragment();
            fragment.setArguments(DateEditFragment.prepareBundle(null));
            fragmentTransaction.replace(R.id.fragment_operations, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
        });

        dateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LiveData<PagedList<Date>> dateLivePagedList = operationsViewModel.getDates();
        DateAdapter adapter = new DateAdapter(new DateDiffUtilCallback(), this);
        dateLivePagedList.observe(getViewLifecycleOwner(), adapter::submitList);
        dateRecyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        operationsViewModel = null;
    }
}