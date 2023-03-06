package com.elorrieta.idleoxygenvending.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private FragmentHomeBinding binding;
    View root;

    HomeViewModel homeViewModel;
    private Thread hilo;

    private static volatile boolean alive;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel=
                new ViewModelProvider(this).get(HomeViewModel.class);

       binding = FragmentHomeBinding.inflate(inflater, container, false);
       root = binding.getRoot();
       ImageButton imageButton = root.findViewById(R.id.iBClicker);

        imageButton.setOnClickListener(v -> {
            MainActivity.user.setOxygenQuantity(MainActivity.user.getOxygenQuantity()+1);
        });
        alive=true;
        updateOxygen();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        alive=false;
    }

    @Override
    public void onPause() {
        super.onPause();
        alive=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        alive=true;
        updateOxygen();
    }

    public void updateOxygen(){
        tVOxigen = root.findViewById(R.id.tVOxygen);
        hilo= new Thread(){
            @Override
            public void run() {
                do{
                    tVOxigen.setText("O2: " + Math.round(MainActivity.user.getOxygenQuantity()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }while (alive);
            }
        };
        hilo.start();
    }

}