import { useHistory } from 'react-router-dom';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle } from '@ionic/react';
import type { Project } from '../../types/projects';
import { blurActiveElement, getProjectId } from '../../utils/ids';
import { formatStatus } from '../../utils/formatStatus';

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
    <IonCard className="app-card accent-card clickable" onClick={navigateToDetail}>
      <IonCardHeader>
        <div className="card-title-row">
          <IonCardTitle className="card-title-main">{project.name}</IonCardTitle>
          <IonBadge color={project.status === 'COMPLETED' ? 'success' : project.status === 'CANCELLED' ? 'danger' : project.status === 'ON_HOLD' ? 'warning' : 'primary'}>
            {formatStatus(project.status)}
          </IonBadge>
        </div>
      </IonCardHeader>
      <IonCardContent>
        <div className="card-info-list">
          <span className="card-info-row">#{project.code} · {clientName}</span>
          {project.description && <span className="card-info-row">{project.description}</span>}
        </div>
        <div className="card-actions">
          <IonButton
            type="button"
            size="small"
            fill="solid"
            color="primary"
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
