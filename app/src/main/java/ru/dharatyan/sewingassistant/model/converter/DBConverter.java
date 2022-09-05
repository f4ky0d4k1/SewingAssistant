package ru.dharatyan.sewingassistant.model.converter;

import androidx.room.TypeConverter;

import ru.dharatyan.sewingassistant.model.entity.Date;

public class DBConverter {

    @TypeConverter
    public static Date toDate(String date) {
        return Date.parse(date);
    }

    @TypeConverter
    public static String fromDate(Date date){
        return date == null ? null : date.toString();
    }
}
