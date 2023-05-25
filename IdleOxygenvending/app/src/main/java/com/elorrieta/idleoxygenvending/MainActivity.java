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
    public static volatile List<Mejora> mejoras;
    public static volatile List<MejoraPorUser> mejorasDelUsuario;
    private ActivityMainBinding binding;
    // Objeto para realizar los anuncios, probado pero no se puede usar hasta publicar la app
    private InterstitialAd mInterstitialAd;
    private volatile boolean activo = true;

    private Thread hiloMejoras;


    //Gestion de la app general, aqui se encuentra lo generado por cada mejora, y por si acaso la recarga de las listas de mejora y mejoraPorUsuario
    //Tambien gestiona si es la primera vez que entra para comprobar si es un usuario que ya existe o hay que crearlo
    //Tambien gestiona en menu inferior de la app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Mejora.cargarDatos(getApplicationContext());
        if (data.getExtras() != null) {
            if (data.getExtras().getString(getString(R.string.firstLogin), "false").equals("true")) {
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
        startHiloMejoras();
    }

    //Guarda los datos tanto en local como en firebase del usuario activo y las relacion con las mejoras
    //por si ha habido algun cambio para que haya persistencia
    private void guardarDatos() {
        Firebase.createUsuario(user, getApplicationContext());
        AppDatabase room = AppDatabase.getDatabase(getApplicationContext());
        for (int i = 0; i < mejoras.size(); i++) {
            room.mejoraPorUsuarioDao().update(mejorasDelUsuario.get(i).getIdUsuario(), mejorasDelUsuario.get(i).getIdMejora(), mejorasDelUsuario.get(i).getUpgrade_Amount());
            Firebase.createMejoraPorUsuario(mejorasDelUsuario.get(i), getApplicationContext());
        }
    }

    //Genera un hilo que depende de un booleano llamado activo, mientras activo sea true
    //Cuenta cuanto O2 esta generando el usuario en base al nivel de las mejoras y cada segundo lo suma
    //Si la variable cambia a false el hilo termina.
    private void startHiloMejoras() {
        hiloMejoras = new Thread() {
            @Override
            public void run() {
                do {
                    MejoraPorUser.cargarDatos(getApplicationContext());
                    int o2ps = 0;
                    if (mejorasDelUsuario != null) {
                        for (int i = 0; i < mejorasDelUsuario.size(); i++) {
                            o2ps += mejoras.get(i).getO2ps() * mejorasDelUsuario.get(i).getUpgrade_Amount();
                        }
                        user.setOxygenQuantity(o2ps);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } while (activo);
            }
        };
        hiloMejoras.start();
    }

    //Gestion de los estados de la app, cuando la dejan es suspension o la cierran los datos se guardan
    // y cuando vuelve a entrar pone el hiloMejoras en marcha para seguir contando.
    @Override
    protected void onPause() {
        super.onPause();
        activo = false;
        guardarDatos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activo = false;
        guardarDatos();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activo = true;
        startHiloMejoras();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activo = true;
    }

}