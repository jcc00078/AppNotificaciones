import { Component, OnInit } from '@angular/core';
import { NotificacionService } from '../service/notificacion.service';
import { Notificacion, TiposNotificacion } from '../model/notificacion';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  mapaNotificaciones = new Map<String, Notificacion[]>(); // Para desplegar en la vista las notificaciones según su tipo
  private eventSource!: EventSource; //Con ! indicamos que la variable se inicializará en algún momento
  TiposNotificacion = TiposNotificacion;
  display: boolean = false;
  nuevaNotificacion: Notificacion = {
    mensaje: '',
    tipo: 'INFO',
  };
  edit: boolean = false;
  aux: boolean = false;

  constructor(
    private notificacionServicio: NotificacionService,
    private messageService: MessageService
  ) {}
  ngOnInit(): void {
    //Ejecuta lo que hay dentro cuando se inicia el componente
    this.getAll();
    this.iniciarEscuchaEventos();
  }
  ngOnDestroy(): void {
    this.pararEscuchaEventos();
  }
  mostrarChat() {
    this.edit = false;
    this.nuevaNotificacion = {
      mensaje: '',
      tipo: 'INFO',
    };
    this.display = true;
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
    return Object.keys(TiposNotificacion);
  }
  getTextoTipos(key: string) {
    return TiposNotificacion[key as keyof typeof TiposNotificacion];
  }

  editarChat(notificacion: Notificacion) {
    this.aux=true;
    this.nuevaNotificacion = notificacion;
    this.display = true;
  }

  crearNotificacion(notificacion: Notificacion) {
    this.aux=false;
    this.notificacionServicio.crearNotificacion(notificacion).subscribe(() => {
      this.display = false;
      //this.edit = false;
    });
  }

  actualizarNotificacion(notificacion: Notificacion, tipo: string) {
    notificacion.tipo = tipo;
    this.notificacionServicio
      .actualizarNotificacion(notificacion)
      .subscribe();
  }

  borrarNotificacion(id: string) {
    this.notificacionServicio.borrarNotificacion(id).subscribe();
  }

  private despuesDeGuardarNotificacion(event: any) {
    this.messageService.add({
      severity: 'info',
      summary: 'Aviso',
      detail: 'Nuevos cambios',
    });
    const notificacion = event.notificacion;
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
      (event) => this.despuesDeGuardarNotificacion(event),
      (event) => this.despuesDeBorrarNotificacion(event)
    );
  }
  private pararEscuchaEventos() {
    this.eventSource.close();
  }
}
