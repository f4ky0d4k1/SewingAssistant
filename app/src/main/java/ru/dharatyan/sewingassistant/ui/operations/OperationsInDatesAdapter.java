package ru.dharatyan.sewingassistant.ui.operations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

public class OperationsInDatesAdapter extends PagedListAdapter<Operation, OperationsInDatesAdapter.ViewHolder> {

    private final Fragment fragment;
    private final OperationsViewModel operationsViewModel;

    protected OperationsInDatesAdapter(@NonNull DiffUtil.ItemCallback<Operation> diffCallback, Fragment fragment) {
        super(diffCallback);
        operationsViewModel = new ViewModelProvider(fragment).get(OperationsViewModel.class);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public OperationsInDatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_operations_in_dates, parent, false);
        return new OperationsInDatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperationsInDatesAdapter.ViewHolder holder, int position) {
        Operation operation = getItem(position);
        Position positionObj = operationsViewModel.getPositionById(operation.getPositionId());
        Article article = operationsViewModel.getArticleById(operation.getArticleId());
        Model model = operationsViewModel.getModelById(article.getModelId());
        holder.bind(operation, positionObj, article, model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView positionText, articleText, quantityText, totalText;
        final View view;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            totalText = view.findViewById(R.id.text_date_operation_total);
            quantityText = view.findViewById(R.id.text_date_operation_quantity);
            positionText = view.findViewById(R.id.text_date_operation_position);
            articleText = view.findViewById(R.id.text_date_operation_article);
        }

        public void bind(Operation operation, Position position, Article article, Model model) {
            totalText.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(operation.getQuantity() * position.getCost()));
            positionText.setText(String.format("%s (%s)", position.getName(), NumberFormat.getCurrencyInstance(Locale.getDefault()).format(position.getCost())));
            articleText.setText(String.format("%s (%s)", article.getName(), model.getName()));
            quantityText.setText(String.format(Locale.getDefault(), "%d %s", operation.getQuantity(), fragment.requireContext().getText(R.string.pieces)));
            view.setBackgroundResource(operation.isEnabled() ? R.drawable.alert_light_frame : R.drawable.alert_dark_frame);
        }
    }
}
