export interface Notificacion {
  id?: string,
  mensaje: string,
  tipo: string
}

export enum TiposNotificacion {
  INFO = 'Pendiente de revisión',
  WARN = 'En proceso',
  SUCCESS = 'Mantenimiento Completado'
}
