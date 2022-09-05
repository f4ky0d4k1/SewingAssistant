package ru.dharatyan.sewingassistant.model.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(
        tableName = "operation",
        indices = @Index(value = {"date", "position_id", "article_id"}, name = "idx_unq_date_position_article", unique = true),
        foreignKeys = {
                @ForeignKey(entity = Position.class, parentColumns = "id", childColumns = "position_id", onDelete = ForeignKey.RESTRICT, onUpdate = CASCADE),
                @ForeignKey(entity = Article.class, parentColumns = "id", childColumns = "article_id", onDelete = ForeignKey.RESTRICT, onUpdate = CASCADE)})
public class Operation {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    private int quantity;
    private boolean enabled;
    private Date date;
    @ColumnInfo(name = "position_id", index = true)
    private Long positionId;
    @ColumnInfo(name = "article_id", index = true)
    private Long articleId;
}
