package com.tenpo.practica.mvc;

import com.tenpo.practica.config.Messages;
import com.tenpo.practica.dto.ListaActividad;
import com.tenpo.practica.dto.UserDto;
import com.tenpo.practica.entity.Activity;
import com.tenpo.practica.entity.Users;
import com.tenpo.practica.exceptions.UserAlreadyExistException;
import com.tenpo.practica.services.IUserService;
import com.tenpo.practica.services.IActivityService;
import com.tenpo.practica.services.ISecurityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@RestController
@Validated
public class RestTenpoController {

	@Autowired
	IUserService userService;

	@Autowired
	Messages messages;
	
	@Autowired
	private ISecurityService securityService;
	
	
	@Autowired
	private IActivityService activityService;
	

	@PostMapping("/signup")
	public ResponseEntity<String> registerUserAccount(@Valid @RequestBody  UserDto accountDto) throws UserAlreadyExistException {

		if (securityService.isAuthenticated()) {
			return new ResponseEntity<String>(messages.get("signup.firstlogout"), HttpStatus.OK);
		}

		Users usuario = userService.registerNewUserAccount(accountDto);

		securityService.autoLogin(usuario.getName(), accountDto.getPassword());
		
		activityService.createActivityObject("signup", "OK",
				messages.get("signup.ok"), true);
		
		return new ResponseEntity<String>(messages.get("signup.ok"), HttpStatus.OK);
	}

	@GetMapping("/find")
	public ResponseEntity<String> findUserByName(@RequestParam("name") 
	@NotNull(message ="{name.notNull}") 
	@NotBlank (message = "{name.notBlank}")
	String name) throws UserAlreadyExistException {

		Users usuario = userService.findByName(name);
		if (usuario != null) {
			activityService.createActivityObject("find user", "OK",
					"user ".concat(name).concat(" encontrado"), true);

			return new ResponseEntity<String>("Usuario encontrado: "+usuario.getName(), HttpStatus.OK);
		}
		else {
			activityService.createActivityObject("find user", "OK",
					"user ".concat(name).concat(" encontrado"), true);
			
			return new ResponseEntity<String>("No encontrado", HttpStatus.OK);
		}
		

	}

	@GetMapping("/login")
	public ResponseEntity<String> login(@RequestParam("username") 
		@NotBlank(message ="{username.notBlank}")
		@NotNull(message ="{username.notNull}")
		String username, 
		@NotBlank(message ="{password.notBlank}")
		@NotNull(message ="{password.notNull}")
		@RequestParam("password") String password) {

		if (securityService.isAuthenticated()) {
			activityService.createActivityObject("login", "ERROR", messages.get("login.alreadylogged"), true);
			return new ResponseEntity<String>(messages.get("login.alreadylogged"), HttpStatus.OK);
		}
		
		securityService.autoLogin(username, password);

		activityService.createActivityObject("login", "OK", "login exitoso", true);
		
		return new ResponseEntity<String>(messages.get("login.successfull"), HttpStatus.OK);

	}


	@GetMapping(value = "/sumar")
	public ResponseEntity<String> sumar(@RequestParam("numero1")
	@NotNull(message ="{numer1.notNull}")
	Integer numero1, 
	@NotNull(message ="{numer2.notNull}")
	@RequestParam("numero2") Integer numero2) {


		long resultado = numero1+numero2;
		
		activityService.createActivityObject("suma", "OK", "suma exitosa", true);

		
		return new ResponseEntity<String>("Resultado "+resultado, HttpStatus.OK);

	}
	

	@SuppressWarnings("unchecked")
	@GetMapping("/logout")
	public ResponseEntity<String> logout() {

		activityService.createActivityObject("logout", "OK", "logout exitoso", true);
		
		SecurityContextHolder.getContext().setAuthentication(null);

		return new ResponseEntity<String>(messages.get("logout.ok"), HttpStatus.OK);

	}
	
	
	@GetMapping("/history")
    public ResponseEntity<Page<Activity>> getAllActivities(
                        @RequestParam(defaultValue = "0") Integer pageNumber, 
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(defaultValue = "date") String sortBy) 
    {
		
		Pageable paging = PageRequest.of(pageNumber, pageSize, org.springframework.data.domain.Sort.by(sortBy));
		

        ListaActividad lista = activityService.listAllActivity(paging);
 
        return new ResponseEntity<Page<Activity>>(lista.getLista(), HttpStatus.OK); 
    }
	
	
}
