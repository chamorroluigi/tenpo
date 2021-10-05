package com.tenpo.practica.mvc;

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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
	private ISecurityService securityService;
	
	
	@Autowired
	private IActivityService activityService;
	

	@PostMapping("/signup")
	public ResponseEntity<String> registerUserAccount(@Valid @RequestBody  UserDto accountDto) throws UserAlreadyExistException {

		if (securityService.isAuthenticated()) {
			return new ResponseEntity<String>("Primero debes desloguearte de sesion actual", HttpStatus.OK);
		}

		Users usuario = userService.registerNewUserAccount(accountDto);

		securityService.autoLogin(usuario.getName(), accountDto.getPassword());
		
		activityService.createActivityObject("register", "OK",
				"Registro exitoso, ahora estas logueado", true);
		
		return new ResponseEntity<String>("Registrado exitosamente, ahora estas logueado", HttpStatus.OK);
	}

	@GetMapping("/find")
	public ResponseEntity<String> findUserByName(@RequestParam("name") 
	@NotNull(message ="Nombre no debe ser nulo") 
	@NotBlank (message = "Nombre no debe estar vacio")
	String name) throws UserAlreadyExistException {

		Users usuario = userService.findByName(name);
		if (usuario != null) {
			activityService.createActivityObject("find user", "Error",
					"user ".concat(name).concat(" no encontrado"), true);

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
		@NotBlank(message ="Username no debe estar vacio")
		@NotNull(message ="Username no debe ser nulo")
		String username, 
		@NotBlank(message ="Password no debe estar vacia")
		@NotNull(message ="Password no debe ser nula")
		@RequestParam("password") @NotBlank String password) {

		if (securityService.isAuthenticated()) {
			activityService.createActivityObject("login", "ERROR", "Ya estaba logueado", true);
			return new ResponseEntity<String>("Ya estas logueado", HttpStatus.OK);
		}
		
		securityService.autoLogin(username, password);

		activityService.createActivityObject("login", "OK", "login exitoso", true);
		
		return new ResponseEntity<String>("Logueado Exitosamente", HttpStatus.OK);

	}


	@GetMapping(value = "/sumar", params = { "numero1", "numero2" })
	public ResponseEntity<String> sumar(@RequestParam("numero1")
	@NotNull(message ="Numero1 no debe ser nulo")
	Integer numero1, 
	@NotNull(message ="Numero2 no debe ser nulo")
	@RequestParam("numero2") Integer numero2) {

		if (!securityService.isAuthenticated()) {
			activityService.createActivityObject("sumar", "ERROR", "No esta logueado", true);
			return new ResponseEntity<String>("No estas logueado", HttpStatus.OK);
		}
		long resultado = numero1+numero2;
		
		activityService.createActivityObject("suma", "OK", "suma exitosa", true);

		
		return new ResponseEntity<String>("Resultado "+resultado, HttpStatus.OK);

	}
	

	@SuppressWarnings("unchecked")
	@GetMapping("/logout")
	public ResponseEntity<String> logout() {

		activityService.createActivityObject("logout", "OK", "logout exitoso", true);
		
		SecurityContextHolder.getContext().setAuthentication(null);

		return new ResponseEntity<String>("Estas deslogueado", HttpStatus.OK);

	}
	
	
	@GetMapping("/history")
    public ResponseEntity<Page<Activity>> getAllEmployees(
                        @RequestParam(defaultValue = "0") Integer pageNumber, 
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(defaultValue = "date") String sortBy) 
    {
		
		Pageable paging = PageRequest.of(pageNumber, pageSize, org.springframework.data.domain.Sort.by(sortBy));
		

        ListaActividad lista = activityService.listAllActivity(paging);
 
        return new ResponseEntity<Page<Activity>>(lista.getLista(), HttpStatus.OK); 
    }
	

	@ExceptionHandler(value = UserAlreadyExistException.class)
	public ResponseEntity<String> handleBlogAlreadyExistsException(UserAlreadyExistException blogAlreadyExistsException) {
		activityService.createActivityObject("register", "ERROR", "Usuario ya existe", false);

		return new ResponseEntity<String>("Usuario ya existe", HttpStatus.CONFLICT);
	}
	
	
		
	
	@ExceptionHandler(value = AuthenticationException.class)
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException blogAlreadyExistsException) {
		
		activityService.createActivityObject("login", "ERROR", "Usuario existente", false);
		
		return new ResponseEntity<String>("Usuario inexistente", HttpStatus.CONFLICT);
	}
	
}
