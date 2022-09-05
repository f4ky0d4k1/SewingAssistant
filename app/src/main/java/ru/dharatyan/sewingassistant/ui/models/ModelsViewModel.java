package ru.dharatyan.sewingassistant.ui.models;

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
import ru.dharatyan.sewingassistant.model.dao.ArticleDao;
import ru.dharatyan.sewingassistant.model.dao.ModelDao;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Model;

public class ModelsViewModel extends AndroidViewModel {

    private final ModelDao modelDao;
    private final ArticleDao articleDao;

    public ModelsViewModel(@NonNull Application application) {
        super(application);
        modelDao = AppDatabase.getInstance(application).modelDao();
        articleDao = AppDatabase.getInstance(application).articleDao();
    }

    public LiveData<PagedList<Model>> getAllModels() {
        return new LivePagedListBuilder<>(
                modelDao.getAll(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(10)
                        .build())
                .build();
    }

    public void saveModel(Model model) {
        if (model.getName().length() <= 0)
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_create_model_empty_name, Toast.LENGTH_LONG).show();
        else try {
            if (model.getId() == null)
                modelDao.insert(model);
            else modelDao.update(model);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_unique_model, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteModelById(Long modelId) {
        try {
            modelDao.deleteById(modelId);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_delete_model, Toast.LENGTH_LONG).show();
        }
    }

    public LiveData<PagedList<Article>> getArticlesByModelId(Long modelId) {
        return new LivePagedListBuilder<>(
                articleDao.getByModelIdFactory(modelId),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(10)
                        .build())
                .build();
    }

    public void saveArticle(Article article) {
        if (article.getName().length() <= 0)
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_create_article_empty_name, Toast.LENGTH_LONG).show();
        else try {
            if (article.getId() == null)
                articleDao.insert(article);
            else articleDao.update(article);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_unique_article, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteArticleById(Long articleId) {
        try {
            articleDao.deleteById(articleId);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getApplication().getApplicationContext(), R.string.toast_constraint_delete_article, Toast.LENGTH_LONG).show();
        }
    }
}