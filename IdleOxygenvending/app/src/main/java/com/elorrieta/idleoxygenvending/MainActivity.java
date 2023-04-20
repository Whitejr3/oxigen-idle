package com.elorrieta.idleoxygenvending;

import static android.content.ContentValues.TAG;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.elorrieta.idleoxygenvending.Database.AppDatabase;
import com.elorrieta.idleoxygenvending.Database.Firebase;
import com.elorrieta.idleoxygenvending.Entities.Usuario;
import com.elorrieta.idleoxygenvending.databinding.ActivityMainBinding;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    public static volatile Usuario user;
    private ActivityMainBinding binding;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppDatabase db =   AppDatabase.getDatabase(getApplicationContext());
        Usuario usuario = new Usuario(1,0000,"",null,0,0);
        //db.usuarioDao().insertAll(usuario);
        MainActivity.user = usuario;

        if(data.getExtras()!=null){
            if(data.getExtras().getString(getString(R.string.firstLogin),"false").equals("true")){
                //If de user is new them create account else the user connect with old account with this email
                user.createAccount(getApplicationContext());
                FirebaseAuth user = FirebaseAuth.getInstance();
                System.out.println(user.getCurrentUser().getEmail());
            }

        }else{
            //Todo cargar los datos del usuario ya logueado y calcular el oxigeno obtenido en el tiempo fuera del juego
            System.out.println("Inicio correcto");
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            MainActivity.user =  Firebase.cargarUsuario(currentUser.getEmail(),getApplicationContext());
        }
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}