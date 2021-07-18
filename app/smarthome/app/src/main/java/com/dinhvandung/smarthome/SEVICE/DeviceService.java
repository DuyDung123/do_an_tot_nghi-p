package com.dinhvandung.smarthome.SEVICE;

import com.dinhvandung.smarthome.DAO.DeviceDAO;
import com.dinhvandung.smarthome.DTO.DeviceDTO;

import java.util.List;

public class DeviceService {

    private DeviceDAO deviceDAO = new DeviceDAO();

    public List<DeviceDTO> findAll(){
        List<DeviceDTO> deviceDTOS = deviceDAO.findAll();
        return deviceDTOS;
    }

    public  DeviceDTO save(DeviceDTO deviceDTO){
        return deviceDAO.save(deviceDTO);
    }
}
