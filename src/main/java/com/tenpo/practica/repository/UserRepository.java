package com.tenpo.practica.repository;

import com.tenpo.practica.entity.Users;

import org.springframework.data.repository.CrudRepository;


public interface UserRepository  extends CrudRepository <Users, String> {

    Users findUserByName(String username);

}
