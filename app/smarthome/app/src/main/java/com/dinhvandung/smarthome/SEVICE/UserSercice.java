package com.dinhvandung.smarthome.SEVICE;

import com.dinhvandung.smarthome.DAO.UserDAO;
import com.dinhvandung.smarthome.DTO.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserSercice {

    private UserDAO userDAO = new UserDAO();

    public UserDTO findOne(long id) {
        UserDTO userDTO = userDAO.findOne(id);
        return userDTO;
    }

    public List<UserDTO> findAll(){
        List<UserDTO> userDTOS = userDAO.findAll();
        return userDTOS;
    }

    public UserDTO findByUserNameAndPassword(String userName, String password) {
        UserDTO userDTO = userDAO.findByUserNameAndPassword(userName,password);
        return userDTO;
    }
}
