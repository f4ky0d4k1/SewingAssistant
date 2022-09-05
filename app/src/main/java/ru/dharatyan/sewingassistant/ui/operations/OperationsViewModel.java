package ru.dharatyan.sewingassistant.ui.operations;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.Objects;

import ru.dharatyan.sewingassistant.AppDatabase;
import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.dao.ArticleDao;
import ru.dharatyan.sewingassistant.model.dao.ModelDao;
import ru.dharatyan.sewingassistant.model.dao.OperationDao;
import ru.dharatyan.sewingassistant.model.dao.PositionDao;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;

public class OperationsViewModel extends AndroidViewModel {

    private final OperationDao operationDao;
    private final PositionDao positionDao;
    private final ArticleDao articleDao;
    private final ModelDao modelDao;

    public OperationsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        operationDao = db.operationDao();
        positionDao = db.positionDao();
        articleDao = db.articleDao();
        modelDao = db.modelDao();
    }

    public LiveData<PagedList<Operation>> getOperationsByDate(Date date) {
        return new LivePagedListBuilder<>(
                operationDao.getFactoryByDate(date),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public Double getTotalByDate(Date date) {
        return operationDao.getTotalByDate(date);
    }

    public LiveData<PagedList<Date>> getDates() {
        return new LivePagedListBuilder<>(
                operationDao.getDates(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public void saveOperation(Operation operation) {
        if (operation.getQuantity() <= 0)
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_create_operation_empty_cost, Toast.LENGTH_LONG).show();
        else {
            AppDatabase.getInstance(this.getApplication()).runInTransaction(() -> {
                Operation sameOperation = operationDao.getByPositionAndArticleAndDate(operation.getPositionId(), operation.getArticleId(), operation.getDate());
                if (sameOperation != null) {
                    if (Objects.equals(sameOperation.getId(), operation.getId()))
                        operationDao.update(operation);
                    else {
                        sameOperation.setQuantity(sameOperation.getQuantity() + operation.getQuantity());
                        operationDao.update(sameOperation);
                        if (operation.getId() != null) operationDao.deleteById(operation.getId());
                    }
                } else
                    operationDao.insert(operation);
            });
        }
    }

    public void deleteOperationById(Long operationId) {
        operationDao.deleteById(operationId);
    }

    public void deleteOperationsByDate(Date date) {
        operationDao.deleteByDate(date);
    }

    public LiveData<PagedList<Position>> getAllPositions() {
        return new LivePagedListBuilder<>(
                positionDao.getFactoryAll(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public Position getPositionById(Long positionId) {
        return positionDao.getById(positionId);
    }

    public LiveData<PagedList<Model>> getAllModels() {
        return new LivePagedListBuilder<>(
                modelDao.getAll(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public Model getModelById(Long modelId) {
        return modelDao.getById(modelId);
    }

    public LiveData<PagedList<Article>> getAllArticles() {
        return new LivePagedListBuilder<>(
                articleDao.getAllFactory(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public LiveData<PagedList<Article>> getArticlesByModelId(Long modelId) {
        return new LivePagedListBuilder<>(
                articleDao.getByModelIdFactory(modelId),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public Article getArticleById(Long articleId) {
        return articleDao.getById(articleId);
    }


}