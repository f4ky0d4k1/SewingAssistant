package ru.dharatyan.sewingassistant.ui.operations;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.ui.DeleteDialogFragment;

public class OperationsEditAdapter extends PagedListAdapter<Operation, OperationsEditAdapter.ViewHolder> {

    private final LiveData<PagedList<Position>> positionLivePagedList;
    private final LiveData<PagedList<Model>> modelLivePagedList;
    private final Fragment fragment;
    private final OperationsViewModel operationsViewModel;

    OperationsEditAdapter(DiffUtil.ItemCallback<Operation> diffUtilCallback, Fragment fragment) {
        super(diffUtilCallback);
        operationsViewModel = new ViewModelProvider(fragment).get(OperationsViewModel.class);
        positionLivePagedList = operationsViewModel.getAllPositions();
        modelLivePagedList = operationsViewModel.getAllModels();
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_operations_edit, parent, false);
        return new ViewHolder(view, modelLivePagedList, positionLivePagedList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(Objects.requireNonNull(getItem(position)));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final Button deleteButton;
        private final Button disableButton;
        private final TextView totalText;
        private final EditText quantityEdit;
        private final Spinner positionSpinner;
        private final Spinner modelSpinner;
        private final Spinner articleSpinner;

        private final ArrayAdapter<Position> positionAdapter;
        private final ArrayAdapter<Model> modelAdapter;
        private final ArrayAdapter<Article> articleAdapter;
        private Operation operation;
        private Model model;
        private Article article;
        private Position position;
        private LiveData<PagedList<Article>> articleLivePagedList;

        private boolean selectedEnable;
        boolean init;

        private View view;

        private final AdapterView.OnItemSelectedListener onModelSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == -1) return;
                if (articleLivePagedList != null)
                    articleLivePagedList.removeObservers(fragment.getViewLifecycleOwner());
                articleLivePagedList = operationsViewModel.getArticlesByModelId(modelAdapter.getItem(i).getId());
                articleLivePagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                    articleSpinner.setOnItemSelectedListener(null);
                    articleAdapter.clear();
                    articleAdapter.addAll(data.snapshot());
                    articleAdapter.notifyDataSetChanged();
                    articleSpinner.setOnItemSelectedListener(onArticleSelectedListener);
                    if (article != null)
                        articleSpinner.setSelection(articleAdapter.getPosition(article));
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        private final AdapterView.OnItemSelectedListener onPositionSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == -1) return;
                save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        private final AdapterView.OnItemSelectedListener onArticleSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == -1) return;
                save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        ViewHolder(View view, LiveData<PagedList<Model>> modelLiveDataPagedList, LiveData<PagedList<Position>> positionLiveDataPagedList) {
            super(view);
            this.view = view;
            deleteButton = view.findViewById(R.id.button_operation_delete);
            disableButton = view.findViewById(R.id.button_operation_disable);
            totalText = view.findViewById(R.id.text_operation_total);
            quantityEdit = view.findViewById(R.id.text_operation_quantity);
            positionSpinner = view.findViewById(R.id.spinner_position);
            modelSpinner = view.findViewById(R.id.spinner_model);
            articleSpinner = view.findViewById(R.id.spinner_article);

            positionAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);
            modelAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);
            articleAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);

            positionSpinner.setAdapter(positionAdapter);
            articleSpinner.setAdapter(articleAdapter);
            modelSpinner.setAdapter(modelAdapter);

            init = false;

            positionLiveDataPagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                positionSpinner.setOnItemSelectedListener(null);
                positionAdapter.clear();
                positionAdapter.addAll(data.snapshot());
                positionAdapter.notifyDataSetChanged();
                if (position != null)
                    positionSpinner.setSelection(positionAdapter.getPosition(position));
                positionSpinner.setOnItemSelectedListener(onPositionSelectedListener);
            });

            modelLiveDataPagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                modelSpinner.setOnItemSelectedListener(null);
                modelAdapter.clear();
                modelAdapter.addAll(data.snapshot());
                modelAdapter.notifyDataSetChanged();
                modelSpinner.setOnItemSelectedListener(onModelSelectedListener);
                onModelSelectedListener.onItemSelected(null, null, modelAdapter.getPosition(model), 0);
                if (model != null)
                    modelSpinner.setSelection(modelAdapter.getPosition(model));
            });

            quantityEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    save();
                }
                return false;
            });

            deleteButton.setOnClickListener(v ->
                    new DeleteDialogFragment(
                            (dialogInterface, i) -> operationsViewModel.deleteOperationById(operation.getId()),
                            (dialogInterface, i) -> dialogInterface.cancel())
                            .show(fragment.getParentFragmentManager(), "deleteDialog")
            );

            View.OnClickListener onDisableButtonClickListener = v -> {
                selectedEnable = !selectedEnable;
                save();
            };
            disableButton.setOnClickListener(onDisableButtonClickListener);
        }

        public void bind(Operation operation) {
            init = false;
            this.operation = operation;
            position = operation.getPositionId() == null ? null : operationsViewModel.getPositionById(operation.getPositionId());
            article = operation.getArticleId() == null ? null : operationsViewModel.getArticleById(operation.getArticleId());
            model = article == null ? null : operationsViewModel.getModelById(article.getModelId());

            quantityEdit.setText(String.valueOf(operation.getQuantity()));
            totalText.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(operation.getQuantity() * position.getCost()));

            positionSpinner.setSelection(positionAdapter.getPosition(position));
            modelSpinner.setSelection(modelAdapter.getPosition(model));

            selectedEnable = operation.isEnabled();
            view.setBackgroundResource(operation.isEnabled() ? R.drawable.alert_light_frame : R.drawable.alert_dark_frame);

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) { }
                init = true;
            }).start();
        }

        private void save() {
            if (!init) return;
            Position selectedPosition = (Position) positionSpinner.getSelectedItem();
            Article selectedArticle = (Article) articleSpinner.getSelectedItem();
            Integer selectedQuantity;
            try {
                selectedQuantity = Integer.parseInt(quantityEdit.getText().toString());
            } catch (NumberFormatException e) {
                selectedQuantity = 0;
            }
            if (selectedPosition == null || selectedArticle == null)
                return;
            if (selectedPosition.equals(position) && selectedArticle.equals(article) &&
                    selectedQuantity.equals(operation.getQuantity()) && selectedEnable == operation.isEnabled())
                return;
            operationsViewModel.saveOperation(new Operation(operation.getId(), selectedQuantity, selectedEnable, operation.getDate(), selectedPosition.getId(), selectedArticle.getId()));
        }
    }
}
