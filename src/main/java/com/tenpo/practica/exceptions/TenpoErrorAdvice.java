package com.tenpo.practica.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.tenpo.practica.config.Messages;
import com.tenpo.practica.services.IActivityService;
@ControllerAdvice
public class TenpoErrorAdvice {

	@Autowired
	private IActivityService activityService;

	@Autowired
	Messages messages;
	
	private final String ERROR_STR = "ERROR";

	@SuppressWarnings("rawtypes")
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onConstraintValidationException(WebRequest request, ConstraintViolationException e) {
		
		String action = extractAction(request);
		
		ValidationErrorResponse error = new ValidationErrorResponse();
		
		for (ConstraintViolation violation : e.getConstraintViolations()) {
			
			activityService.createActivityObject(action,ERROR_STR, "Constraint no cumplida: "+violation.getPropertyPath().toString(), true);
			
			error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return error;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMethodArgumentNotValidException(WebRequest request, MethodArgumentNotValidException e) {
		
		String action = extractAction(request);
		
		
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			activityService.createActivityObject(action,ERROR_STR, "Dato no valido en: "+fieldError.getField(), true);
			error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return error;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMethodArgumentTypeMismatchException(WebRequest request, MethodArgumentTypeMismatchException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();

		String action = extractAction(request);
		
		activityService.createActivityObject(action, ERROR_STR, messages.get("error.datatype"), true);
		
		error.getViolations().add(new Violation(e.getName(), "Error en tipo de dato"));

		return error;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMissingServletRequestParameterException(WebRequest request,MissingServletRequestParameterException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		
		String action = extractAction(request);
		
		activityService.createActivityObject(action, ERROR_STR,  messages.get("error.parameter.missing"), true);
		
		error.getViolations().add(new Violation(e.getParameterName(), messages.get("error.parameter.missing")));
		return error;
	}

	@ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onUnsatisfiedServletRequestParameterException(WebRequest request,
			UnsatisfiedServletRequestParameterException e) {
		
		String action = extractAction(request);

		UnsatisfiedServletRequestParameterExceptionTenpo tenpoex = new UnsatisfiedServletRequestParameterExceptionTenpo(
				e.getParamConditionGroups(), e.getActualParams());
		activityService.createActivityObject(action, ERROR_STR,  messages.get("error.parameter.general"), true);
		ValidationErrorResponse error = new ValidationErrorResponse();
		error.getViolations().add(new Violation("Error en parametros", tenpoex.getMessage()));

		return error;
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onAuthenticationException(WebRequest request, AuthenticationException e) {
		
		String action = extractAction(request);

		activityService.createActivityObject(action, ERROR_STR, "Error de auntenticacion", false);
		ValidationErrorResponse error = new ValidationErrorResponse();
		error.getViolations().add(new Violation("Error validacion", "Usuario/password invalidos"));

		return error;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public ValidationErrorResponse handleNotFoundError(WebRequest request, NoHandlerFoundException exception) {

		String action = extractAction(request);

		ValidationErrorResponse error = new ValidationErrorResponse();
		activityService.createActivityObject(action, ERROR_STR, messages.get("error.method.notfound"), false);

		error.getViolations().add(new Violation(messages.get("error.method.notfound"), "path: "+action));
		
		return error;
	}
	
	
	@ExceptionHandler(value = UserAlreadyExistException.class)
	public ResponseEntity<String> handleBlogAlreadyExistsException(UserAlreadyExistException blogAlreadyExistsException) {
		activityService.createActivityObject("register", "ERROR", messages.get("signuperror.error.userexists"), false);

		return new ResponseEntity<String>(messages.get("signuperror.error.userexists"), HttpStatus.CONFLICT);
	}
	
	
	private String extractAction(WebRequest request) {
		String url = ((ServletWebRequest) request).getRequest().getRequestURL().toString();

		int lastIndex = url.lastIndexOf("/");
		url = url.substring(lastIndex + 1, url.length());
		
		return url;
	}
}
