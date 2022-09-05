package ru.dharatyan.sewingassistant.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.databinding.FragmentReportBinding;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.service.XLSService;
import ru.dharatyan.sewingassistant.util.Constants;
import ru.dharatyan.sewingassistant.util.diffutil.FileDiffUtilCallback;

public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;
    private FragmentReportBinding binding;
    private Spinner spinnerStartYear;
    private Spinner spinnerStartMonth;
    private Spinner spinnerStartDay;
    private Spinner spinnerEndYear;
    private Spinner spinnerEndMonth;
    private Spinner spinnerEndDay;
    private Date startDate;
    private Date endDate;
    private Button buttonReportCreate;
    private RecyclerView recyclerViewReports;
    private EditText editLastname;
    private EditText editMonth;
    private EditText editPaid;

    private ArrayAdapter<Integer> spinnerStartDayAdapter;
    private ArrayAdapter<Integer> spinnerEndDayAdapter;
    private ReportsAdapter reportsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reportViewModel =
                new ViewModelProvider(this).get(ReportViewModel.class);

        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editLastname = binding.editLastname;
        editMonth = binding.editMonth;
        editPaid = binding.editPaid;
        spinnerStartYear = binding.spinnerStartYear;
        spinnerStartMonth = binding.spinnerStartMonth;
        spinnerStartDay = binding.spinnerStartDay;
        spinnerEndYear = binding.spinnerEndYear;
        spinnerEndMonth = binding.spinnerEndMonth;
        spinnerEndDay = binding.spinnerEndDay;
        buttonReportCreate = binding.buttonReportCreate;
        recyclerViewReports = binding.recyclerViewReports;
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(getContext()));
        reportsAdapter = new ReportsAdapter(new FileDiffUtilCallback(), this);
        recyclerViewReports.setAdapter(reportsAdapter);
        startDate = new Date();
        endDate = new Date();

        initYearStartSpinner();
        initMonthStartSpinner();
        initSpinnerStartDay();
        initYearEndSpinner();
        initMonthEndSpinner();
        initSpinnerEndDay();
        initButtonReportCreate();

        refreshRecyclerViewReports();

        return root;
    }

    private void refreshRecyclerViewReports() {
        File[] files = getContext() == null ? null : getContext().getExternalFilesDir(null).listFiles();
        reportsAdapter.submitList(files == null ? new ArrayList<>() : Arrays.stream(files).collect(Collectors.toList()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initButtonReportCreate() {
        buttonReportCreate.setOnClickListener((v) -> {
            try {
                double paid = editPaid.getText().length() == 0 ? 0 : Double.parseDouble(editPaid.getText().toString());
                String lastname = editLastname.getText().toString();
                String month = editMonth.getText().toString();
                List<Operation> operations = reportViewModel.getOperationsByDates(startDate, endDate);
                List<Position> positions = reportViewModel.getAllPositions();
                List<Article> articles = reportViewModel.getArticlesByIds(operations.stream().map(Operation::getArticleId).collect(Collectors.toList()));
                List<Model> models = reportViewModel.getModelsByIds(articles.stream().map(Article::getModelId).collect(Collectors.toList()));

                FileOutputStream fos = new FileOutputStream(new File(requireContext().getExternalFilesDir(null), String.format("report(%s_%s).xls", startDate.toString(), endDate.toString())));
                new XLSService(requireContext()).createReport(fos, operations, positions, articles, models, lastname, month, paid);
                fos.close();

                refreshRecyclerViewReports();
            } catch (IOException | NumberFormatException e) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initYearStartSpinner() {
        List<Integer> years = IntStream.rangeClosed(
                Constants.START_DATE,
                LocalDate.now().getYear())
                .boxed().collect(Collectors.toList());
        spinnerStartYear.setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                years));
        spinnerStartYear.setSelection(startDate.getYear() - Constants.START_DATE);
        spinnerStartYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int year = (Integer) adapterView.getItemAtPosition(i);
                startDate.setYear(year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initMonthStartSpinner() {
        spinnerStartMonth.setSelection(startDate.getMonth() - 1);
        spinnerStartMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int month, long l) {
                if (month != startDate.getMonth() - 1) {
                    startDate.setMonth(month + 1);
                    refreshSpinnerStartDay();
                }
                spinnerStartDay.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initSpinnerStartDay() {
        spinnerStartDay.setEnabled(false);
        if (spinnerStartDayAdapter == null) spinnerStartDayAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item);
        spinnerStartDay.setAdapter(spinnerStartDayAdapter);
        spinnerStartDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != startDate.getDay() - 1)
                    startDate.setDay(i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        refreshSpinnerStartDay();
    }

    private void refreshSpinnerStartDay() {
        int month = startDate.getMonth(), year = startDate.getYear();
        spinnerStartDayAdapter.clear();
        spinnerStartDayAdapter.addAll(IntStream.rangeClosed(1, Date.daysInMonth(month, year))
                .boxed().collect(Collectors.toList()));
        spinnerStartDayAdapter.notifyDataSetChanged();
        if (startDate.getDay() <= Date.daysInMonth(month, year))
            spinnerStartDay.setSelection(startDate.getDay() - 1);
        else spinnerStartDay.setSelection(0);
    }

    private void initYearEndSpinner() {
        List<Integer> years = IntStream.rangeClosed(
                Constants.START_DATE,
                LocalDate.now().getYear())
                .boxed().collect(Collectors.toList());
        spinnerEndYear.setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                years));
        spinnerEndYear.setSelection(endDate.getYear() - Constants.START_DATE);
        spinnerEndYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int year = (Integer) adapterView.getItemAtPosition(i);
                endDate.setYear(year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initMonthEndSpinner() {
        spinnerEndMonth.setSelection(endDate.getMonth() - 1);
        spinnerEndMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int month, long l) {
                if (month != endDate.getMonth() - 1) {
                    endDate.setMonth(month + 1);
                    refreshSpinnerEndDay();
                }
                spinnerEndDay.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initSpinnerEndDay() {
        spinnerEndDay.setEnabled(false);
        if (spinnerEndDayAdapter == null) spinnerEndDayAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item);
        spinnerEndDay.setAdapter(spinnerEndDayAdapter);
        spinnerEndDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != endDate.getDay() - 1)
                    endDate.setDay(i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        refreshSpinnerEndDay();
    }

    private void refreshSpinnerEndDay() {
        int month = endDate.getMonth(), year = endDate.getYear();
        spinnerEndDayAdapter.clear();
        spinnerEndDayAdapter.addAll(IntStream.rangeClosed(1, Date.daysInMonth(month, year))
                .boxed().collect(Collectors.toList()));
        spinnerEndDayAdapter.notifyDataSetChanged();
        if (endDate.getDay() <= Date.daysInMonth(month, year))
            spinnerEndDay.setSelection(endDate.getDay() - 1);
        else spinnerEndDay.setSelection(0);
    }
}