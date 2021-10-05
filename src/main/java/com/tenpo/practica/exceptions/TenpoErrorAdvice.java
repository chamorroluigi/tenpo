package com.tenpo.practica.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.tenpo.practica.services.IActivityService;
import org.springframework.web.servlet.NoHandlerFoundException;
@ControllerAdvice
public class TenpoErrorAdvice {

	@Autowired
	private IActivityService activityService;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ UserAlreadyExistException.class })
	public void handle(UserAlreadyExistException e) {

	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (ConstraintViolation violation : e.getConstraintViolations()) {
			error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return error;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return error;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();

		error.getViolations().add(new Violation(e.getName(), "Error en tipo de dato"));

		return error;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		error.getViolations().add(new Violation(e.getParameterName(), "Falta parmetro"));
		return error;
	}

	@ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onUnsatisfiedServletRequestParameterException(WebRequest request,
			UnsatisfiedServletRequestParameterException e) {
		String url = ((ServletWebRequest) request).getRequest().getRequestURL().toString();

		int lastIndex = url.lastIndexOf("/");
		url = url.substring(lastIndex + 1, url.length());

		UnsatisfiedServletRequestParameterExceptionTenpo tenpoex = new UnsatisfiedServletRequestParameterExceptionTenpo(
				e.getParamConditionGroups(), e.getActualParams());
		activityService.createActivityObject(url, "ERROR", "Error en parametros de entrada", true);
		ValidationErrorResponse error = new ValidationErrorResponse();
		error.getViolations().add(new Violation("Error en parametros", tenpoex.getMessage()));

		return error;
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onAuthenticationException(WebRequest request, AuthenticationException e) {
		String url = ((ServletWebRequest) request).getRequest().getRequestURL().toString();

		int lastIndex = url.lastIndexOf("/");
		url = url.substring(lastIndex + 1, url.length());

		activityService.createActivityObject(url, "ERROR", "Error de auntenticacion", false);
		ValidationErrorResponse error = new ValidationErrorResponse();
		error.getViolations().add(new Violation("Error validacion", "Usuario/password invalidos"));

		return error;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public ValidationErrorResponse handleNotFoundError(WebRequest request, NoHandlerFoundException exception) {

		String url = ((ServletWebRequest) request).getRequest().getRequestURL().toString();

		int lastIndex = url.lastIndexOf("/");
		url = url.substring(lastIndex + 1, url.length());

		ValidationErrorResponse error = new ValidationErrorResponse();
		activityService.createActivityObject(url, "ERROR", "Metodo no encontrado", false);

		error.getViolations().add(new Violation("Error metodo encontrado", "path: "+url));
		
		return error;
	}
}
