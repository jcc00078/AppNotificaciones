package com.jcc.AppNotificaciones.repositorio;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jcc.AppNotificaciones.modelo.Notificacion;

public interface NotificacionRepositorio extends ReactiveMongoRepository<Notificacion, String> {

}
