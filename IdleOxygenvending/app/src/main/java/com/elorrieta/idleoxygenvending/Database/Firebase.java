package com.elorrieta.idleoxygenvending.Database;


import android.content.Context;
import android.media.metrics.Event;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elorrieta.idleoxygenvending.Entities.Usuario;
import com.elorrieta.idleoxygenvending.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class Firebase {
    private static FirebaseFirestore db;
    private static  boolean result= true ;
    private static int id ;

    private static int lastId;
    private static ArrayList<Event> events = new ArrayList<Event>();
    private static String userName ="";
    private static Event eventChange ;


    /*Recupera un usuario por el correo*/
    public static Usuario cargarUsuario(int id,Context context){
        final Usuario[] user = new Usuario[1];
        db = FirebaseFirestore.getInstance();
        db.collection(context.getString(R.string.firebase_table_usuario))
                .whereEqualTo(context.getString(R.string.firebase_id_usuario), id)
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
        if(user[0] == null){
            user[0] = new Usuario(1,0000,"",null,0,0);
        }
        return user[0];
    }

    public static int userExists(String email, Context context) {
        db = FirebaseFirestore.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        db.collection(context.getString(R.string.firebase_table_usuario))

                .whereEqualTo(context.getString(R.string.firebase_email_usuario), email)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId()!=null){
                                    id= Integer.parseInt(document.getId());
                                }else {
                                    id = 0;
                                }
                                break;
                            }

                        }
                        latch.countDown();

                    }
                });
        try {
            latch.await(10, TimeUnit.SECONDS); // Espera hasta 10 segundos para obtener el resultado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;

    }

    public static int getLastId(Context context) {
        db = FirebaseFirestore.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        db.collection(context.getString(R.string.firebase_table_usuario))
                .orderBy(context.getString(R.string.firebase_id_usuario), Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lastId = Integer.parseInt(document.getId());
                                break;
                            }

                        }
                        latch.countDown();

                    }
                });
        try {
            latch.await(10, TimeUnit.SECONDS); // Espera hasta 10 segundos para obtener el resultado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       return lastId;
    }

    public static void createUsusario(Usuario usuario,Context context) {
        Map<String,Object> user =new HashMap<>();
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
}
