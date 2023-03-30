package com.elorrieta.idleoxygenvending.Entities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.Database.Firebase;
import com.elorrieta.idleoxygenvending.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;


@Entity
public class Usuario {
    @PrimaryKey @NonNull
    int id;

    int oxygenQuantity;

    String email;


    Date last_conn_time;


    int prestige_lvl;

    int prestige_points;



    public Usuario(int id, int oxygenQuantity, String email, Date last_conn_time, int prestige_lvl, int prestige_points) {
        this.id = id;
        this.email=email;
        this.oxygenQuantity = oxygenQuantity;
        this.last_conn_time = last_conn_time;
        this.prestige_lvl = prestige_lvl;
        this.prestige_points = prestige_points;

    }

    public Usuario() {

    }

    public Usuario(QueryDocumentSnapshot document) {
        this.id = Integer.parseInt(document.getId());
        this.email= document.getString("email");
        this.oxygenQuantity = Long.bitCount((Long) document.get("oxygenQuantity"));
        this.last_conn_time = document.getDate("last_conn_time");
        this.prestige_lvl = Long.bitCount((Long) (document.get("prestige_lvl")));
        this.prestige_points =  Long.bitCount((Long) document.get("prestige_points"));
   }

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
        if(MainActivity.user!=null){
            this.oxygenQuantity += oxygenQuantity;
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

    //Crea una cuenta nueva en caso de que no exista ninguna ya con ese correo
    //Si el correo es "" se crea una nueva cuenta
    //Si el correo existe se le cargaran los datos
    public void createAccount(Context context) {
        int id,index;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Usuario usuario = null;

        id = Firebase.userExists(currentUser.getEmail(),context);
        //no existe hay que crearloA

        if(id==0){
            id = Firebase.getLastId(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                index = id+1;
                usuario = new Usuario(index,0,currentUser.getEmail(),Date.from(Instant.now()),0,0);
                Firebase.createUsusario(usuario,context);
            }
        }else{
            usuario = Firebase.cargarUsuario(id,context);
        }
        MainActivity.user = usuario;

    }
}

