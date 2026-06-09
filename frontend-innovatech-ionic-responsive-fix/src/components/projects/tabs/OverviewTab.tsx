import { IonBadge, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonProgressBar } from '@ionic/react';
import type { Phase, Project, Task } from '../../../types/projects';

interface OverviewTabProps {
  project: Project;
  phases: Phase[];
  tasks: Task[];
  onTabChange: (tab: string) => void;
}

function badgeColor(status: string) {
  if (status === 'COMPLETED' || status === 'DONE') return 'success';
  if (status === 'CANCELLED') return 'danger';
  if (status === 'ON_HOLD') return 'warning';
  return 'primary';
}

export function OverviewTab({ project, phases, tasks, onTabChange }: OverviewTabProps) {
  return (
    <>
      <IonCard className="app-card">
        <IonCardHeader><IonCardTitle>📋 Información del proyecto</IonCardTitle></IonCardHeader>
        <IonCardContent>
          <div className="form-grid">
            <p><strong>Nombre:</strong><br />{project.name}</p>
            <p><strong>Código:</strong><br />#{project.code}</p>
            <p><strong>Cliente:</strong><br />{project.client?.name ?? (project.clientId ? `Cliente #${project.clientId}` : 'Sin cliente')}</p>
            <p><strong>Estado:</strong><br /><IonBadge color={badgeColor(project.status)}>{project.status.replace(/_/g, ' ')}</IonBadge></p>
            {project.startDate && <p><strong>Inicio:</strong><br />{project.startDate}</p>}
            {project.endDate && <p><strong>Término:</strong><br />{project.endDate}</p>}
          </div>
          {project.description && <p className="muted">{project.description}</p>}
        </IonCardContent>
      </IonCard>

      <div className="stat-grid">
        <IonCard className="app-card ion-text-center"><IonCardContent><h2>{phases.length}</h2><p>Fases</p></IonCardContent></IonCard>
        <IonCard className="app-card ion-text-center"><IonCardContent><h2>{tasks.length}</h2><p>Tareas</p></IonCardContent></IonCard>
        <IonCard className="app-card ion-text-center"><IonCardContent><h2>{tasks.filter((t) => t.status === 'DONE').length}</h2><p>Completadas</p></IonCardContent></IonCard>
      </div>

      {phases.length > 0 && (
        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle>📊 Avance por fase</IonCardTitle></IonCardHeader>
          <IonCardContent>
            {phases.map((phase) => {
              const phaseTasks = tasks.filter((t) => t.phaseId === phase.phaseId);
              const done = phaseTasks.filter((t) => t.status === 'DONE').length;
              const progress = phaseTasks.length ? done / phaseTasks.length : 0;
              return (
                <IonCard key={phase.phaseId} className="clickable" button onClick={() => onTabChange(String(phase.phaseId))}>
                  <IonCardContent>
                    <div className="page-header" style={{ marginBottom: 8 }}>
                      <strong>{phase.name}</strong>
                      <IonBadge color={badgeColor(String(phase.computedStatus ?? phase.status ?? 'PENDING'))}>{Math.round(progress * 100)}%</IonBadge>
                    </div>
                    <IonProgressBar value={progress} />
                    <p className="muted">{done}/{phaseTasks.length} tareas terminadas</p>
                  </IonCardContent>
                </IonCard>
              );
            })}
          </IonCardContent>
        </IonCard>
      )}
    </>
  );
}
