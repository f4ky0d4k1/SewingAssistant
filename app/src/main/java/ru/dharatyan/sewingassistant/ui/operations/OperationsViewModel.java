package ru.dharatyan.sewingassistant.ui.operations;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

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

public class OperationsViewModel extends AndroidViewModel {

//    private OperationWithPositionAndArticleDao operationWithPositionAndArticleDao;
    private OperationDao operationDao;
    private PositionDao positionDao;
    private ArticleDao articleDao;
    private ModelDao modelDao;

    public OperationsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
//        operationWithPositionAndArticleDao = db.operationWithPositionAndArticleDao();
        operationDao = db.operationDao();
        positionDao = db.positionDao();
        articleDao = db.articleDao();
        modelDao = db.modelDao();
    }

    public LiveData<PagedList<Position>> getAllPositions() {
        return new LivePagedListBuilder<>(
                positionDao.getAll(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
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

    public Position getPositionById(Long positionId) {
        return positionDao.getById(positionId);
    }

    public Article getArticleById(Long articleId) {
        return articleDao.getById(articleId);
    }

    public Model getModelById(Long modelId) {
        return modelDao.getById(modelId);
    }

    public Double getTotalByDate(Date date) {
        Double response = operationDao.getTotalByDate(date);
        return response;
    }

    public void deleteOperationsByDate(Date date) {
        operationDao.deleteByDate(date);
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

    public LiveData<PagedList<Operation>> getOperationsByDate(Date date) {
        return new LivePagedListBuilder<>(
                operationDao.getByDate(date),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(20)
                        .build())
                .build();
    }

    public void saveOperation(Operation operation) {
        AppDatabase.getInstance(this.getApplication()).runInTransaction(() -> {
            Operation sameOperation = operationDao.getByPositionAndArticleAndDate(operation.getPositionId(), operation.getArticleId(), operation.getDate());
            if (sameOperation != null) {
                sameOperation.setQuantity(sameOperation.getQuantity() + operation.getQuantity());
            if (operation.getId() != null)
                operationDao.deleteById(operation.getId());
            operationDao.update(sameOperation);
            } else if (operation.getId() == null) operationDao.insert(operation);
            else operationDao.update(operation);
        });
    }

    public void deleteOperationById(Long operationId) {
        operationDao.deleteById(operationId);
    }
}