package com.elorrieta.idleoxygenvending.Entities;



import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.elorrieta.idleoxygenvending.Database.AppDatabase;
import com.elorrieta.idleoxygenvending.MainActivity;
import java.util.List;

@Entity
public class Mejora {

    @PrimaryKey @NonNull
    int id;

    String nombre;

    String descripcion;

    float O2ps;

    int baseprice;

    public Mejora() {
    }

    public Mejora(int id, String nombre, String descripcion, float o2ps, int baseprice) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        O2ps = o2ps;
        this.baseprice = baseprice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getO2ps() {
        return O2ps;
    }

    public void setO2ps(float o2ps) {
        O2ps = o2ps;
    }

    public int getBaseprice() {
        return baseprice;
    }

    public void setBaseprice(int baseprice) {
        this.baseprice = baseprice;
    }

    public static void cargarDatos(Context context){
        AppDatabase room = AppDatabase.getDatabase(context);


        List<Mejora> mejoras = room.mejoraDao().getAll();
        MainActivity.mejoras = mejoras;
    }
}
