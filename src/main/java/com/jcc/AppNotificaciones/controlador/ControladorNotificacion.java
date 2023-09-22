package com.jcc.AppNotificaciones.controlador;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin("http://localhost:4200") //Ya que se va utilizar como cliente Angular
public class ControladorNotificacion {

}
