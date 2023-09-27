import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {Notificacion} from '../model/notificacion'
@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  baseURL: string = 'http://localhost:8080/notificacion';

  constructor(private httpClient: HttpClient) {}

  getAll(): Observable<Notificacion[]> {
    return this.httpClient.get<Notificacion[]>(this.baseURL);
  }
  crearNotificacion(notificacion: Notificacion): Observable<Notificacion> {
    return this.httpClient.post<Notificacion>(this.baseURL, notificacion);
  }
  actualizarNotificacion(notificacion: Notificacion): Observable<Notificacion> {
    return this.httpClient.put<Notificacion>(this.baseURL, notificacion);
  }
  borrarNotificacion(id: string): Observable<any> {
    return this.httpClient.delete<void>(this.baseURL + '/delete/' + id);
  }
  escucharEventos(
    guardado: (event: any) => void,
    borrado: (event: any) => void
  ): EventSource {
    const eventSource = new EventSource(this.baseURL + '/eventos');
    eventSource.addEventListener('NotificacionGuardada', (event: MessageEvent) => {
      guardado(JSON.parse(event.data));
    });
    eventSource.addEventListener('NotificacionBorrada', (event: MessageEvent) => {
      borrado(JSON.parse(event.data));
    });
    eventSource.onerror = (error) => {
      console.log(error);
    };
    return eventSource;
  }
}
