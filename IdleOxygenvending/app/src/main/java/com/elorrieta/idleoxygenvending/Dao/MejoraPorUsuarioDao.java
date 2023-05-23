package com.elorrieta.idleoxygenvending.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.elorrieta.idleoxygenvending.Entities.Mejora;
import com.elorrieta.idleoxygenvending.Entities.MejoraPorUser;

import java.util.ArrayList;
import java.util.List;
//Gestion de datos entre Room y la app
//insert, delete, update y select de las mejoras por usuario.
@Dao
public interface MejoraPorUsuarioDao {
    @Insert
    void insertAll(MejoraPorUser... mejoraPorUsers);

    @Delete
    void delete(MejoraPorUser mejoraPorUsers);


    @Query("SELECT * FROM MejoraPorUser")
    List<MejoraPorUser> getAll();

    @Query("Update MejoraPorUser set Upgrade_Amount = :amount WHERE IdUsuario = :idUser AND IdMejora = :idMejora")
    void update(int idUser, int idMejora, int amount);
}
