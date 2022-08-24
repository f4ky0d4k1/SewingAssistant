package ru.dharatyan.sewingassistant.ui.operations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.ui.view.UntouchableRecyclerView;
import ru.dharatyan.sewingassistant.util.diffutil.OperationDiffUtilCallback;

public class DateAdapter extends PagedListAdapter<Date, DateAdapter.ViewHolder> {

    private final Fragment fragment;
    private final OperationsViewModel operationsViewModel;

    protected DateAdapter(DiffUtil.ItemCallback<Date> diffUtilCallback, Fragment fragment) {
        super(diffUtilCallback);
        operationsViewModel = new ViewModelProvider(fragment).get(OperationsViewModel.class);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_dates, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final Button deleteButton, showButton;
        final TextView dateText, totalText;
        final UntouchableRecyclerView operationsRecyclerView;
        final OperationsInDatesAdapter operationsAdapter;
        private Date date;

        ViewHolder(View view) {
            super(view);
            dateText = view.findViewById(R.id.text_date_date);
            totalText = view.findViewById(R.id.text_date_total);
            operationsRecyclerView = view.findViewById(R.id.recyclerView_operations);
            operationsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            operationsAdapter = new OperationsInDatesAdapter(new OperationDiffUtilCallback(), fragment);
            operationsRecyclerView.setAdapter(operationsAdapter);
            view.setOnClickListener(v -> onViewClick());

            showButton = view.findViewById(R.id.button_date_show);
            showButton.setOnClickListener(v -> {
                if (operationsRecyclerView.getVisibility() == View.VISIBLE)
                    operationsRecyclerView.setVisibility(View.GONE);
                else operationsRecyclerView.setVisibility(View.VISIBLE);
            });

            deleteButton = view.findViewById(R.id.button_date_delete);
            deleteButton.setOnClickListener(v -> {
                if (date != null) operationsViewModel.deleteOperationsByDate(date);
            });
        }

        public void bind(Date date) {
            this.date = date;

            dateText.setText(date.toString());
            LiveData<PagedList<Operation>> operationLivePagedList = operationsViewModel.getOperationsByDate(date);
            operationLivePagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                totalText.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(operationsViewModel.getTotalByDate(date)));
                operationsAdapter.submitList(data);
            });

        }

        private void onViewClick() {
            FragmentTransaction fragmentTransaction = fragment.getParentFragmentManager().beginTransaction();
            Fragment fragment = new DateEditFragment();
            fragment.setArguments(DateEditFragment.prepareBundle(Date.parse(dateText.getText().toString())));
            fragmentTransaction.replace(R.id.fragment_operations, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
        }
    }
}
