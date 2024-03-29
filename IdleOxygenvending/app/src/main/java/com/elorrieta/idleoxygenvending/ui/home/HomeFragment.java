package com.elorrieta.idleoxygenvending.ui.home;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.elorrieta.idleoxygenvending.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    TextView tVOxigen;
    View root;
    HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();

    //Crea el fragment asigna el layout de Inicio
    //Y crear un listener encima de la Imagen para sumar O2 cada vez que se pulsa y realiza una animacion
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        ImageButton imageButton = root.findViewById(R.id.iBClicker);

        imageButton.setOnClickListener(v -> {
            MainActivity.user.setOxygenQuantity(1000);
            Animation animation = AnimationUtils.loadAnimation(root.getContext(), R.anim.pop);
            imageButton.setAnimation(animation);
            imageButton.startAnimation(animation);


        });
        updateOxygen();
        return root;
    }

    //Actualiza en texto de la ventana Inicio cada 0,1 segundos con un handler
    public void updateOxygen() {
        tVOxigen = root.findViewById(R.id.tVOxygen);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.user != null) {
                    tVOxigen.setText(MainActivity.user.showOxygenQuantity());

                    handler.postDelayed(this, 100);
                }


            }
        }, 100);
    }

    //Manejo de la ventana al cerrar o pausar la app
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOxygen();
    }


}