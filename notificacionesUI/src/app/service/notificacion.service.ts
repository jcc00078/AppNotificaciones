import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  baseURL: string = 'https://localhost:8080/notificacion';

  constructor(private httpClient: HttpClient) {}

  getAll(): Observable<Notification[]> {
    return this.httpClient.get<Notification[]>(this.baseURL);
  }
  crearNotificacion(notificacion: Notification): Observable<Notification> {
    return this.httpClient.post<Notification>(this.baseURL, notificacion);
  }
  actualizarNotificacion(notificacion: Notification): Observable<Notification> {
    return this.httpClient.put<Notification>(this.baseURL, notificacion);
  }
  borrarNotificacion(id: string): Observable<any> {
    return this.httpClient.delete<void>(this.baseURL + 'delete/' + id);
  }
  escucharEventos(
    guardado: (event: any) => void,
    borrado: (event: any) => void
  ): EventSource {
    const eventSource = new EventSource(this.baseURL + '/eventos');
    eventSource.addEventListener('NotificacionGuardada', (event) => {
      guardado(JSON.parse(event.data));
    });
    eventSource.addEventListener('NotificacionBorrada', (event) => {
      borrado(JSON.parse(event.data));
    });
    eventSource.onerror = (error) => {
      console.log(error);
    };
    return eventSource;
  }
}
