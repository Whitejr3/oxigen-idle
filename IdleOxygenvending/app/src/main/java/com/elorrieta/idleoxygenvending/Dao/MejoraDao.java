package com.elorrieta.idleoxygenvending.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.elorrieta.idleoxygenvending.Entities.Mejora;

import java.util.Date;
import java.util.List;

@Dao
public interface MejoraDao {

    @Insert
    void insertAll(Mejora... mejoras);

    @Delete
    void delete(Mejora mejora);


    @Query("SELECT * FROM Mejora")
    List<Mejora> getAll();

    @Query("Update Mejora set nombre = :nombre,descripcion = :descripcion,O2ps = :O2ps,baseprice = :baseprice WHERE id = :id")
    void update(int id, String nombre, String descripcion, int O2ps, int baseprice);
}
