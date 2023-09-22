package com.jcc.AppNotificaciones.evento;

import com.jcc.AppNotificaciones.modelo.Notificacion;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
@Data //Generacion automática de metodos comunes
@Getter
@Setter
@Accessors(chain = true)//Encadenar varias llamadas a métodos setter en una sola expresión(más legible)
public class NotificacionGuardada implements Evento {
	private Notificacion notificacion;

}
