package com.elorrieta.idleoxygenvending;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private SharedPreferences sharedPreferences;

    /*Este metodo crea el layout y pone en cuenta a un cronometro que si se acaba pasa al MainActivity
    * Por otro lado comprueba que haya inciado sesion alguna vez y si tiene una cuenta ya guardada
    * Si el usuario tiene la cuenta solo tiene que esperar 5 segundos y vuelve al juego
    * Sino le salta google auth para que inicie sesion y si no lo hace entra con una cuenta nueva*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int time = 5000;
        FirebaseApp.initializeApp(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        sharedPreferences = getSharedPreferences(getString(R.string.nameSpaceSharedPreferences), MODE_PRIVATE);
        if (sharedPreferences.getString(getString(R.string.key1), null) == null) {
            time = 30000;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }
        new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                // Actualizar la UI con el tiempo restante

            }
            public void onFinish() {
                // Mostrar un mensaje cuando el tiempo se acabe
                if (sharedPreferences.getString(getString(R.string.key1), null) == null) {
                    saveData("", false);
                } else {
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        }.start();
      //  mAuth = FirebaseAuth.getInstance(); para recuperar la instancia de la cuenta que se ha logueado
    }

    /*Despues de lanzar el intenta para que se logue el usuario con google auth
     *Empieza esta funcion que llama a la funcion firebaseAuthwithGoogle*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveData(user.getEmail(), true);
                        }
                        if (task.isCanceled()) {
                            Log.d(TAG, "signInWithCredential:unsuccessful");
                            saveData("", false);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    /*Guarda con SharedPreferences el correo del usuario y si se ha registrado con google o no
     * 1 Si el usuario se registrar con google auth se guarda su correo y el booleano a true
     * 2 Si el usuario no se registra con google auth, se guarda el correo como cadena vacia y el booleano a false*/
    private void saveData(String gmail, Boolean googleAuth) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.key1), gmail);
        editor.putBoolean(getString(R.string.key2), googleAuth);
        editor.apply();
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.firstLogin), true);
        startActivity(intent);
    }

}