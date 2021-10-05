package com.tenpo.practica.services;

import com.tenpo.practica.dto.UserDto;
import com.tenpo.practica.entity.Users;
import com.tenpo.practica.exceptions.UserAlreadyExistException;
import com.tenpo.practica.repository.RoleRepository;
import com.tenpo.practica.repository.UserRepository;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService{
	
	 @Autowired
	 UserRepository userRepository;
	 
	 @Autowired
	    private RoleRepository roleRepository;
	 
	 @Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    @Override
    public Users registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
    	
       	if(userRepository.findUserByName(userDto.getUsername())!=null)
    		throw new UserAlreadyExistException("Este usuario ya existe");
    	else {
    		Users user = new Users();
        	user.setName(userDto.getUsername());
        	user.setRoles(new HashSet<>(roleRepository.findAll()));
        	user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        	return userRepository.save(user);
    	}
  	
        
    }

	@Override
	public Users findByName(String name) {
		Users user = userRepository.findUserByName(name);
		
		if(user!=null) {
			System.out.println("usuario encontrado");
			return user;
		} else 
			System.out.println("usuario encontrado");
		
		return null;
	}
}
