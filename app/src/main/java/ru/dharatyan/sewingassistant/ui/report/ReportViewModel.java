package ru.dharatyan.sewingassistant.ui.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

import ru.dharatyan.sewingassistant.AppDatabase;
import ru.dharatyan.sewingassistant.model.dao.ArticleDao;
import ru.dharatyan.sewingassistant.model.dao.ModelDao;
import ru.dharatyan.sewingassistant.model.dao.OperationDao;
import ru.dharatyan.sewingassistant.model.dao.PositionDao;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;

public class ReportViewModel extends AndroidViewModel {

    PositionDao positionDao;
    OperationDao operationDao;
    ArticleDao articleDao;
    ModelDao modelDao;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        positionDao = AppDatabase.getInstance(application).positionDao();
        operationDao = AppDatabase.getInstance(application).operationDao();
        articleDao = AppDatabase.getInstance(application).articleDao();
        modelDao = AppDatabase.getInstance(application).modelDao();
    }

    public List<Operation> getOperationsByDates(Date startDate, Date endDate) {
        return operationDao.getBetweenDates(startDate, endDate);
    }

    public List<Position> getAllPositions() {
        return positionDao.getAll();
    }

    public List<Article> getArticlesByIds(List<Long> ids) {
        return ids.stream().map(id -> articleDao.getById(id)).collect(Collectors.toList());
    }

    public List<Model> getModelsByIds(List<Long> ids) {
        return ids.stream().map(id -> modelDao.getById(id)).collect(Collectors.toList());
    }

}