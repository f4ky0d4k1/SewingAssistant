package ru.dharatyan.sewingassistant.model.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import ru.dharatyan.sewingassistant.model.entity.Model;

@Dao
public interface ModelDao {

    @Query("SELECT * FROM model ORDER BY name")
    DataSource.Factory<Integer, Model> getAll();

    @Query("SELECT * FROM model WHERE id = :id ORDER BY name")
    Model getById(long id);

    @Insert
    void insert(Model model);

    @Transaction
    @Update
    void update(Model model);

    @Transaction
    @Query("DELETE FROM model WHERE id = :id")
    void deleteById(Long id);
}
