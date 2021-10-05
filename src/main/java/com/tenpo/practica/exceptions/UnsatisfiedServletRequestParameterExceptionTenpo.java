package com.tenpo.practica.exceptions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;

import com.tenpo.practica.services.ActivityServiceImpl;
import com.tenpo.practica.services.IActivityService;


public class UnsatisfiedServletRequestParameterExceptionTenpo extends UnsatisfiedServletRequestParameterException {



	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String[]> paramConditions;
	
	Map<String, String[]> actualParams;
	
	
	public UnsatisfiedServletRequestParameterExceptionTenpo(List<String[]> paramConditions,
			Map<String, String[]> actualParams) {
		super(paramConditions, actualParams);
		
		this.paramConditions = paramConditions;
		this.actualParams = actualParams;
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getMessage() {
	  StringBuilder sb = new StringBuilder("Algunos de los siguientes parametros requeridos:  ");
	  int i = 0;
	  for (String[] conditions : this.paramConditions) {
	    if (i > 0) {
	      sb.append(" O ");
	    }
	    sb.append("\"");
	    sb.append(StringUtils.arrayToDelimitedString(conditions, ", "));
	    sb.append("\"");
	    i++;
	  }
	  sb.append(" no esta presente o no cumple condiciones ");
	  //sb.append(requestParameterMapToString(this.actualParams));
	  
	  
	  return sb.toString();
	}
	
	
	private static String requestParameterMapToString(Map<String, String[]> actualParams) {
		StringBuilder result = new StringBuilder();
		for (Iterator<Map.Entry<String, String[]>> it = actualParams.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String[]> entry = it.next();
			result.append(entry.getKey()).append('=').append(ObjectUtils.nullSafeToString(entry.getValue()));
			if (it.hasNext()) {
				result.append(", ");
			}
		}
		return result.toString();
	}
	
	public void setParamConditions(List<String[]> paramConditions) {
		this.paramConditions = paramConditions;
	}

	public void setActualParams(Map<String, String[]> actualParams) {
		this.actualParams = actualParams;
	}
}
