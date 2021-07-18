package com.dinhvandung.smarthome.DAO;

import com.dinhvandung.smarthome.DTO.DeviceDTO;

import java.util.ArrayList;
import java.util.List;

public class DeviceDAO {

    public static List<DeviceDTO> deviceDTOS = new ArrayList<>();
    public List<DeviceDTO> findAll(){
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setId(1l);
        deviceDTO.setName("đèn 1");
        deviceDTO.setStatus("1");
        DeviceDTO deviceDTO1 = new DeviceDTO();
        deviceDTO1.setId(2l);
        deviceDTO1.setName("đèn 2");
        deviceDTO1.setStatus("0");
        deviceDTOS.add(deviceDTO);
        deviceDTOS.add(deviceDTO1);
        return deviceDTOS;
    }

    public DeviceDTO save( DeviceDTO deviceDTO){
        DeviceDTO deviceDTOModel = new DeviceDTO();
        deviceDTOModel.setId(deviceDTO.getId());
        deviceDTOModel.setName(deviceDTO.getName());
        deviceDTOModel.setStatus(deviceDTO.getStatus());
        deviceDTOS.add(deviceDTOModel);
        return deviceDTOModel;
    }
}
