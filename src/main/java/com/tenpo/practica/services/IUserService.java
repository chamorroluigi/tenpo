package com.tenpo.practica.services;

import com.tenpo.practica.dto.UserDto;
import com.tenpo.practica.entity.Users;
import com.tenpo.practica.exceptions.UserAlreadyExistException;

public interface IUserService {

    Users registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;
    
    Users findByName(String name);

}
