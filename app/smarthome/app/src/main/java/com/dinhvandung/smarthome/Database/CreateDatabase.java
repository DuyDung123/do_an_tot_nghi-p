package com.dinhvandung.smarthome.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CreateDatabase extends SQLiteOpenHelper {

    public static String TB_SETTINGS = "SETTINGS";

    public static String TB_SETTINGS_ID = "ID";
    public static String TB_SETTINGS_IPSERVER = "IPSERVER";

    public CreateDatabase(@Nullable Context context) {
        super(context, "smarthome", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tbSETTINGS = "CREATE TABLE " + TB_SETTINGS + " ( " + TB_SETTINGS_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TB_SETTINGS_IPSERVER + " TEXT )";

        db.execSQL(tbSETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase open(){
        return this.getWritableDatabase();
    }
}
