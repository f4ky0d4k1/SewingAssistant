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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.ui.DeleteDialogFragment;

public class PositionAdapter extends PagedListAdapter<Position, PositionAdapter.ViewHolder> {

    private final PositionsViewModel positionsViewModel;
    Fragment fragment;

    protected PositionAdapter(@NonNull DiffUtil.ItemCallback<Position> diffCallback, Fragment fragment) {
        super(diffCallback);
        positionsViewModel = new ViewModelProvider(fragment).get(PositionsViewModel.class);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_positions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(Objects.requireNonNull(getItem(position)));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameEdit, costEdit;
        private Position position;

        ViewHolder(View view) {
            super(view);

            nameEdit = view.findViewById(R.id.text_position_name);
            costEdit = view.findViewById(R.id.text_position_cost);
            Button button = view.findViewById(R.id.button_position_delete);
            ((TextView) view.findViewById(R.id.currency_cost)).setText(Currency.getInstance(Locale.getDefault()).getSymbol());

            nameEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    String name = nameEdit.getText().toString();
                    if (!name.equals(position.getName())) {
                        positionsViewModel.savePosition(new Position(position.getId(), name, parseCost()));
                        nameEdit.setText(position.getName());
                    }
                }
                return false;
            });

            costEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    String name = nameEdit.getText().toString();
                    double cost = parseCost();
                    if (cost != position.getCost()) {
                        positionsViewModel.savePosition(new Position(position.getId(), name, cost));
                        costEdit.setText(String.valueOf(position.getCost()));
                    }
                }
                return false;
            });

            button.setOnClickListener(v -> new DeleteDialogFragment(
                    (dialogInterface, i) -> positionsViewModel.deletePositionById(position.getId()),
                    (dialogInterface, i) -> dialogInterface.cancel())
                    .show(fragment.getParentFragmentManager(), "deleteDialog"));
        }

        public void bind(Position position) {
            this.position = position;
            nameEdit.setText(position.getName());
            costEdit.setText(String.valueOf(position.getCost()));
        }

        private double parseCost() {
            try {
                return Double.parseDouble(costEdit.getText().toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
