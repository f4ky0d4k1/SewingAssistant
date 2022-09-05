package ru.dharatyan.sewingassistant.model.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Map;

import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;

@Dao
public interface OperationDao {

    @Query("SELECT * FROM operation ORDER BY date")
    LiveData<Operation> getAll();

    @Query("SELECT * FROM operation WHERE id = :id ORDER BY date")
    LiveData<Operation> getById(long id);

    @Query("SELECT * FROM operation WHERE position_id = :position_id AND article_id = :article_id AND date = :date LIMIT 1")
    Operation getByPositionAndArticleAndDate(long position_id, long article_id, Date date);

    @Query("SELECT o.* FROM operation o LEFT JOIN article a ON o.article_id = a.id WHERE date BETWEEN :startDate AND :endDate ORDER BY o.date, a.model_id, o.article_id, o.position_id")
    List<Operation> getBetweenDates(Date startDate, Date endDate);

    @Query("SELECT o.* FROM operation o LEFT JOIN article a ON o.article_id = a.id WHERE date = :date ORDER BY o.date, a.model_id, o.article_id, o.position_id")
    DataSource.Factory<Integer, Operation> getFactoryByDate(Date date);


    @Query("SELECT total(o.quantity * p.cost) FROM operation o LEFT JOIN position p ON o.position_id = p.id WHERE date = :date")
    Double getTotalByDate(Date date);

    @Query("SELECT DISTINCT date FROM operation")
    DataSource.Factory<Integer, Date> getDates();

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insert(Operation operation);

    @Update
    void update(Operation operation);

    @Query("DELETE FROM operation WHERE id = :id")
    void deleteById(Long id);

    @Query("DELETE FROM operation WHERE date = :date")
    void deleteByDate(Date date);
}
