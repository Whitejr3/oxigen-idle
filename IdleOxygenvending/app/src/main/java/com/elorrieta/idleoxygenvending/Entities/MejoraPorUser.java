package com.elorrieta.idleoxygenvending.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
}
