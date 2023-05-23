package com.elorrieta.idleoxygenvending.Entities;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.elorrieta.idleoxygenvending.Database.AppDatabase;
import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

@Entity
public class Mejora {

    @PrimaryKey
    @NonNull
    private int id;

    private String nombre;

    private String descripcion;

    private int o2ps;

    private int baseprice;


    //Constructores
    @Ignore
    public Mejora() {
    }

    public Mejora(int id, String nombre, String descripcion, int o2ps, int baseprice) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.o2ps = o2ps;
        this.baseprice = baseprice;
    }

    @Ignore
    public Mejora(QueryDocumentSnapshot document, Context context) {
        this.id = Integer.parseInt(document.get(context.getString(R.string.firebase_id_mejora)).toString());
        this.nombre = document.getString(context.getString(R.string.firebase_nombre_mejora));
        this.descripcion = document.getString(context.getString(R.string.firebase_descripcion_mejora));
        this.o2ps = Integer.parseInt(document.get(context.getString(R.string.firebase_o2ps_mejora)).toString());
        this.baseprice = Integer.parseInt(document.get(context.getString(R.string.firebase_baseprice_mejora)).toString());

    }
    //getters y setters

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

    public int getO2ps() {
        return o2ps;
    }

    public void setO2ps(int o2ps) {
        this.o2ps = o2ps;
    }
    public int getBaseprice() {
        return baseprice;
    }

    public void setBaseprice(int baseprice) {
        this.baseprice = baseprice;
    }

    @Ignore
    public int getO2psTotal(int index) {
        int o2ps = 0;
        if (MainActivity.mejorasDelUsuario != null) {
            o2ps = MainActivity.mejorasDelUsuario.get(index).getUpgrade_Amount() * getO2ps();
        }
        return o2ps;
    }

    //funciones para mostrar los datos formateados

    @Ignore
    public String showO2psTotal(int index) {
        String o2ps;
        if (MainActivity.mejoras != null) {
            o2ps = (float) getO2psTotal(index) / 1000 + " O2/s";
        } else {
            return "Desconocido";
        }
        return o2ps;
    }

    @Ignore
    public String showPrice(int index) {
        String price;
        if (MainActivity.mejoras != null) {
            price = "precio: " + (float) getPrice(index) / 1000 + " O2";
        } else {
            return "Desconocido";
        }
        return price;
    }

    @Ignore
    public String showO2ps() {
        String o2ps = "";
        if (MainActivity.mejoras != null) {
            o2ps = "+ " + (float) getO2ps() / 1000 + " O2/s";
        }
        return o2ps;
    }

    //Funcion que optiene el precio de la mejora en funcion del nivel de la misma
    //y lo devuelve como int
    @Ignore
    public int getPrice(int index) {
        int result = -1;
        if (MainActivity.mejorasDelUsuario != null) {
            result = 0;
            if (MainActivity.mejorasDelUsuario.get(index).getUpgrade_Amount() == 0) {
                result = baseprice;
            } else {
                for (int i = 0; i < MainActivity.mejorasDelUsuario.get(index).getUpgrade_Amount(); i++) {
                    if (MainActivity.mejorasDelUsuario.get(index).getUpgrade_Amount() % 5 == 0) {
                        result = (int) ((result + (baseprice * 1.35)));
                    } else {
                        result = (int) ((result + (baseprice * 1.25)));
                    }

                }
            }
        }
        return result;
    }

    //Carga los datos de las mejoras guardado en room en una lista en el main
    @Ignore
    public static void cargarDatos(Context context) {
        AppDatabase room = AppDatabase.getDatabase(context);
        List<Mejora> mejoras = room.mejoraDao().getAll();
        MainActivity.mejoras = mejoras;
    }
}
