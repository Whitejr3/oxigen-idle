package com.elorrieta.idleoxygenvending.ui.home;

import android.os.Bundle;
import android.os.Handler;
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
    private Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel=
                new ViewModelProvider(this).get(HomeViewModel.class);

       binding = FragmentHomeBinding.inflate(inflater, container, false);
       root = binding.getRoot();
       ImageButton imageButton = root.findViewById(R.id.iBClicker);

        imageButton.setOnClickListener(v -> {
            MainActivity.user.setOxygenQuantity(1000);
        });
        updateOxygen();
        return root;
    }

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

    public void updateOxygen(){
        tVOxigen = root.findViewById(R.id.tVOxygen);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.user!=null){
                    tVOxigen.setText(MainActivity.user.showOxygenQuantity());

                    handler.postDelayed(this, 100);
                }


            }
        }, 100);

    }

}