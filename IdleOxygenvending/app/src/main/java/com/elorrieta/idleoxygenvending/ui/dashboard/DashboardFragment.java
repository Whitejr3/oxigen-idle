package com.elorrieta.idleoxygenvending.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elorrieta.idleoxygenvending.Database.AppDatabase;
import com.elorrieta.idleoxygenvending.Entities.MejoraPorUser;
import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.elorrieta.idleoxygenvending.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private LinearLayout linearLayout;

    //Genere el layout de Mejoras, generadando las mejoras de forma dinamica con los datos de las relaciones
    //Al mejorar se comprueba que tiene el 02 necesario si tiene el nivel aumenta, se guarda en room y se actualizan los datos en la pantalla
    @SuppressLint("ResourceAsColor")
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
            nombre.setTextColor(R.color.text_color);
            //nombre.setTextColor(layout.getChildAt(1).getSolidColor());
            nombre.setText(MainActivity.mejoras.get(i).getNombre());
            TextView precio = new TextView(root.getContext());
            precio.setLayoutParams(layout.getChildAt(2).getLayoutParams());
            precio.setTextColor(R.color.text_color);
            TextView nivel = new TextView(root.getContext());
            nivel.setTextColor(R.color.text_color);
            nivel.setLayoutParams(layout.getChildAt(3).getLayoutParams());
            if (MainActivity.mejorasDelUsuario.size() != 0) {
                precio.setText(MainActivity.mejoras.get(i).showPrice(i));
                nivel.setText(String.valueOf(MainActivity.mejorasDelUsuario.get(i).getUpgrade_Amount()));
            } else {
                //Si no estan bien cargados los datos de las mejoras por usuario para que no hayan valores nulos
                nivel.setText(R.string.mejoras_nivel_null);
                precio.setText(R.string.mejoras_precio_null);

            }
            TextView o2ps = new TextView(root.getContext());
            o2ps.setLayoutParams(layout.getChildAt(4).getLayoutParams());
            o2ps.setText(MainActivity.mejoras.get(i).showO2psTotal(i));
            o2ps.setTextColor(R.color.text_color);
            Button button = new Button(layout1.getContext());
            button.setLayoutParams(layout.getChildAt(5).getLayoutParams());
            button.setText(MainActivity.mejoras.get(i).showO2ps());
            button.setBackgroundColor(R.color.green_base);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(MainActivity.user.getOxygenQuantity()>=MainActivity.mejoras.get(finalI).getPrice(finalI)){
                        AppDatabase room = AppDatabase.getDatabase(getContext());
                        MainActivity.user.setOxygenQuantity(-MainActivity.mejoras.get(finalI).getPrice(finalI));
                        MainActivity.mejorasDelUsuario.get(finalI).setUpgrade_Amount(MainActivity.mejorasDelUsuario.get(finalI).getUpgrade_Amount()+1);
                        room.mejoraPorUsuarioDao().update(MainActivity.mejorasDelUsuario.get(finalI).getIdUsuario(), MainActivity.mejorasDelUsuario.get(finalI).getIdMejora(), MainActivity.mejorasDelUsuario.get(finalI).getUpgrade_Amount());
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