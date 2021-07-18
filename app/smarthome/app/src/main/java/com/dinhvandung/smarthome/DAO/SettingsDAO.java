package com.dinhvandung.smarthome.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.Database.CreateDatabase;
import com.dinhvandung.smarthome.Database.DatabaseServer;

import java.util.List;

public class SettingsDAO {

    SQLiteDatabase database;
    DatabaseServer databaseServer;

    public SettingsDAO(){
    }

    public SettingsDAO(Context context){
        databaseServer = new DatabaseServer();
        CreateDatabase createDatabase = new CreateDatabase(context);
        database = createDatabase.open();
    }

    public Long save(SettingsDTO settingsDTO){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_SETTINGS_IPSERVER,settingsDTO.getIpserver());
        Long kiemtra = database.insert(CreateDatabase.TB_SETTINGS,null,contentValues);
        return  kiemtra;
    }

    public Boolean update(SettingsDTO settingsDTO){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_SETTINGS_IPSERVER,settingsDTO.getIpserver());
        int check = database.update(CreateDatabase.TB_SETTINGS,contentValues,CreateDatabase.TB_SETTINGS_ID +"="+settingsDTO.getId(),null);
        if (check!= 0){
            return true;
        }else {
            return false;
        }
    }

    public SettingsDTO findOne(){
        SettingsDTO settingsDTO = new SettingsDTO();
        String truyvan = "select * from "+ CreateDatabase.TB_SETTINGS;
        Cursor cursor = database.rawQuery(truyvan,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            settingsDTO.setId(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_SETTINGS_ID)));
            settingsDTO.setIpserver(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_SETTINGS_IPSERVER)));
            cursor.moveToNext();
        }
        return  settingsDTO;
    }

    public boolean checkIpServerSettings(){
        //count(*)
        String truyvan = "select * from "+ CreateDatabase.TB_SETTINGS;
        Cursor cursor = database.rawQuery(truyvan,null);
        if (cursor.getCount()!= 0){
            return true;
        }else {
            return false;
        }
    }
}
