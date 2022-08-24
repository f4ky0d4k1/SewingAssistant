package ru.dharatyan.sewingassistant.model.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
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
@Entity(tableName = "article",
        indices = @Index(value = {"name", "model_id"}, name = "idx_unq_name_model_id", unique = true),
        foreignKeys = {@ForeignKey(entity = Model.class, parentColumns = "id", childColumns = "model_id", onDelete = ForeignKey.CASCADE, onUpdate = CASCADE)})
public class Article {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    private Long id;
    @ColumnInfo(name = "name", index = true)
    private String name;
    @ColumnInfo(name = "model_id", index = true)
    private Long modelId;

    @Override
    public String toString() {
        return name;
    }
}
