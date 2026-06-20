const STATUS_MAP: Record<string, string> = {
  ACTIVE: 'Activo',
  INACTIVE: 'Inactivo',
  ON_LEAVE: 'Con licencia',
  PLANNING: 'Planificación',
  IN_PROGRESS: 'En progreso',
  ON_HOLD: 'En pausa',
  COMPLETED: 'Completado',
  CANCELLED: 'Cancelado',
  TODO: 'Por hacer',
  IN_REVIEW: 'En revisión',
  DONE: 'Hecho',
  PENDING: 'Pendiente',
  ACCEPTED: 'Aceptado',
  REJECTED: 'Rechazado',
  LOW: 'Baja',
  MEDIUM: 'Media',
  HIGH: 'Alta',
  CRITICAL: 'Crítica',
  OWNER: 'Propietario',
  MEMBER: 'Miembro',
};

export function formatStatus(status: string): string {
  return STATUS_MAP[status] ?? status.replace(/_/g, ' ');
}
