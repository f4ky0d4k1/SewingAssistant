package ru.dharatyan.sewingassistant.ui.report;

import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.ui.DeleteDialogFragment;
import ru.dharatyan.sewingassistant.ui.positions.PositionsViewModel;

public class ReportsAdapter extends ListAdapter<File, ReportsAdapter.ViewHolder> {

    Fragment fragment;
    List<File> files;

    protected ReportsAdapter(@NonNull DiffUtil.ItemCallback<File> diffCallback, Fragment fragment) {
        super(diffCallback);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(Objects.requireNonNull(getItem(position)));
    }

    @Override
    public void submitList(@Nullable List<File> list) {
        files = list;
        super.submitList(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private File file;

        ViewHolder(View view) {
            super(view);

            textName = view.findViewById(R.id.text_report_name);
            Button button = view.findViewById(R.id.button_report_delete);

            textName.setOnClickListener(v -> open());

            button.setOnClickListener(v -> new DeleteDialogFragment(
                    (dialogInterface, i) -> delete(),
                    (dialogInterface, i) -> dialogInterface.cancel())
                    .show(fragment.getParentFragmentManager(), "deleteDialog"));
        }

        private void open() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkURI = FileProvider.getUriForFile(
                    fragment.requireContext(),
                    fragment.requireContext().getApplicationContext()
                            .getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/vnd.ms-excel");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fragment.startActivity(intent);
        }

        private void delete() {
            files.remove(file);
            file.delete();
            submitList(files);
            notifyDataSetChanged();
        }

        public void bind(File file) {
            this.file = file;
            textName.setText(file.getName());
        }
    }
}
