export interface Notificacion {
  id?: string;
  mensaje: string;
  tipo: string;
}

export enum tiposNotificaciones {
  SUCCESS = 'Resuelta',
  WARN = 'En progreso',
  INFO = 'Nueva'
}
