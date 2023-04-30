package com.elorrieta.idleoxygenvending.Database;


import static com.elorrieta.idleoxygenvending.MainActivity.lastId;

import android.content.Context;

import androidx.annotation.NonNull;

import com.elorrieta.idleoxygenvending.Entities.Mejora;
import com.elorrieta.idleoxygenvending.Entities.Usuario;
import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Firebase {
    private static FirebaseFirestore db;


    /*Recupera un usuario por el correo*/
    public static Usuario cargarUsuario(String email, Context context) {
        final Usuario[] user = new Usuario[1];
        db = FirebaseFirestore.getInstance();
        db.collection(context.getString(R.string.firebase_table_usuario))
                .whereEqualTo(context.getString(R.string.firebase_email_usuario), email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user[0] = new Usuario(document);
                                break;
                            }
                        }

                    }
                });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (user[0] == null) {
            user[0] = new Usuario(1, 0000, "", null, 0, 0);
        }
        return user[0];
    }

    public static void userExists(String email, Context context) {
        db = FirebaseFirestore.getInstance();
        db.collection(context.getString(R.string.firebase_table_usuario))
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MainActivity.user = new Usuario(document);

                            }
                            if(MainActivity.user.getId()==-1){
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    Usuario usuario = new Usuario(lastId, 0, currentUser.getEmail(), Date.from(Instant.now()), 0, 0);
                                    createUsuario(usuario,context);
                                }
                            }
                        }
                    }
                });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void getLastId(Context context) {
        lastId = -1;
        db = FirebaseFirestore.getInstance();
        db.collection(context.getString(R.string.firebase_table_usuario))
                .orderBy(context.getString(R.string.firebase_id_usuario), Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lastId = Integer.parseInt(document.getId()) + 1;
                                System.out.println(document.getId());
                                break;

                            }

                        }
                    }
                });

    }

    public static void createUsuario(Usuario usuario, Context context) {
        Map<String, Object> user = new HashMap<>();
        user.put(context.getString(R.string.firebase_id_usuario), usuario.getId());
        user.put(context.getString(R.string.firebase_oxygenQuantity_usuario), usuario.getOxygenQuantity());
        user.put(context.getString(R.string.firebase_email_usuario), usuario.getEmail());
        user.put(context.getString(R.string.firebase_last_conn_time_usuario), usuario.getLast_conn_time());
        user.put(context.getString(R.string.firebase_prestige_lvl_usuario), usuario.getPrestige_lvl());
        user.put(context.getString(R.string.firebase_prestige_points_usuario), usuario.getPrestige_points());


        db.collection("usuario").document(String.valueOf(usuario.getId()))
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }


    public static void createMejora(Mejora mejora, Context context) {
        Map<String, Object> mejoras = new HashMap<>();
        mejoras.put(context.getString(R.string.firebase_id_mejora), mejora.getId());
        mejoras.put(context.getString(R.string.firebase_nombre_mejora), mejora.getNombre());
        mejoras.put(context.getString(R.string.firebase_descripcion_mejora), mejora.getDescripcion());
        mejoras.put(context.getString(R.string.firebase_o2ps_mejora), mejora.getO2ps());
        mejoras.put(context.getString(R.string.firebase_baseprice_mejora), mejora.getBaseprice());



        db.collection("mejoras").document(String.valueOf(mejora.getId()))
                .set(mejora)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
