package ru.dharatyan.sewingassistant.ui.positions;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.dharatyan.sewingassistant.AppDatabase;
import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.dao.PositionDao;
import ru.dharatyan.sewingassistant.model.entity.Position;

public class PositionsViewModel extends AndroidViewModel {

    private final PositionDao positionDao;
    private final Application application;

    public PositionsViewModel(@NonNull Application application) {
        super(application);
        positionDao = AppDatabase.getInstance(application).positionDao();
        this.application = application;
    }

    public LiveData<PagedList<Position>> getAllPositions() {
        return new LivePagedListBuilder<>(
                positionDao.getAll(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(10)
                        .build())
                .build();
    }

    public void savePosition(Position position) {
        try {
            if (position.getId() == null)
                positionDao.insert(position);
            else positionDao.update(position);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(application.getApplicationContext(), R.string.toast_constraint_unique_position, Toast.LENGTH_LONG).show();
        }
    }

    public void deletePositionById(Long positionId) {
        try {
            positionDao.deleteById(positionId);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(application.getApplicationContext(), R.string.toast_constraint_delete_position, Toast.LENGTH_LONG).show();
        }

    }
}