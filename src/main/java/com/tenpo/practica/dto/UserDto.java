package com.tenpo.practica.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto {


	
	@NotBlank(message = "Username no debe estar vacio")
	@NotNull(message = "Username no debe ser nulo")
	@Size(min = 5, message = "Longitud de Username no debe ser menor a 5 caracteres")
	String username;
	
	@NotNull(message = "Password no debe ser nula")
	@NotBlank(message = "Password no debe estar vacia")
	@Size(min = 5, message = "Longitud de Password no debe ser menor a 5 caracteres")
    String password;

	
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
