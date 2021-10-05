package com.tenpo.practica.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tenpo.practica.dto.ActivityDto;
import com.tenpo.practica.dto.ListaActividad;
import com.tenpo.practica.entity.Activity;
import com.tenpo.practica.repository.ActivityRepository;


@Service
public class ActivityServiceImpl implements IActivityService {

	
	@Autowired
	ActivityRepository activityRepository;
	
	@Autowired
	private ISecurityService securityService;
	
	@Override
	public Activity createActivityInDb(ActivityDto activityDto) {
		
		Activity activity = new Activity();
		
		activity.setActivity(activityDto.getActivity());
		activity.setDate(new Date());
		activity.setStatus("OK");
		activity.setUsername(activityDto.getUsername());
		
		Activity newActivity = activityRepository.save(activity);
		
		return newActivity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListaActividad listAllActivity(Pageable page) {
		Page<Activity> list = activityRepository.findAll(page);
		
		ListaActividad listaAct = new ListaActividad(0,  list) ;
				
		if(list!=null && !list.isEmpty())
			System.out.println("Lista vacia");
		else {
			
			listaAct.setLista(list);
			listaAct.setTotal(list.getSize());
		
			System.out.println("Lista con datos");
			
			return listaAct;
		}
		return listaAct;
	}


	@Override
	public Activity createActivityObject(String activity, String status, String description, Boolean isLoged) {
		
		Activity act = new Activity();
		
		act.setActivity(activity);
		act.setDate(new Date());
		act.setStatus(status);
		act.setDescription(description);
		
	
		if(isLoged) {
			act.setUsername(securityService.getUsername());
		} else {
			act.setUsername("Annonymus");
		}	
		
		return activityRepository.save(act);

	}



}
