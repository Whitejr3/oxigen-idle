package com.elorrieta.idleoxygenvending.Database;


import static com.elorrieta.idleoxygenvending.MainActivity.lastId;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.elorrieta.idleoxygenvending.Entities.Mejora;
import com.elorrieta.idleoxygenvending.Entities.MejoraPorUser;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Firebase {
    private static FirebaseFirestore db;

    private static Usuario usuario;


    /*Recupera un usuario por el correo*/
    public static void cargarUsuario(String email, Context context) {
        db = FirebaseFirestore.getInstance();
        Thread thread = new Thread() {

            @Override
            public void run() {
                db.collection(context.getString(R.string.firebase_table_usuario)).whereEqualTo(context.getString(R.string.firebase_email_usuario), email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MainActivity.user = new Usuario(document, context);
                                break;
                            }
                        }

                    }
                });
            }


        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (MainActivity.user == null) {
            MainActivity.user = new Usuario(1, 0000, "", null, 0, 0);
        }
    }


    //Compruebe si el usuario existe en Firebase
    //Si existe se guarda en una variable en el Main
    //Si no existe crea el nuevo usuario con el nuevo ultimo id que genera otra queary
    //Y crea las relaciones de las mejoras por usuario en Firebase y lo guarda en Room
    public static void userExists(String email, Context context) {
        db = FirebaseFirestore.getInstance();
        Thread thread = new Thread() {

            @Override
            public void run() {
                db.collection(context.getString(R.string.firebase_table_usuario)).whereEqualTo("email", email).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MainActivity.user = new Usuario(document, context);
                                cargarMejorasPorUsuario(context);
                            }
                            if (MainActivity.user.getId() == -1) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                Usuario usuario = null;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    usuario = new Usuario(lastId, 0, currentUser.getEmail(), Date.from(Instant.now()), 0, 0);
                                    createUsuario(usuario, context);
                                    MainActivity.user = usuario;
                                    for (int i = 0; i < MainActivity.mejoras.size(); i++) {
                                        MejoraPorUser mejoraPorUser = new MejoraPorUser(lastId, MainActivity.mejoras.get(i).getId(), 0);
                                        Firebase.createMejoraPorUsuario(mejoraPorUser, context);
                                        AppDatabase room = AppDatabase.getDatabase(context);
                                        room.mejoraPorUsuarioDao().insertAll(mejoraPorUser);
                                    }

                                }


                            }

                        }
                    }
                });
            }
        };
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    //Recoge el ultimo id de la talba usuarios en firebase y asigna ese valor +1 a una variable en el main
    //Para gestionar bien la creacion de un nuevo usuario
    public static void getLastId(Context context) {
        lastId = -1;
        db = FirebaseFirestore.getInstance();
        db.collection(context.getString(R.string.firebase_table_usuario)).orderBy(context.getString(R.string.firebase_id_usuario), Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    /*El metodo createUsuario sirve tanto para crear un usuario como para modificar uno que ya exista , si el id no existe crea el usuario
     * y si existe lo modifica con los nuevos datos*/
    public static void createUsuario(Usuario usuario, Context context) {
        Map<String, Object> user = new HashMap<>();
        user.put(context.getString(R.string.firebase_id_usuario), usuario.getId());
        user.put(context.getString(R.string.firebase_oxygenQuantity_usuario), usuario.getOxygenQuantity());
        user.put(context.getString(R.string.firebase_email_usuario), usuario.getEmail());
        user.put(context.getString(R.string.firebase_last_conn_time_usuario), usuario.getLast_conn_time());
        user.put(context.getString(R.string.firebase_prestige_lvl_usuario), usuario.getPrestige_lvl());
        user.put(context.getString(R.string.firebase_prestige_points_usuario), usuario.getPrestige_points());


        db.collection("usuario").document(String.valueOf(usuario.getId())).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


    //Crea una mejora en Firebase o la modifica
    public static void createMejora(Mejora mejora, Context context) {
        Map<String, Object> mejoras = new HashMap<>();
        mejoras.put(context.getString(R.string.firebase_id_mejora), mejora.getId());
        mejoras.put(context.getString(R.string.firebase_nombre_mejora), mejora.getNombre());
        mejoras.put(context.getString(R.string.firebase_descripcion_mejora), mejora.getDescripcion());
        mejoras.put(context.getString(R.string.firebase_o2ps_mejora), mejora.getO2ps());
        mejoras.put(context.getString(R.string.firebase_baseprice_mejora), mejora.getBaseprice());


        db.collection(context.getString(R.string.firebase_table_mejoras)).document(String.valueOf(mejora.getId())).set(mejora).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    //Crea una mejora por usuario o la modifica en Firebase
    public static void createMejoraPorUsuario(MejoraPorUser mejoraPorUser, Context context) {
        Map<String, Object> mejoras = new HashMap<>();
        String id = mejoraPorUser.getIdUsuario() + " " + mejoraPorUser.getIdMejora();
        mejoras.put(context.getString(R.string.firebase_idusuario_mejoraporusuario), mejoraPorUser.getIdUsuario());
        mejoras.put(context.getString(R.string.firebase_idmejora_mejoraporusuario), mejoraPorUser.getIdMejora());
        mejoras.put(context.getString(R.string.firebase_nivel_mejoraporusuario), mejoraPorUser.getUpgrade_Amount());


        Thread thread = new Thread() {

            @Override
            public void run() {
                db.collection(context.getString(R.string.firebase_table_mejoras_por_user)).document(id).set(mejoras).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }


        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //Recoge las mejoras que hay en Firebase y las guarda en Room
    public static void cargarMejoras(Context context) {
        db = FirebaseFirestore.getInstance();
        Thread thread = new Thread() {

            @Override
            public void run() {
                db.collection(context.getString(R.string.firebase_table_mejoras)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            AppDatabase room = AppDatabase.getDatabase(context);
                            boolean noEstaLaMejora;
                            List<Mejora> mejorasActuales = room.mejoraDao().getAll();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Mejora mejora = new Mejora(document, context);
                                noEstaLaMejora = true;
                                for (Mejora actual : mejorasActuales) {
                                    if (actual.getId() == mejora.getId()) {
                                        noEstaLaMejora = false;
                                        return;
                                    }
                                }
                                if (noEstaLaMejora) {
                                    room.mejoraDao().insertAll(mejora);
                                }
                            }

                        }

                    }
                });
            }


        };
        thread.start();
        try {
            thread.join(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //Recoge las Mejoras por Usuario del usuario activo y lo guarda en room
    public static void cargarMejorasPorUsuario(Context context) {
        db = FirebaseFirestore.getInstance();
        Thread thread = new Thread() {

            @Override
            public void run() {
                db.collection(context.getString(R.string.firebase_table_mejoras_por_user)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            AppDatabase room = AppDatabase.getDatabase(context);
                            boolean noEstaLaMejoraPorUsuario;
                            List<MejoraPorUser> mejorasActuales = room.mejoraPorUsuarioDao().getAll();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MejoraPorUser mejora = new MejoraPorUser(document, context);
                                noEstaLaMejoraPorUsuario = true;
                                for (MejoraPorUser actual : mejorasActuales) {
                                    if (actual.getIdUsuario() == mejora.getIdUsuario() && actual.getIdMejora() == mejora.getIdMejora()) {
                                        noEstaLaMejoraPorUsuario = false;
                                        return;
                                    }
                                }
                                if (noEstaLaMejoraPorUsuario && MainActivity.user.getId() == mejora.getIdUsuario()) {
                                    room.mejoraPorUsuarioDao().insertAll(mejora);
                                }
                            }
                            MejoraPorUser.cargarDatos(context);

                        }

                    }
                });
            }


        };
        thread.start();
        try {
            thread.join(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
