package ru.dharatyan.sewingassistant.ui.operations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.databinding.FragmentEditDateBinding;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.util.Constants;
import ru.dharatyan.sewingassistant.util.diffutil.OperationDiffUtilCallback;

public class DateEditFragment extends Fragment {

    private OperationsViewModel operationsViewModel;
    private FragmentEditDateBinding binding;
    private LiveData<PagedList<Operation>> operationLivePagedList;
    private RecyclerView operationsRecyclerView;
    private Spinner yearSpinner, monthSpinner, daySpinner;
    private Date selectedDate;
    private ArrayAdapter<Integer> daySpinnerAdapter;
    private OperationsEditAdapter operationsEditAdapter;

    private Button createButton;
    private EditText quantityEdit;
    private Spinner positionSpinner;
    private Spinner modelSpinner;
    private Spinner articleSpinner;

    private LiveData<PagedList<Article>> articleLivePagedList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        operationsViewModel = new ViewModelProvider(this).get(OperationsViewModel.class);

        binding = FragmentEditDateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initBundle();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                remove();
                getParentFragmentManager().popBackStackImmediate();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        operationsRecyclerView = binding.recyclerViewEditDateOperations;
        iniOperationsRecyclerView();

        yearSpinner = binding.spinnerYear;
        initYearSpinner();

        daySpinner = binding.spinnerDay;
        initDaySpinner();

        monthSpinner = binding.spinnerMonth;
        initMonthSpinner();

        initOperationCreateLayout();
        return root;
    }

    private void iniOperationsRecyclerView() {
        operationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        operationsEditAdapter = new OperationsEditAdapter(new OperationDiffUtilCallback(), this);
        operationsRecyclerView.setAdapter(operationsEditAdapter);
    }

    private void initYearSpinner() {
        List<Integer> years = IntStream.rangeClosed(
                Constants.START_DATE,
                LocalDate.now().getYear())
                .boxed().collect(Collectors.toList());
        yearSpinner.setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                years));
        yearSpinner.setSelection(selectedDate.getYear() - Constants.START_DATE);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int year = (Integer) adapterView.getItemAtPosition(i);
                if (year != selectedDate.getYear()) {
                    selectedDate.setYear(year);
                    showOperations();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initMonthSpinner() {
        monthSpinner.setSelection(selectedDate.getMonth() - 1);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int month, long l) {
                if (month != selectedDate.getMonth() - 1) {
                    selectedDate.setMonth(month + 1);
                    refreshDaySpinner();
                }
                daySpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initDaySpinner() {
        daySpinner.setEnabled(false);
        if (daySpinnerAdapter == null) daySpinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item);
        daySpinner.setAdapter(daySpinnerAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != selectedDate.getDay() - 1) {
                    selectedDate.setDay(i + 1);
                    showOperations();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        refreshDaySpinner();
    }

    private void refreshDaySpinner() {
        int month = selectedDate.getMonth(), year = selectedDate.getYear();
        daySpinnerAdapter.clear();
        daySpinnerAdapter.addAll(IntStream.rangeClosed(1, Date.daysInMonth(month, year))
                .boxed().collect(Collectors.toList()));
        daySpinnerAdapter.notifyDataSetChanged();
        if (selectedDate.getDay() <= Date.daysInMonth(month, year))
            daySpinner.setSelection(selectedDate.getDay() - 1);
        else daySpinner.setSelection(0);
        showOperations();
    }


    private void showOperations() {
        if (operationLivePagedList != null)
            operationLivePagedList.removeObservers(getViewLifecycleOwner());
        operationLivePagedList = operationsViewModel.getOperationsByDate(selectedDate);
        operationLivePagedList.observe(getViewLifecycleOwner(), data -> {
            operationsEditAdapter.submitList(data);
        });
    }

    public static Bundle prepareBundle(Date date) {
        if (date == null) date = new Date();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Date.class.getSimpleName(), date);
        return bundle;
    }

    private void initBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) selectedDate = (Date) bundle.getParcelable(Date.class.getSimpleName());
        if (selectedDate == null) selectedDate = new Date();
    }

    private void initOperationCreateLayout() {
        createButton = binding.buttonOperationCreate;
        quantityEdit = binding.textOperationQuantity;
        positionSpinner = binding.spinnerPosition;
        modelSpinner = binding.spinnerModel;
        articleSpinner = binding.spinnerArticle;

        ArrayAdapter<Position> positionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        ArrayAdapter<Model> modelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        ArrayAdapter<Article> articleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);

        operationsViewModel.getAllPositions().observe(getViewLifecycleOwner(), data -> {
            positionAdapter.clear();
            positionAdapter.addAll(data.snapshot());
            positionAdapter.notifyDataSetChanged();
        });
        operationsViewModel.getAllModels().observe(getViewLifecycleOwner(), data -> {
            modelAdapter.clear();
            modelAdapter.addAll(data.snapshot());
            modelAdapter.notifyDataSetChanged();
        });

        positionSpinner.setAdapter(positionAdapter);
        articleSpinner.setAdapter(articleAdapter);
        modelSpinner.setAdapter(modelAdapter);

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != -1) {
                    if (articleLivePagedList != null)
                        articleLivePagedList.removeObservers(getViewLifecycleOwner());
                    articleLivePagedList = operationsViewModel.getArticlesByModelId(modelAdapter.getItem(i).getId());
                    articleLivePagedList.observe(getViewLifecycleOwner(), data -> {
                        articleAdapter.clear();
                        articleAdapter.addAll(data.snapshot());
                        articleAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        createButton.setOnClickListener(v -> save());
    }

    private void save() {
        Position selectedPosition = (Position) positionSpinner.getSelectedItem();
        Article selectedArticle = (Article) articleSpinner.getSelectedItem();
        Integer selectedQuantity;
        try {
            selectedQuantity = Integer.parseInt(quantityEdit.getText().toString());
        } catch (NumberFormatException e) {
            selectedQuantity = null;
        }
        if (selectedPosition == null || selectedArticle == null ||
                selectedQuantity == null || selectedQuantity <= 0) return;
        operationsViewModel.saveOperation(new Operation(null, selectedQuantity, selectedDate, selectedPosition.getId(), selectedArticle.getId()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}