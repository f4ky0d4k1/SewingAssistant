package ru.dharatyan.sewingassistant.ui.positions;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Currency;
import java.util.Locale;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Position;

public class PositionAdapter extends PagedListAdapter<Position, PositionAdapter.ViewHolder> {

    private final PositionsViewModel positionsViewModel;

    protected PositionAdapter(@NonNull DiffUtil.ItemCallback<Position> diffCallback, Fragment fragment) {
        super(diffCallback);
        positionsViewModel = new ViewModelProvider(fragment).get(PositionsViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_positions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView nameEdit, costEdit;
        final Button button;
        Long positionId;
        String oldName;
        double oldCost;

        public void bind(Position position) {
            String name = position.getName();
            nameEdit.setText(name);
            oldName = name;
            double cost = position.getCost();
            costEdit.setText(String.valueOf(cost));
            oldCost = cost;
            positionId = position.getId();

            if (positionId != null) {
                button.setVisibility(View.VISIBLE);
                button.setClickable(true);
            } else {
                button.setVisibility(View.INVISIBLE);
                button.setClickable(false);
            }
        }

        ViewHolder(View view) {
            super(view);

            nameEdit = view.findViewById(R.id.text_position_name);
            costEdit = view.findViewById(R.id.text_position_cost);
            button = view.findViewById(R.id.button_position_delete);
            ((TextView) view.findViewById(R.id.currency_cost)).setText(Currency.getInstance(Locale.getDefault()).getSymbol());

            nameEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER &&
                        nameEdit.length() > 0 &&
                        costEdit.length() > 0) {
                    String name = nameEdit.getText().toString();
                    double cost = Double.parseDouble(costEdit.getText().toString());
                    if (!name.equals(oldName) && cost > 0) {
                        positionsViewModel.savePosition(new Position(positionId, name, cost));
                        nameEdit.setText(oldName);
                    }
                    return true;
                }
                return false;
            });

            costEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER &&
                        nameEdit.length() > 0 &&
                        costEdit.length() > 0) {
                    String name = nameEdit.getText().toString();
                    double cost = Double.parseDouble(costEdit.getText().toString());
                    if (cost != oldCost && cost > 0) {
                        positionsViewModel.savePosition(new Position(positionId, name, cost));
                        costEdit.setText(String.valueOf(oldCost));
                    }
                    return true;
                }
                return false;
            });

            button.setOnClickListener(v -> positionsViewModel.deletePositionById(positionId));
        }
    }
}
