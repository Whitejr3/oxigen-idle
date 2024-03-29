package com.elorrieta.idleoxygenvending.Database;
import android.content.Context;


import androidx.room.*;
import androidx.room.RoomDatabase;

import com.elorrieta.idleoxygenvending.Dao.MejoraDao;
import com.elorrieta.idleoxygenvending.Dao.MejoraPorUsuarioDao;
import com.elorrieta.idleoxygenvending.Dao.UsuarioDao;
import com.elorrieta.idleoxygenvending.Entities.Mejora;
import com.elorrieta.idleoxygenvending.Entities.MejoraPorUser;
import com.elorrieta.idleoxygenvending.Entities.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Usuario.class, Mejora.class, MejoraPorUser.class}, version = 3)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {


    public abstract UsuarioDao usuarioDao();
    public abstract MejoraDao mejoraDao();
    public abstract MejoraPorUsuarioDao mejoraPorUsuarioDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //Instancia de la base de datos Room, si no existe la crea
    //Si existe recupera los datos
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "IdleOxygenDatabase")
                            .createFromAsset("database/IdleOxygen.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .enableMultiInstanceInvalidation()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}