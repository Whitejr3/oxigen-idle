package com.elorrieta.idleoxygenvending.Database;

import androidx.room.TypeConverter;

import java.util.Date;
//Convierte el tipo de dato Date ya que Room no lo detecta
//Date se convierte en un long para guardarlo en Room y al sacarlo pasa de long a Date
public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
