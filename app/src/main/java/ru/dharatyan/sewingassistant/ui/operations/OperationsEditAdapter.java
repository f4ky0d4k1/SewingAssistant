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

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;

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
        holder.bind(getItem(position));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final Button deleteButton;
        private final TextView totalText;
        private final EditText quantityEdit;
        private final Spinner positionSpinner;
        private final Spinner modelSpinner;
        private final Spinner articleSpinner;


        //        private Integer positionSelection;
//        private Integer articleSelection;
//        private Integer modelSelection;
//        private Integer selectedQuantity;
        //        private Long operationId;
        private final ArrayAdapter<Position> positionAdapter;
        private final ArrayAdapter<Model> modelAdapter;
        private final ArrayAdapter<Article> articleAdapter;
        private Operation operation;
        private Model model;
        private Article article;
        private Position position;
        private LiveData<PagedList<Article>> articleLivePagedList;

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
            if (selectedPosition.equals(position) && selectedArticle.equals(article) &&
                    selectedQuantity.equals(operation.getQuantity())) return;
            operationsViewModel.saveOperation(new Operation(operation.getId(), selectedQuantity, operation.getDate(), selectedPosition.getId(), selectedArticle.getId()));
        }

        public void bind(Operation operation) {
            this.operation = operation;

            if (operation.getId() != null) {
                position = operation.getPositionId() == null ? null : operationsViewModel.getPositionById(operation.getPositionId());
                article = operation.getArticleId() == null ? null : operationsViewModel.getArticleById(operation.getArticleId());
                model = article == null ? null : operationsViewModel.getModelById(article.getModelId());

                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setClickable(true);
                quantityEdit.setText(String.valueOf(operation.getQuantity()));
                totalText.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(operation.getQuantity() * position.getCost()));

//                positionSelection = ;
                positionSpinner.setSelection(positionAdapter.getPosition(position));

//                modelSelection = ;
                modelSpinner.setSelection(modelAdapter.getPosition(model));
            } else {
                deleteButton.setVisibility(View.INVISIBLE);
                deleteButton.setClickable(false);
                quantityEdit.setText(null);
                totalText.setText(null);

                positionSpinner.setSelection(-1);
                modelSpinner.setSelection(-1);
//                positionSelection = -1;
//                modelSelection = -1;
//                articleSelection = -1;
            }
        }

        ViewHolder(View view, LiveData<PagedList<Model>> modelLiveDataPagedList, LiveData<PagedList<Position>> positionLiveDataPagedList) {
            super(view);
            deleteButton = view.findViewById(R.id.button_operation_delete);
            totalText = view.findViewById(R.id.text_operation_total);
            quantityEdit = view.findViewById(R.id.text_operation_quantity);
            positionSpinner = view.findViewById(R.id.spinner_position);
            modelSpinner = view.findViewById(R.id.spinner_model);
            articleSpinner = view.findViewById(R.id.spinner_article);

            positionAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);
            modelAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);
            articleAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);

            positionLiveDataPagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                positionAdapter.clear();
                positionAdapter.addAll(data.snapshot());
                positionAdapter.notifyDataSetChanged();
            });
            positionSpinner.setAdapter(positionAdapter);
            positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    if (positionSelection != -1 &&
//                            !positionSelection.equals(i) &&
//                            selectedQuantity != null &&
//                            selectedQuantity != 0)
//                        operationsViewModel.saveOperation(new Operation(
//                                operationId,
//                                selectedQuantity,
//                                date,
//                                (Position) positionSpinner.getSelectedItem()));
//                    else positionSelection = i;
                    if (i != -1) save();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            articleSpinner.setAdapter(articleAdapter);
            articleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != -1) save();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            modelLiveDataPagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                modelAdapter.clear();
                modelAdapter.addAll(data.snapshot());
                modelAdapter.notifyDataSetChanged();
            });
            modelSpinner.setAdapter(modelAdapter);
            modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != -1) {
                        if (articleLivePagedList != null)
                            articleLivePagedList.removeObservers(fragment.getViewLifecycleOwner());
                        articleLivePagedList = operationsViewModel.getArticlesByModelId(modelAdapter.getItem(i).getId());
                        articleLivePagedList.observe(fragment.getViewLifecycleOwner(), data -> {
                            articleAdapter.clear();
                            articleAdapter.addAll(data.snapshot());
                            articleAdapter.notifyDataSetChanged();
                            articleSpinner.setSelection(articleAdapter.getPosition(article));
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            quantityEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    save();
                    return true;
                }
                return false;
            });

            deleteButton.setOnClickListener(v ->
                    operationsViewModel.deleteOperationById(operation.getId())
            );
        }
    }
}
