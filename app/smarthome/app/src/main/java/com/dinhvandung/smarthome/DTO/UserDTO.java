package com.dinhvandung.smarthome.DTO;

public class UserDTO {

    private  Long id;
    private  String userName;
    private  String passWoord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWoord() {
        return passWoord;
    }

    public void setPassWoord(String passWoord) {
        this.passWoord = passWoord;
    }
}
