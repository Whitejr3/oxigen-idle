package com.elorrieta.idleoxygenvending.Entities;

import android.content.Context;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.elorrieta.idleoxygenvending.Database.AppDatabase;
import com.elorrieta.idleoxygenvending.MainActivity;
import com.elorrieta.idleoxygenvending.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.List;

@Entity(primaryKeys = {"IdUsuario","IdMejora"})
public class MejoraPorUser {

    int IdUsuario;

    int IdMejora;

    int Upgrade_Amount;

    public MejoraPorUser() {
    }

    public MejoraPorUser(int idUsuario, int idMejora, int upgrade_Amount) {
        IdUsuario = idUsuario;
        IdMejora = idMejora;
        Upgrade_Amount = upgrade_Amount;
    }

    @Ignore
    public MejoraPorUser(QueryDocumentSnapshot document, Context context) {
        this.IdUsuario =  Integer.parseInt(document.get(context.getString(R.string.firebase_idusuario_mejoraporusuario)).toString());
        this.IdMejora = Integer.parseInt(document.get(context.getString(R.string.firebase_idmejora_mejoraporusuario)).toString());
        this.Upgrade_Amount = Integer.parseInt(document.get(context.getString(R.string.firebase_nivel_mejoraporusuario)).toString());
    }


    public int getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        IdUsuario = idUsuario;
    }

    public int getIdMejora() {
        return IdMejora;
    }

    public void setIdMejora(int idMejora) {
        IdMejora = idMejora;
    }

    public int getUpgrade_Amount() {
        return Upgrade_Amount;
    }

    public void setUpgrade_Amount(int upgrade_Amount) {
        Upgrade_Amount = upgrade_Amount;
    }

    public static void cargarDatos(Context context) {
        AppDatabase room = AppDatabase.getDatabase(context);
        List<MejoraPorUser> mejoraPorUserList = room.mejoraPorUsuarioDao().getAll();
        MainActivity.mejorasDelUsuario =  mejoraPorUserList;
    }
}
