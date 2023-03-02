package com.elorrieta.idleoxygenvending.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;


@Entity
public class Usuario {
    @PrimaryKey @NonNull
    int id;

    float OxygenQuantity;

    Date last_conn_time;

    int prestige_lvl;

    int prestige_points;

    public Usuario(int id, float oxygenQuantity, Date last_conn_time, int prestige_lvl, int prestige_points) {
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

    public float getOxygenQuantity() {
        return OxygenQuantity;
    }

    public void setOxygenQuantity(float oxygenQuantity) {
        OxygenQuantity = oxygenQuantity;
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

