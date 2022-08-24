package ru.dharatyan.sewingassistant;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.dharatyan.sewingassistant.model.dao.ArticleDao;
import ru.dharatyan.sewingassistant.model.dao.ModelDao;
import ru.dharatyan.sewingassistant.model.dao.OperationDao;
import ru.dharatyan.sewingassistant.model.dao.PositionDao;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.util.converters.DBConverter;

@Database(entities = {Position.class, Operation.class, Model.class, Article.class}, exportSchema = false, version = 1)
@TypeConverters({DBConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PositionDao positionDao();

    public abstract OperationDao operationDao();

    public abstract ModelDao modelDao();

    public abstract ArticleDao articleDao();

//    public abstract ArticleWithModelDao articleWithModelDao();
//
//    public  abstract OperationWithPositionAndArticleDao operationWithPositionAndArticleDao();

    private static AppDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context,
                        AppDatabase.class, "database")
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }


}