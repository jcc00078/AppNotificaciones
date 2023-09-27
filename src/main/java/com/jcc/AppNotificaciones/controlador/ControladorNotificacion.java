package com.jcc.AppNotificaciones.controlador;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jcc.AppNotificaciones.evento.Evento;
import com.jcc.AppNotificaciones.modelo.Notificacion;
import com.jcc.AppNotificaciones.servicio.ServicioNotificacion;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin("http://localhost:4200") //Ya que se va utilizar como cliente Angular
public class ControladorNotificacion {
	@Autowired
	ServicioNotificacion servicioNotificacion;

	/**
	 * Función para obtener todos los documentos de nuestra colección
	 * @return Flux<Notificacion> Devuelve todos los documentos
	 */
	@GetMapping
	public Flux<Notificacion> getAll(){
		return servicioNotificacion.getAll();
	}

	/**
	 * Función para crear una notificación 
	 * @param notificacion Notificación que va a ser creada
	 * @return Mono<Notificacion> Representa la notificación creada 
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED) 
	public Mono<Notificacion> crearNotificacion(@RequestBody Notificacion notificacion){
		return servicioNotificacion.save(notificacion);
	}

	/**
	 * Función para actualizar una notificación 
	 * @param notificacion Notificación que va a ser actualizada
	 * @return Mono<Notificacion> Representa la notificación actualizada 
	 */
	@PutMapping
	@ResponseStatus(code = HttpStatus.OK) 
	public Mono<Notificacion> actualizarNotificacion(@RequestBody Notificacion notificacion){
		return servicioNotificacion.actualizarTipoNotificacion(notificacion, notificacion.getTipo());
	}

	/**
	 * Función para borrar una notificación 
	 * @param id Identificador de la notificación que va a ser borrada
	 * @return Mono<Void> Representa la notificación borrada 
	 */
	@DeleteMapping("/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT) 
	public Mono<Void> borrarNotificacion(@PathVariable final String id ){
		return servicioNotificacion.deleteById(id);
	}

	/**
	 * Función para escuchar cambios de eventos.
	 * Toma un flujo de eventos del servicio y lo transforma en objetos SSE 
	 * configurados adecuadamente y los emite en un flujo de eventos SSE que puede ser consumido por un cliente (navegador web)
	 * para recibir actualizaciones o notificaciones en tiempo real desde el servidor.
	 * @return Flux<ServerSentEvent<Evento>> Devuelve un flujo de eventos SSE
	 */
	@GetMapping("/eventos")
	public Flux<ServerSentEvent<Evento>> getFlujoEventos(){
		return servicioNotificacion //Nuestro servicio está configurado para escuchar eventos y nos proporciona un flujo de eventos
				.escucharEventos() //Este flujo de eventos es lo que se va a mapear y transformar en eventos SSE
				.map(evento -> ServerSentEvent // Cada objeto de tipo Evento emitido por el flujo lo transformamos en SSE
						.<Evento>builder() //Este objeto SSE representa un evento que se enviará al cliente
						.retry(Duration.ofSeconds(4)) //Si se pierde la conexión, el cliente intentará reconectar cada 4 segundos como tiempo de espera
						.event(evento.getClass().getSimpleName()) //Nombre del evento
						.data(evento) //Esto establece los datos del evento, que en este caso son el objeto Evento en sí
						.build()); //construimos y devolvemos el objeto SSE configurado
	}


}
