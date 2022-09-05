package ru.dharatyan.sewingassistant.model.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ru.dharatyan.sewingassistant.model.entity.Position;

@Dao
public interface PositionDao {

    @Query("SELECT * FROM position ORDER BY name")
    DataSource.Factory<Integer, Position> getFactoryAll();

    @Query("SELECT * FROM position ORDER BY name")
    List<Position> getAll();

    @Query("SELECT * FROM position WHERE id = :id ORDER BY name")
    Position getById(long id);

    @Insert
    void insert(Position position);

    @Transaction
    @Update
    void update(Position position);

    @Transaction
    @Query("DELETE FROM position WHERE id = :id")
    void deleteById(Long id);
}
