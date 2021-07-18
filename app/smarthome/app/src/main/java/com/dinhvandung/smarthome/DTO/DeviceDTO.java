package com.dinhvandung.smarthome.DTO;

import java.util.ArrayList;
import java.util.List;

public class DeviceDTO {

    private Long id;
    private String name;
    private String status;
    private String devicecode;
    private Long category;
    private List<StatusDTO> listStatus = new ArrayList<StatusDTO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public List<StatusDTO> getListStatus() {
        return listStatus;
    }

    public void setListStatus(List<StatusDTO> listStatus) {
        this.listStatus = listStatus;
    }
}
