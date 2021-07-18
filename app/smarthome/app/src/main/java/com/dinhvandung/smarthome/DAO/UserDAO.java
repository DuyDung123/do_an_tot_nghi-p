package com.dinhvandung.smarthome.DAO;

import com.dinhvandung.smarthome.DTO.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public UserDTO findOne(long id) {
        UserDTO userDTO = new UserDTO();
        return userDTO;
    }

    public List<UserDTO> findAll(){
        List<UserDTO> userDTOS = new ArrayList<>();
        return userDTOS;
    }

    public UserDTO findByUserNameAndPassword(String userName, String password) {
        UserDTO userDTO = new UserDTO();
        return userDTO;
    }
}
