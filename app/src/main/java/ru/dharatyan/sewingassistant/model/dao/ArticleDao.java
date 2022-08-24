package ru.dharatyan.sewingassistant.model.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import ru.dharatyan.sewingassistant.model.entity.Article;

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM article ORDER BY name")
    DataSource.Factory<Integer, Article> getAllFactory();

    @Query("SELECT * FROM article WHERE model_id = :modelId ORDER BY name")
    DataSource.Factory<Integer, Article> getByModelIdFactory(long modelId);

    @Query("SELECT * FROM article WHERE id = :id ORDER BY name")
    Article getById(long id);

    @Insert
    void insert(Article article);

    @Transaction
    @Update
    void update(Article article);

    @Transaction
    @Query("DELETE FROM article WHERE id = :id")
    void deleteById(Long id);
}
