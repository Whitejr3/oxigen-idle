package com.elorrieta.idleoxygenvending.Entities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.elorrieta.idleoxygenvending.Database.Firebase;
import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;


@Entity
public class Usuario {
    @PrimaryKey
    @NonNull
    int id;

    int oxygenQuantity;

    String email;


    Date last_conn_time;


    int prestige_lvl;

    int prestige_points;


    //Constructores
    public Usuario(int id, int oxygenQuantity, String email, Date last_conn_time, int prestige_lvl, int prestige_points) {
        this.id = id;
        this.email = email;
        this.oxygenQuantity = oxygenQuantity;
        this.last_conn_time = last_conn_time;
        this.prestige_lvl = prestige_lvl;
        this.prestige_points = prestige_points;

    }
    @Ignore
    public Usuario() {
        this.id = -1;
    }

    @Ignore
    public Usuario(QueryDocumentSnapshot document, Context context) {
        this.id = Integer.parseInt(document.get(context.getString(R.string.firebase_id_usuario)).toString());
        this.email = document.getString(context.getString(R.string.firebase_email_usuario));
        this.oxygenQuantity = Integer.parseInt(document.get(context.getString(R.string.firebase_oxygenQuantity_usuario)).toString());
        this.last_conn_time = document.getDate(context.getString(R.string.firebase_last_conn_time_usuario));
        this.prestige_lvl = Integer.parseInt(document.get(context.getString(R.string.firebase_prestige_lvl_usuario)).toString());
        this.prestige_points = Integer.parseInt(document.get(context.getString(R.string.firebase_prestige_points_usuario)).toString());
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOxygenQuantity() {
        return oxygenQuantity;
    }

    public void setOxygenQuantity(int oxygenQuantity) {
        if (MainActivity.user != null) {
            this.oxygenQuantity += oxygenQuantity;
        }

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    //Muestra en 02 que tiene el usuario con un formato
    public String showOxygenQuantity() {
        String oxygenQuantity;
        if (MainActivity.user != null) {
            oxygenQuantity = "O2: " + (float) MainActivity.user.getOxygenQuantity() / 1000;
        } else {
            return "Desconocido";
        }
        return oxygenQuantity;
    }

    //Crea una cuenta nueva en caso de que no exista ninguna ya con ese correo
    //Si el correo es "" se crea una nueva cuenta
    //Si el correo existe se le cargaran los datos
    public void loadAccount(Context context) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Thread thread = new Thread() {
            @Override
            public void run() {
                Firebase.userExists(currentUser.getEmail(), context);

            }
        };
        thread.start();
        try {
            thread.join();
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    //Calcula el 02 que generan todas las mejoras y lo devuelve en forma de int
    public int getO2ps(){
        int result =-1;
        if(MainActivity.mejorasDelUsuario!=null){
            result=0;
            for (int i = 0; i < MainActivity.mejorasDelUsuario.size(); i++) {
                if(MainActivity.mejorasDelUsuario.get(i).getUpgrade_Amount()>0){
                    result+=(MainActivity.mejorasDelUsuario.get(i).getUpgrade_Amount()*MainActivity.mejoras.get(i).getO2ps());
                }
            }
        }
        return result;
    }
}

