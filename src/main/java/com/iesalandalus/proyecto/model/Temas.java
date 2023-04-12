package com.iesalandalus.proyecto.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class Temas {

	public Tema tema;
	public List<Tema> temas;

	public Temas() {
		temas = new ArrayList<>();
	}

	public List<Tema> getAll() {

		return temas;
	}

	public String getTemasString() {
		StringBuilder str = new StringBuilder();
		for (Tema t : temas) {
			str.append("," + t.getNombre());
		}

		str.delete(0, 1);
		return str.toString();

	}

}
