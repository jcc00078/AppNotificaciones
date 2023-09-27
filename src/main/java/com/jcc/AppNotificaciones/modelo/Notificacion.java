package com.jcc.AppNotificaciones.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document(collection="notificaciones")
@Data
@Getter
@Setter
public class Notificacion {
	@Id 
	private String id;
	private String mensaje;
	private String tipo;
	
	
	public Notificacion(String mensaje, String tipo) {
		this.mensaje = mensaje;
		this.tipo = tipo;
	}

}
