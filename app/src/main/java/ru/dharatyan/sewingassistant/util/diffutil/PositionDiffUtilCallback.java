package ru.dharatyan.sewingassistant.util.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Position;

public class PositionDiffUtilCallback extends DiffUtil.ItemCallback<Position> {

    @Override
    public boolean areItemsTheSame(@NonNull Position oldItem, @NonNull Position newItem) {
        return Objects.equals(oldItem.getId(),newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Position oldItem, @NonNull Position newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
