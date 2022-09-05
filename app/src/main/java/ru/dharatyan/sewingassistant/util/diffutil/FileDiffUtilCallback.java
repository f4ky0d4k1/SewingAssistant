package ru.dharatyan.sewingassistant.util.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.io.File;
import java.util.Objects;

import ru.dharatyan.sewingassistant.model.entity.Position;

public class FileDiffUtilCallback extends DiffUtil.ItemCallback<File> {

    @Override
    public boolean areItemsTheSame(@NonNull File oldItem, @NonNull File newItem) {
        return Objects.equals(oldItem.getAbsolutePath(), newItem.getAbsolutePath());
    }

    @Override
    public boolean areContentsTheSame(@NonNull File oldItem, @NonNull File newItem) {
        return Objects.equals(oldItem.getAbsolutePath(), newItem.getAbsolutePath());
    }
}
