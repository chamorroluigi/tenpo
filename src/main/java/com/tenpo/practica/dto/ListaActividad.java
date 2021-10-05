package com.tenpo.practica.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tenpo.practica.entity.Activity;

public class ListaActividad {
	
	Integer total;
	
	
	Page<Activity>lista;


	public ListaActividad(Integer total, Page<Activity> lista) {
		this.total = total;
		this.lista = lista;
	}
	
	public Integer getTotal() {
		return total;
	}


	public void setTotal(Integer total) {
		this.total = total;
	}

	public Page<Activity> getLista() {
		return lista;
	}

	public void setLista(Page<Activity> lista) {
		this.lista = lista;
	}



}
