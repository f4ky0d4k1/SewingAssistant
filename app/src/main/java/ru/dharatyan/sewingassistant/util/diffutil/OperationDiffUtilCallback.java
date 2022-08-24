package ru.dharatyan.sewingassistant.util.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import ru.dharatyan.sewingassistant.model.entity.Operation;

public class OperationDiffUtilCallback extends DiffUtil.ItemCallback<Operation> {

    @Override
    public boolean areItemsTheSame(@NonNull Operation oldItem, @NonNull Operation newItem) {
        return Objects.equals(oldItem.getId(),newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Operation oldItem, @NonNull Operation newItem) {
        return Objects.equals(oldItem, newItem);
    }

//    private final List<OperationWithPositionAndArticle> oldList;
//    private final List<OperationWithPositionAndArticle> newList;
//
//    public OperationDiffUtilCallback(List<OperationWithPositionAndArticle> oldList, List<OperationWithPositionAndArticle> newList) {
//        this.oldList = oldList;
//        this.newList = newList;
//    }
//
//    @Override
//    public int getOldListSize() {
//        return oldList.size();
//    }
//
//    @Override
//    public int getNewListSize() {
//        return newList.size();
//    }

//    @Override
//    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//        OperationWithPositionAndArticle oldOperation = oldList.get(oldItemPosition);
//        OperationWithPositionAndArticle newOperation = newList.get(newItemPosition);
//        return Objects.equals(oldOperation.getOperation().getId(),newOperation.getOperation().getId());
//    }
//
//    @Override
//    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//        OperationWithPositionAndArticle oldOperation = oldList.get(oldItemPosition);
//        OperationWithPositionAndArticle newOperation = newList.get(newItemPosition);
//        return Objects.equals(oldOperation, newOperation);
//    }
}
