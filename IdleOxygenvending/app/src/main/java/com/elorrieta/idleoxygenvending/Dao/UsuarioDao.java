package com.elorrieta.idleoxygenvending.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.elorrieta.idleoxygenvending.Entities.Usuario;


import java.util.List;

@Dao
public interface UsuarioDao {

    @Insert
    void insertAll(Usuario... usuarios);

    @Delete
    void delete(Usuario usuario);


    @Query("SELECT * FROM Usuario")
    List<Usuario> getAll();

    @Query("Update Usuario set OxygenQuantity = :OxygenQuantity,last_conn_time = :last_conn_time,prestige_lvl = :prestige_lvl,prestige_points = :prestige_points WHERE id = :id")
    void update(int id, int OxygenQuantity, String last_conn_time,int prestige_lvl,int prestige_points);
}

