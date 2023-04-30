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
import com.elorrieta.idleoxygenvending.Entities.Mejora;
import com.elorrieta.idleoxygenvending.Entities.MejoraPorUser;
import com.elorrieta.idleoxygenvending.Entities.Usuario;
import com.elorrieta.idleoxygenvending.databinding.ActivityMainBinding;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static volatile Usuario user= new Usuario();
    public static volatile int lastId;
    private ActivityMainBinding binding;
    private InterstitialAd mInterstitialAd;

    public static volatile List<Mejora> mejoras;

    private static volatile  List<MejoraPorUser> mejoraspDelUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(data.getExtras()!=null){
            if(data.getExtras().getString(getString(R.string.firstLogin),"false").equals("true")){
                //If de user is new them create account else the user connect with old account with this email
                user.loadAccount(getApplicationContext());
                FirebaseAuth userAuth = FirebaseAuth.getInstance();
                System.out.println(userAuth.getCurrentUser().getEmail());

            }

        }

        for (int i = 0; i < mejoras.size() ; i++) {
            Firebase.createMejora(mejoras.get(i),getApplicationContext());
        }
        System.out.println(mejoras.size());
        System.out.println(user.getOxygenQuantity()+ user.getEmail());

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