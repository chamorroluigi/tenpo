package com.tenpo.practica.exceptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.practica.services.IActivityService;

@Component
public class CustomForbiddenEntryPoint implements AuthenticationEntryPoint {

	
	@Autowired
	private IActivityService activityService;
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
         AuthenticationException authException) throws IOException, ServletException {
    	 activityService.createActivityObject(request.getRequestURI().replace("/",""), "ERROR",
 				"No esta autorizado", false);
    	
        HashMap<String, String> map = new HashMap<>(2);
        map.put("uri", request.getRequestURI());
        map.put("msg", "Usted no esta autorizado");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(map);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }

}
