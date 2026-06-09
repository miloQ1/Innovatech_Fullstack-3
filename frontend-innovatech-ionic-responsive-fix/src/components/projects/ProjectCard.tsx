import { useHistory } from 'react-router-dom';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle } from '@ionic/react';
import type { Project } from '../../types/projects';
import { blurActiveElement, getProjectId } from '../../utils/ids';

interface ProjectCardProps {
  project: Project;
}

export function ProjectCard({ project }: ProjectCardProps) {
  const history = useHistory();
  const projectId = getProjectId(project as Project & Record<string, unknown>);
  const clientName = project.client?.name ?? (project.clientId ? `Cliente #${project.clientId}` : 'Sin cliente');

  const navigateToDetail = () => {
    if (!projectId) return;
    blurActiveElement();
    history.push(`/projects/${projectId}`);
  };

  return (
    <IonCard className="app-card clickable" onClick={navigateToDetail}>
      <IonCardHeader>
        <IonCardTitle>{project.name}</IonCardTitle>
        <IonCardSubtitle>#{project.code} · {clientName}</IonCardSubtitle>
      </IonCardHeader>
      <IonCardContent>
        <p className="muted">{project.description ?? 'Sin descripción'}</p>
        <div className="button-row">
          <IonBadge color={project.status === 'COMPLETED' ? 'success' : project.status === 'CANCELLED' ? 'danger' : project.status === 'ON_HOLD' ? 'warning' : 'primary'}>
            {project.status.replace(/_/g, ' ')}
          </IonBadge>
          <IonButton
            type="button"
            size="small"
            fill="outline"
            disabled={!projectId}
            onClick={(event) => {
              event.stopPropagation();
              navigateToDetail();
            }}
          >
            Ver detalle
          </IonButton>
        </div>
      </IonCardContent>
    </IonCard>
  );
}
