package com.elorrieta.idleoxygenvending.ui.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elorrieta.idleoxygenvending.Entities.MejoraPorUser;
import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.elorrieta.idleoxygenvending.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private LinearLayout linearLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        linearLayout = root.findViewById(R.id.mejoras_layout_scroll);

        MejoraPorUser.cargarDatos(getContext());
        for (int i = 0; i < MainActivity.mejoras.size(); i++) {
            ConstraintLayout layout = (ConstraintLayout) linearLayout.getChildAt(0);
            ConstraintLayout layout1 = new ConstraintLayout(root.getContext());
            ImageView imageView = new ImageView(root.getContext());
            TextView nombre = new TextView(root.getContext());
            nombre.setLayoutParams(layout.getChildAt(1).getLayoutParams());
            nombre.setText(MainActivity.mejoras.get(i).getNombre());
            TextView precio = new TextView(root.getContext());
            precio.setLayoutParams(layout.getChildAt(2).getLayoutParams());

            TextView nivel = new TextView(root.getContext());
            nivel.setLayoutParams(layout.getChildAt(3).getLayoutParams());
            if (MainActivity.mejorasDelUsuario.size() != 0) {
                precio.setText(MainActivity.mejoras.get(i).showPrice(i));
                nivel.setText(String.valueOf(MainActivity.mejorasDelUsuario.get(i).getUpgrade_Amount()));
            } else {
                nivel.setText("0");
                precio.setText("Desconocido");

            }
            TextView o2ps = new TextView(root.getContext());
            o2ps.setLayoutParams(layout.getChildAt(4).getLayoutParams());
            o2ps.setText(MainActivity.mejoras.get(i).showO2psTotal(i));
            Button button = new Button(layout1.getContext());
            button.setLayoutParams(layout.getChildAt(5).getLayoutParams());
            button.setText(MainActivity.mejoras.get(i).showO2ps());
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(MainActivity.user.getOxygenQuantity()>=MainActivity.mejoras.get(finalI).getPrice(finalI)){
                        MainActivity.user.setOxygenQuantity(-MainActivity.mejoras.get(finalI).getPrice(finalI));
                        MainActivity.mejorasDelUsuario.get(finalI).setUpgrade_Amount(MainActivity.mejorasDelUsuario.get(finalI).getUpgrade_Amount()+1);
                        nivel.setText(String.valueOf(MainActivity.mejorasDelUsuario.get(finalI).getUpgrade_Amount()));
                        precio.setText(MainActivity.mejoras.get(finalI).showPrice(finalI));
                        o2ps.setText(MainActivity.mejoras.get(finalI).showO2psTotal(finalI));

                    }

                }
            });
            layout1.setLayoutParams(layout.getLayoutParams());
            imageView.setLayoutParams(layout.getChildAt(0).getLayoutParams());
            imageView.setImageResource(R.drawable.tree);
            layout1.addView(imageView);
            layout1.addView(button);
            layout1.addView(nombre);
            layout1.addView(precio);
            layout1.addView(nivel);
            layout1.addView(o2ps);
            linearLayout.addView(layout1);
        }
        linearLayout.removeViewAt(0);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}