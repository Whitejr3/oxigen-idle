package com.elorrieta.idleoxygenvending;

import android.content.Intent;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static volatile Usuario user = new Usuario();
    public static volatile int lastId;
    private ActivityMainBinding binding;
    private InterstitialAd mInterstitialAd;

    public static volatile List<Mejora> mejoras;

    public static volatile  List<MejoraPorUser> mejorasDelUsuario;

    private volatile boolean activo=true;

    private  Thread hiloMejoras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Mejora.cargarDatos(getApplicationContext());
        if(data.getExtras()!=null){
            if(data.getExtras().getString(getString(R.string.firstLogin),"false").equals("true")) {
                //If de user is new them create account else the user connect with old account with this email
                user.loadAccount(getApplicationContext());
            }
        }
        MejoraPorUser.cargarDatos(getApplicationContext());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        hiloMejoras = new Thread(){
            @Override
            public void run() {
                do{
                    int o2ps =0;
                    if(mejorasDelUsuario!=null){
                        for (int i = 0; i <mejorasDelUsuario.size() ; i++) {
                            o2ps+= mejoras.get(i).getO2ps()*mejorasDelUsuario.get(i).getUpgrade_Amount();
                        }
                        user.setOxygenQuantity(o2ps);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }while (activo);
           }
        };
        hiloMejoras.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activo= false;
        Firebase.createUsuario(user,getApplicationContext());
        AppDatabase room = AppDatabase.getDatabase(getApplicationContext());
        for (int i = 0; i < mejoras.size(); i++) {
            room.mejoraDao().update(mejoras.get(i).getId(),mejoras.get(i).getNombre(),mejoras.get(i).getDescripcion(),mejoras.get(i).getO2ps(),mejoras.get(i).getBaseprice());
            room.mejoraPorUsuarioDao().update(mejorasDelUsuario.get(i).getIdUsuario(),mejorasDelUsuario.get(i).getIdMejora(),mejorasDelUsuario.get(i).getUpgrade_Amount());
            Firebase.createMejora(mejoras.get(i),getApplicationContext());
            Firebase.createMejoraPorUsuario(mejorasDelUsuario.get(i),getApplicationContext());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activo= false;
        Firebase.createUsuario(user,getApplicationContext());
        AppDatabase room = AppDatabase.getDatabase(getApplicationContext());
        for (int i = 0; i < mejoras.size(); i++) {
            room.mejoraDao().update(mejoras.get(i).getId(),mejoras.get(i).getNombre(),mejoras.get(i).getDescripcion(),mejoras.get(i).getO2ps(),mejoras.get(i).getBaseprice());
            room.mejoraPorUsuarioDao().update(mejorasDelUsuario.get(i).getIdUsuario(),mejorasDelUsuario.get(i).getIdMejora(),mejorasDelUsuario.get(i).getUpgrade_Amount());
            Firebase.createMejora(mejoras.get(i),getApplicationContext());
            Firebase.createMejoraPorUsuario(mejorasDelUsuario.get(i),getApplicationContext());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activo= true;
        hiloMejoras = new Thread(){
            @Override
            public void run() {
                do{
                    int o2ps =0;
                    if(mejorasDelUsuario!=null){
                        for (int i = 0; i <mejorasDelUsuario.size() ; i++) {
                            o2ps+= mejoras.get(i).getO2ps()*mejorasDelUsuario.get(i).getUpgrade_Amount();
                        }
                        user.setOxygenQuantity(o2ps);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }while (activo);
            }
        };
        hiloMejoras.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activo=true;
    }
}