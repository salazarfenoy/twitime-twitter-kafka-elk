package com.iesalandalus.proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class TweetFromKafka {

	
	private String fecha;
	private String usuario;
	private String texto;
	private String imagen;
	private int followers;
	private String nombre;

	public TweetFromKafka() {

	}

	public TweetFromKafka(TweetFromKafka t) {

		setFecha(t.fecha);
		setUsuario(t.usuario);
		setTexto(t.texto);
		setImagen(t.imagen);
		setFollowers(t.followers);
		setNombre(t.nombre);
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {

		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
