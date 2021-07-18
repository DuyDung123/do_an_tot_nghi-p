package com.dinhvandung.smarthome.SEVICE;

import android.content.Context;


import com.dinhvandung.smarthome.DAO.SettingsDAO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;


public class SettingsService {

    SettingsDAO settingsDAO;

    public SettingsService(Context context){
        settingsDAO = new SettingsDAO(context);
    }


    public Long save(SettingsDTO settingsDTO){

        return  settingsDAO.save(settingsDTO);
    }

    public Boolean update(SettingsDTO settingsDTO){
        return  settingsDAO.update(settingsDTO);
    }

    public SettingsDTO findOne(){
        return settingsDAO.findOne();
    }

    public boolean checkIpServerSettings(){
        return settingsDAO.checkIpServerSettings();
    }
}
