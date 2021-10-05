package com.tenpo.practica.services;

public interface ISecurityService {
	
	
	 boolean isAuthenticated();
	 void autoLogin(String username, String password);
	 String getUsername();
}
