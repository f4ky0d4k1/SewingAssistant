package ru.dharatyan.sewingassistant.util.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import ru.dharatyan.sewingassistant.model.entity.Model;

public class ModelDiffUtilCallback extends DiffUtil.ItemCallback<Model> {

    @Override
    public boolean areItemsTheSame(@NonNull Model oldItem, @NonNull Model newItem) {
        return Objects.equals(oldItem.getId(),newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Model oldItem, @NonNull Model newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
