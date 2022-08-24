package ru.dharatyan.sewingassistant.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "model", indices = @Index(value = {"name"}, name = "idx_unq_name", unique = true))
public class Model {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    private Long id;
    @ColumnInfo(name = "name", index = true)
    private String name;

    @Override
    public String toString() {
        return name;
    }
}