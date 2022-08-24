package ru.dharatyan.sewingassistant.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "position", indices = @Index(value = {"name", "cost"}, name = "idx_unq_name_cost", unique = true))
public class Position {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private double cost;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s (%s)", name, NumberFormat.getCurrencyInstance(Locale.getDefault()).format(cost));
    }
}
