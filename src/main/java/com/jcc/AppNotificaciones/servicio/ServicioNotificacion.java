package com.jcc.AppNotificaciones.servicio;

import org.bson.BsonObjectId;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.jcc.AppNotificaciones.evento.Evento;
import com.jcc.AppNotificaciones.evento.NotificacionBorrada;
import com.jcc.AppNotificaciones.evento.NotificacionGuardada;
import com.jcc.AppNotificaciones.modelo.Notificacion;
import com.jcc.AppNotificaciones.repositorio.NotificacionRepositorio;
import com.mongodb.client.model.changestream.OperationType;

import lombok.RequiredArgsConstructor;
//Reactor es una biblioteca de programación reactiva para construir aplicaciones que manejan flujos de datos de manera asíncrona
import reactor.core.publisher.Flux; //Flux es una representación de un flujo asincrónico de datos, similar a una secuencia de eventos o valores que pueden emitirse con el tiempo
import reactor.core.publisher.Mono; // Mono representa una fuente de datos asíncrona que emite cero o un elemento

@Service
@RequiredArgsConstructor// Ayuda para constructores
public class ServicioNotificacion {
	private final NotificacionRepositorio notificacionRepositorio;
	private final ReactiveMongoTemplate reactiveMongoTemplate; 
	/**
	 * Función que representa el flujo de notificaciones
	 * @return Flux<Notificacion> Devuelve todos los documentos
	 */
	public Flux<Notificacion> getAll(){
		return notificacionRepositorio.findAll(); //Traemos todos los documentos de nuestra bbdd
	}
	/**
	 * Función para guardar una notificación en la bbdd
	 * @param notificacion Notificación que se va a guardar
	 * @return Mono<Notificacion> Representa la notificación guardada
	 */
	public Mono<Notificacion> save(Notificacion notificacion){
		return notificacionRepositorio.save(notificacion);
	}

	/**
	 * Función que busca un documento por su identificador 
	 * @param id Identificador 
	 * @return Mono<Notificacion> Devuelve un documento
	 */
	public Mono<Notificacion> findById(String id){
		return notificacionRepositorio.findById(id);
	}

	/**
	 * Función para actualizar el tipo de notificación
	 * @param notificacion Notificación 
	 * @param tipo Tipo de notificación
	 * @return Mono<Notificacion> Representa la notificación actualizada
	 */
	public Mono<Notificacion> actualizarTipoNotificacion(Notificacion notificacion, String tipo) {
		return findById(notificacion.getId()).flatMap(notificacion1 ->{
			notificacion1.setTipo(tipo);
			return notificacionRepositorio.save(notificacion1);
		});

	}

	/**
	 * Función para borrar una notificación 
	 * @param id ID de la notificación que se va a borrar
	 * @return Mono<Void> Representa el borrado
	 */
	public Mono<Void> deleteById(String id){
		return findById(id).flatMap(notificacionRepositorio::delete);
	}
	
	/**
	 * Función para tener una instancia de notificación a partir del recurso que nos devuelve la bbdd
	 * @param notificacion Notificacion
	 * @return Notificación Devuelve la notificacion
	 */
	public Notificacion toResource(Notificacion notificacion) {
		return notificacion;
	}
	
	/**
	 * Función que utilizaremos en el controlador para escuchar los eventos de cambio que nos están enviando 
	 */
	public Flux<Evento> listenToEvents(){
		final ChangeStreamOptions changeStreamOptions = 
				ChangeStreamOptions
				.builder() //Esto ha creado una instancia del constructor de ChangeStreamOptions
				.returnFullDocumentOnUpdate() // si hay una actualización en un documento, se obtendrá el documento completo
				.filter(Aggregation.newAggregation( //configuración de filtro para la transmisión de cambios utilizando una agregación de MongoDB
						Aggregation.match(
								Criteria.where("tipoOperacion") //Definimos los tipos de operaciones en los que se producirán los cambios
								.in(OperationType.DELETE.getValue(),
										OperationType.INSERT.getValue(),
										OperationType.UPDATE.getValue(),
										OperationType.REPLACE.getValue()
										)
						)
				))
				.build(); //Creación de instancia final de ChangeStreamOptions con todas las configuraciones anteriores aplicadas
		
		//Por último configuramos una transmisión de cambios en la colección "notificaciones" de la base de datos MongoDB.
		//Utilizando las opciones de configuracion que hemos especificado anteriormente en changeStreamOptions para ver como se van a gestionar los cambios en la coleccion "notificaciones"
		return reactiveMongoTemplate
				.changeStream("notificaciones", changeStreamOptions,Notificacion.class)
				.map(this::toEvent); //Por último, se transforma a un evento y se devuelve
	}

	
	/**
	 * Función que devuelve el evento que se ha producido
	 * @param changeStreamEvent Evento de flujo de cambio que se produce
	 * @return Evento Devuelve el evento
	 */
	public Evento toEvent(final ChangeStreamEvent<Notificacion> changeStreamEvent) {
		final Evento evento;
		switch (changeStreamEvent.getOperationType()) {
		//Sirve para notificar el documento que se ha borrado. Para esto buscamos en la bbdd del documento (nos devuelve el documento)  y buscamos el id 
		case DELETE:
			evento = new NotificacionBorrada()
			.setId(((BsonObjectId) changeStreamEvent
					.getRaw().getDocumentKey().getObjectId("_id")).getValue().toString());
			break;

			//Unificamos los siguientes 3 casos	
		case INSERT: 
		case UPDATE: 
		case REPLACE:
			evento = new NotificacionGuardada().setNotificacion(toResource(changeStreamEvent.getBody())); //Recuperamos la notificacion desde el cuerpo del evento que nos provee la bbdd
			break;
			//Lanzamos excepción si se realiza una operación que no está soportada por nuestra bbdd
		default: 
			throw new UnsupportedOperationException(
					String.valueOf(changeStreamEvent.getOperationType())
					);
		}
		return evento;

	}
}
