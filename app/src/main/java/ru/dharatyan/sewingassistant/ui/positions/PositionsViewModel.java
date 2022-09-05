package ru.dharatyan.sewingassistant.ui.positions;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import ru.dharatyan.sewingassistant.AppDatabase;
import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.dao.PositionDao;
import ru.dharatyan.sewingassistant.model.entity.Position;

public class PositionsViewModel extends AndroidViewModel {

    private final PositionDao positionDao;

    public PositionsViewModel(@NonNull Application application) {
        super(application);
        positionDao = AppDatabase.getInstance(application).positionDao();
    }

    public LiveData<PagedList<Position>> getAllPositions() {
        return new LivePagedListBuilder<>(
                positionDao.getFactoryAll(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(10)
                        .build())
                .build();
    }

    public void savePosition(Position position) {
        if (position.getName().length() <= 0)
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_create_position_empty_name, Toast.LENGTH_LONG).show();
        else if (position.getCost() <= 0)
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_create_position_empty_cost, Toast.LENGTH_LONG).show();
        else try {
            if (position.getId() == null)
                positionDao.insert(position);
            else positionDao.update(position);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_unique_position, Toast.LENGTH_LONG).show();
        }
    }

    public void deletePositionById(Long positionId) {
        try {
            positionDao.deleteById(positionId);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_delete_position, Toast.LENGTH_LONG).show();
        }

    }
}