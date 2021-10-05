package com.tenpo.practica.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.tenpo.practica.entity.Activity;

public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long>{
	
	

}
