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
    private Thread hilo;
    private static volatile boolean alive;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        updateOxygen();
        ImageButton imageButton = root.findViewById(R.id.iBClicker);
        alive = true;
        imageButton.setOnClickListener(v -> {
            MainActivity.oxygen +=1;
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hilo.interrupt();
        binding = null;
        alive = false;
    }

    private  void updateOxygen(){
        View view = binding.getRoot();
        tVOxigen = view.findViewById(R.id.tVOxygen);

       hilo= new Thread(){
            @Override
            public void run() {
                do{
                    tVOxigen.setText("O2: "+ Math.round(MainActivity.oxygen));
                }while (alive);
            }
        };
       hilo.start();
    }
}