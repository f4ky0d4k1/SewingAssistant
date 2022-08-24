package ru.dharatyan.sewingassistant.util.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;

public class DateDiffUtilCallback extends DiffUtil.ItemCallback<Date> {

    @Override
    public boolean areItemsTheSame(@NonNull Date oldItem, @NonNull Date newItem) {
        return Objects.equals(oldItem, newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull Date oldItem, @NonNull Date newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
