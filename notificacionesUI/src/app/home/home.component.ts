import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificacionService } from '../service/notificacion.service';
import { Notificacion, tiposNotificaciones } from '../model/notificacion';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  mapaNotificaciones = new Map<String, Notificacion[]>(); // Para desplegar en la vista las notificaciones según su tipo
  private eventSource!: EventSource; //Con ! indicamos que la variable se inicializará en algún momento
  constructor(private notificacionServicio: NotificacionService) {}
  ngOnInit(): void {
    //Ejecuta lo que hay dentro cuando se inicia el componente
    this.getAll();
    this.iniciarEscuchaEventos();
  }
  ngOnDestroy(): void {
    this.pararEscuchaEventos();
  }
  getAll() {
    for (const tipo of this.getTipos()) {
      this.mapaNotificaciones.set(tipo, []);
    }
    this.notificacionServicio.getAll().subscribe((listaNotificaciones) => {
      listaNotificaciones.forEach((notificacion) => {
        this.mapaNotificaciones.get(notificacion.tipo)?.push(notificacion); //Aqui no puedo hacer .set ya que entonces solo podría tener una notificación por tipo, por tanto uso push
      });
    });
  }
  getTipos(): Array<string> {
    return Object.keys(tiposNotificaciones);
  }
  borrarNotificacion(id: string) {
    this.notificacionServicio.borrarNotificacion(id).subscribe(); //subscribe() para ser notificado cuando se produzca un evento o se complete la operación asincrona
  }
  crearNotificacion(notificacion: Notificacion) {
    this.notificacionServicio.crearNotificacion(notificacion).subscribe();
  }
  actualizarNotificacion(notificacion: Notificacion) {
    this.notificacionServicio.actualizarNotificacion(notificacion).subscribe();
  }

  private despuesDeGuardarNotificacion(evento: any) {
    const notificacion = evento.notificacion;
    this.eliminarNotificacion(notificacion.id);
    this.mapaNotificaciones.get(notificacion.tipo)?.push(notificacion);
  }
  private eliminarNotificacion(id: string) {
    //Para eliminar una notificacion del mapa
    for (const notificaciones of this.mapaNotificaciones.values()) {
      //Ahora vamos a crear un nuevo array con solo los id de cada notificacion y buscamos la posicion del id que queremos eliminar
      const index = notificaciones.map((n) => n.id).indexOf(id);
      if (index >= 0) {
        //Si hemos encontrado el indice entonces eliminamos 1 elemento
        notificaciones.splice(index, 1);
      }
    }
  }
  private despuesDeBorrarNotificacion(evento: any) {
    this.eliminarNotificacion(evento.id);
  }
  private iniciarEscuchaEventos() {
    this.eventSource = this.notificacionServicio.escucharEventos(
      (e) => this.despuesDeGuardarNotificacion(e),
      (e) => this.despuesDeBorrarNotificacion(e)
    );
  }
  private pararEscuchaEventos() {
    this.eventSource.close();
  }
}
