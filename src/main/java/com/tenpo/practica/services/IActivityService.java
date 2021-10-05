package com.tenpo.practica.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.tenpo.practica.dto.ActivityDto;
import com.tenpo.practica.dto.ListaActividad;
import com.tenpo.practica.entity.Activity;

public interface IActivityService {
	
	
	Activity createActivityInDb(ActivityDto activity);
	
	
	Activity createActivityObject(String activity, String status, String description, Boolean isLoged);
	
	
	ListaActividad listAllActivity(Pageable page);

}
