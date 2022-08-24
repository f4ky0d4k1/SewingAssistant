package ru.dharatyan.sewingassistant.util.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import ru.dharatyan.sewingassistant.model.entity.Article;

public class ArticleDiffUtilCallback extends DiffUtil.ItemCallback<Article> {

    @Override
    public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
        return Objects.equals(oldItem.getId(),newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
