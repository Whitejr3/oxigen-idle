package com.elorrieta.idleoxygenvending;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.elorrieta.idleoxygenvending.Database.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    private SharedPreferences sharedPreferences;

    /*Este metodo crea el layout y pone en cuenta a un cronometro que si se acaba pasa al MainActivity
     * Por otro lado comprueba que haya inciado sesion alguna vez y si tiene una cuenta ya guardada
     * Si el usuario tiene la cuenta solo tiene que esperar 5 segundos y vuelve al juego
     * Sino le salta google auth para que inicie sesion y si no lo hace entra con una cuenta nueva*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int time = 5000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        FirebaseApp.initializeApp(getApplicationContext());
        sharedPreferences = getSharedPreferences(getString(R.string.nameSpaceSharedPreferences), MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            findViewById(R.id.google_sign).setVisibility(View.INVISIBLE);

            Thread Carganormal = new Thread() {
                @Override
                public void run() {
                    Firebase.cargarUsuario(currentUser.getEmail(), getApplicationContext());
                }
            };
            Carganormal.start();
            try {
                Carganormal.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            new CountDownTimer(time, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Actualizar la UI con el tiempo restante

                }

                public void onFinish() {
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }.start();
        } else {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);

            findViewById(R.id.google_sign).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
            Thread primeraSesion = new Thread() {
                @Override
                public void run() {
                    Firebase.getLastId(getApplicationContext());
                    Firebase.cargarMejoras(getApplicationContext());
                }
            };
            primeraSesion.start();

            try {
                primeraSesion.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }

    /*Al hacer click en el boton para iniciar sesión, lanza este metodo que genera el intent a la ventana para
     * selecionar una cuenta de google play*/
    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /*Despues de lanzar el intenta para que se logue el usuario con google auth
     *Empieza esta funcion que llama a la funcion firebaseAuthwithGoogle*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /*Esta funcion comprueba si el usuario a inciado la sesion correctamente,si ha fallado o si la ha cancelado
     *Si ha fallado no hace nada
     *Si la ha cancelado llama a saveData con los parametros "" y false
     *Si ha iniciado sesion correctamente llama a saveData con los parametros correo de la cuenta y true*/
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.putExtra(getString(R.string.firstLogin), "true");
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:unsuccessful");
            }
        });

    }

}