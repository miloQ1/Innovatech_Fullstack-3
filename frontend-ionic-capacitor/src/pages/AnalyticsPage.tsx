import { useEffect, useMemo, useState } from 'react';
import {
  IonBadge,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonIcon,
  IonProgressBar,
  IonSpinner,
  IonText,
} from '@ionic/react';
import { analyticsOutline, businessOutline, folderOpenOutline, peopleOutline, speedometerOutline } from 'ionicons/icons';
import { clientService, projectService } from '../api/projectService';
import { assignmentService, professionalService } from '../api/resourcesService';
import type { Client, Project } from '../types/projects';
import type { Assignment, Professional } from '../types/resources';
import { getProfessionalId, getProjectId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

function progressValue(value: number) {
  if (!Number.isFinite(value)) return 0;
  return Math.max(0, Math.min(1, value / 100));
}

function professionalName(professional?: Professional) {
  if (!professional) return 'Recurso no encontrado';
  const name = `${professional.firstName ?? ''} ${professional.lastName ?? ''}`.trim();
  return name || professional.email || `Recurso #${getProfessionalId(professional as Professional & Record<string, unknown>) ?? ''}`;
}

export function AnalyticsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      setError(null);
      const [projectResult, clientResult, professionalResult, assignmentResult] = await Promise.allSettled([
        projectService.getAll(),
        clientService.getAll(),
        professionalService.getAll(),
        assignmentService.getAll(),
      ]);

      setProjects(projectResult.status === 'fulfilled' ? projectResult.value : []);
      setClients(clientResult.status === 'fulfilled' ? clientResult.value : []);
      setProfessionals(professionalResult.status === 'fulfilled' ? professionalResult.value : []);
      setAssignments(assignmentResult.status === 'fulfilled' ? assignmentResult.value : []);

      if ([projectResult, clientResult, professionalResult, assignmentResult].some((result) => result.status === 'rejected')) {
        setError('Algunos indicadores no se pudieron calcular porque faltó respuesta de un microservicio.');
      }
      setLoading(false);
    };

    load();
  }, []);

  const professionalsById = useMemo(() => new Map(
    professionals
      .map((item) => [getProfessionalId(item as Professional & Record<string, unknown>), item] as const)
      .filter(([id]) => !!id),
  ), [professionals]);

  const projectsById = useMemo(() => new Map(
    projects
      .map((item) => [getProjectId(item as Project & Record<string, unknown>), item] as const)
      .filter(([id]) => !!id),
  ), [projects]);

  const activeProjects = projects.filter((project) => project.status === 'IN_PROGRESS').length;
  const completedProjects = projects.filter((project) => project.status === 'COMPLETED').length;
  const activeClients = clients.filter((client) => client.status === 'ACTIVE').length;
  const activeAssignments = assignments.filter((assignment) => assignment.assignmentStatus !== 'CANCELLED' && assignment.assignmentStatus !== 'COMPLETED');
  const plannedHours = assignments.reduce((sum, item) => sum + (item.plannedHours ?? 0), 0);
  const allocationTotal = activeAssignments.reduce((sum, item) => sum + (item.allocationPct ?? 0), 0);
  const averageAllocation = professionals.length ? Math.round(allocationTotal / professionals.length) : 0;
  const averageProgress = projects.length ? Math.round(projects.reduce((sum, item) => sum + (item.progressPct ?? 0), 0) / projects.length) : 0;

  const assignmentsByResource = useMemo(() => {
    const rows = new Map<number, { resourceId: number; allocation: number; hours: number; count: number }>();
    activeAssignments.forEach((assignment) => {
      const current = rows.get(assignment.resourceId) ?? { resourceId: assignment.resourceId, allocation: 0, hours: 0, count: 0 };
      current.allocation += assignment.allocationPct ?? 0;
      current.hours += assignment.plannedHours ?? 0;
      current.count += 1;
      rows.set(assignment.resourceId, current);
    });
    return Array.from(rows.values()).sort((a, b) => b.allocation - a.allocation);
  }, [activeAssignments]);

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Calculando analítica...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title"><IonIcon icon={analyticsOutline} />Analítica y KPIs</h1>
          <p className="page-subtitle">Panel de indicadores construido desde Proyectos, Clientes, Recursos y Asignaciones.</p>
        </div>
      </div>

      {error && <IonText color="warning"><p>{error}</p></IonText>}

      <div className="stat-grid">
        <IonCard className="app-card">
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-indigo"><IonIcon icon={folderOpenOutline} /></div>
            <div className="stat-body"><p className="stat-value">{projects.length}</p><p className="stat-label">Proyectos totales</p></div>
          </IonCardContent>
        </IonCard>
        <IonCard className="app-card">
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-green"><IonIcon icon={businessOutline} /></div>
            <div className="stat-body"><p className="stat-value">{activeClients}</p><p className="stat-label">Clientes activos</p></div>
          </IonCardContent>
        </IonCard>
        <IonCard className="app-card">
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-blue"><IonIcon icon={peopleOutline} /></div>
            <div className="stat-body"><p className="stat-value">{professionals.length}</p><p className="stat-label">Recursos registrados</p></div>
          </IonCardContent>
        </IonCard>
        <IonCard className="app-card">
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-amber"><IonIcon icon={speedometerOutline} /></div>
            <div className="stat-body"><p className="stat-value">{averageAllocation}%</p><p className="stat-label">Asignación promedio</p></div>
          </IonCardContent>
        </IonCard>
      </div>

      <div className="card-grid ion-margin-top">
        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle>Estado general de proyectos</IonCardTitle></IonCardHeader>
          <IonCardContent>
            <div className="list-row"><span>En progreso</span><IonBadge color="primary">{activeProjects}</IonBadge></div>
            <div className="list-row"><span>Completados</span><IonBadge color="success">{completedProjects}</IonBadge></div>
            <div className="list-row"><span>Avance promedio</span><strong>{averageProgress}%</strong></div>
            <IonProgressBar value={progressValue(averageProgress)} />
          </IonCardContent>
        </IonCard>

        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle>Capacidad comprometida</IonCardTitle></IonCardHeader>
          <IonCardContent>
            <div className="list-row"><span>Asignaciones activas</span><strong>{activeAssignments.length}</strong></div>
            <div className="list-row"><span>Horas planificadas</span><strong>{plannedHours}h</strong></div>
            <div className="list-row"><span>Promedio de uso</span><strong>{averageAllocation}%</strong></div>
            <IonProgressBar value={progressValue(averageAllocation)} />
          </IonCardContent>
        </IonCard>
      </div>

      <IonCard className="app-card ion-margin-top">
        <IonCardHeader><IonCardTitle>Utilización por recurso</IonCardTitle></IonCardHeader>
        <IonCardContent>
          {assignmentsByResource.length === 0 ? <p className="muted">Todavía no hay asignaciones activas.</p> : assignmentsByResource.map((row) => (
            <div key={row.resourceId} className="list-row">
              <div>
                <strong>{professionalName(professionalsById.get(row.resourceId))}</strong>
                <p className="muted">{row.count} asignaciones · {row.hours}h planificadas</p>
              </div>
              <div style={{ minWidth: 180 }}>
                <IonBadge color={row.allocation > 100 ? 'danger' : row.allocation === 100 ? 'success' : 'primary'}>{row.allocation}%</IonBadge>
                <IonProgressBar value={progressValue(row.allocation)} />
              </div>
            </div>
          ))}
        </IonCardContent>
      </IonCard>

      <IonCard className="app-card ion-margin-top">
        <IonCardHeader><IonCardTitle>Asignaciones recientes</IonCardTitle></IonCardHeader>
        <IonCardContent>
          {assignments.slice(0, 8).length === 0 ? <p className="muted">No hay asignaciones registradas.</p> : assignments.slice(0, 8).map((assignment) => (
            <div key={assignment.assignmentId} className="list-row">
              <div>
                <strong>{professionalName(professionalsById.get(assignment.resourceId))}</strong>
                <p className="muted">{projectsById.get(assignment.projectId)?.name ?? `Proyecto #${assignment.projectId}`} · {assignment.projectRole ?? 'Sin rol'}</p>
              </div>
              <IonBadge>{formatStatus(assignment.assignmentStatus ?? 'ACTIVE')}</IonBadge>
            </div>
          ))}
        </IonCardContent>
      </IonCard>
    </div>
  );
}
