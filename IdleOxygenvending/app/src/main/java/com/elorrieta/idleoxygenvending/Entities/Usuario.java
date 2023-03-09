package com.elorrieta.idleoxygenvending.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.elorrieta.idleoxygenvending.MainActivity;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.sql.Date;


@Entity
public class Usuario {
    @PrimaryKey @NonNull
    int id;

    int OxygenQuantity;

    Date last_conn_time;

    int prestige_lvl;

    int prestige_points;

    public Usuario(int id, int oxygenQuantity, Date last_conn_time, int prestige_lvl, int prestige_points) {
        this.id = id;
        OxygenQuantity = oxygenQuantity;
        this.last_conn_time = last_conn_time;
        this.prestige_lvl = prestige_lvl;
        this.prestige_points = prestige_points;
    }

    public Usuario() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOxygenQuantity() {
        return OxygenQuantity;
    }

    public void setOxygenQuantity(int oxygenQuantity) {
        if(MainActivity.user!=null){
            OxygenQuantity = MainActivity.user.OxygenQuantity + oxygenQuantity;
        }

    }

    public String showOxygenQuantity(){
        String oxygenQuantity;
        if(MainActivity.user!=null){
            oxygenQuantity ="O2: " + (float)MainActivity.user.getOxygenQuantity()/1000;
        }else {
            return "Desconocido";
        }
        return oxygenQuantity;
    }

    public Date getLast_conn_time() {
        return last_conn_time;
    }

    public void setLast_conn_time(Date last_conn_time) {
        this.last_conn_time = last_conn_time;
    }

    public int getPrestige_lvl() {
        return prestige_lvl;
    }

    public void setPrestige_lvl(int prestige_lvl) {
        this.prestige_lvl = prestige_lvl;
    }

    public int getPrestige_points() {
        return prestige_points;
    }

    public void setPrestige_points(int prestige_points) {
        this.prestige_points = prestige_points;
    }
}

