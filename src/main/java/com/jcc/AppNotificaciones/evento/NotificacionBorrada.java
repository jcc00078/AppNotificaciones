package com.jcc.AppNotificaciones.evento;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data //Generacion automática de metodos comunes
@Getter
@Setter
@Accessors(chain = true)//Encadenar varias llamadas a métodos setter en una sola expresión(más legible)
public class NotificacionBorrada implements Evento {
	private String id;

}
