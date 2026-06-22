import { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import {
  IonBadge,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonIcon,
  IonSpinner,
  IonText,
} from '@ionic/react';
import {
  businessOutline,
  flashOutline,
  folderOpenOutline,
  peopleOutline,
  syncOutline,
} from 'ionicons/icons';
import { clientService, projectService } from '../api/projectService';
import { professionalService } from '../api/resourcesService';
import { useAuth } from '../hooks/useAuth';
import type { Client, Project } from '../types/projects';
import { getClientId, getProjectId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

export function DashboardPage() {
  const { user } = useAuth();
  const history = useHistory();
  const [projects, setProjects] = useState<Project[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [professionalsCount, setProfessionalsCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true);
      setError(null);
      try {
        const [projectResult, clientResult, professionalResult] = await Promise.allSettled([
          projectService.getAll(),
          clientService.getAll(),
          professionalService.getAll(),
        ]);

        setProjects(projectResult.status === 'fulfilled' ? projectResult.value : []);
        setClients(clientResult.status === 'fulfilled' ? clientResult.value : []);
        setProfessionalsCount(professionalResult.status === 'fulfilled' ? professionalResult.value.length : 0);

        if (projectResult.status === 'rejected' || clientResult.status === 'rejected' || professionalResult.status === 'rejected') {
          setError('Algunos datos no se pudieron cargar. Revisa que el BFF y los microservicios estén levantados.');
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'No se pudo cargar el dashboard');
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const activeClients = clients.filter((client) => client.status === 'ACTIVE');
  const inProgress = projects.filter((project) => project.status === 'IN_PROGRESS').length;
  const completed = projects.filter((project) => project.status === 'COMPLETED').length;
  const latestProjects = projects.slice(0, 4);
  const latestClients = clients.slice(0, 4);

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando dashboard...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Hola, {user?.firstName ?? user?.userName ?? 'usuario'} 👋</h1>
          <p className="page-subtitle">Panel principal para navegar, crear y revisar información del sistema.</p>
        </div>
        <div className="button-row">
          <IonButton routerLink="/clients/create" routerDirection="forward">+ Cliente</IonButton>
          <IonButton fill="outline" routerLink="/projects" routerDirection="forward">+ Proyecto</IonButton>
        </div>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}

      <div className="stat-grid">
        <IonCard className="app-card clickable" onClick={() => history.push('/projects')}>
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-indigo"><IonIcon icon={folderOpenOutline} /></div>
            <div className="stat-body">
              <p className="stat-value">{projects.length}</p>
              <p className="stat-label">Total proyectos</p>
            </div>
          </IonCardContent>
        </IonCard>
        <IonCard className="app-card clickable" onClick={() => history.push('/projects')}>
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-amber"><IonIcon icon={syncOutline} /></div>
            <div className="stat-body">
              <p className="stat-value">{inProgress}</p>
              <p className="stat-label">En progreso</p>
            </div>
          </IonCardContent>
        </IonCard>
        <IonCard className="app-card clickable" onClick={() => history.push('/clients')}>
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-green"><IonIcon icon={businessOutline} /></div>
            <div className="stat-body">
              <p className="stat-value">{activeClients.length}</p>
              <p className="stat-label">Clientes activos</p>
            </div>
          </IonCardContent>
        </IonCard>
        <IonCard className="app-card clickable" onClick={() => history.push('/resources')}>
          <IonCardContent className="stat-card">
            <div className="icon-badge tone-blue"><IonIcon icon={peopleOutline} /></div>
            <div className="stat-body">
              <p className="stat-value">{professionalsCount}</p>
              <p className="stat-label">Profesionales</p>
            </div>
          </IonCardContent>
        </IonCard>
      </div>

      <IonCard className="app-card">
        <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={flashOutline} />Acciones rápidas</IonCardTitle></IonCardHeader>
        <IonCardContent className="button-row">
          <IonButton routerLink="/projects" routerDirection="forward">Ver proyectos</IonButton>
          <IonButton fill="outline" routerLink="/clients" routerDirection="forward">Ver clientes</IonButton>
          <IonButton fill="outline" routerLink="/resources" routerDirection="forward">Gestionar recursos</IonButton>
          <IonButton fill="outline" routerLink="/collaboration" routerDirection="forward">Colaboración</IonButton>
          <IonButton fill="outline" routerLink="/analytics" routerDirection="forward">Analítica</IonButton>
          <IonButton fill="outline" routerLink="/notifications" routerDirection="forward">Notificaciones</IonButton>
          <IonButton fill="outline" routerLink="/clients/create" routerDirection="forward">Crear cliente</IonButton>
        </IonCardContent>
      </IonCard>

      <div className="card-grid ion-margin-top">
        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={folderOpenOutline} />Proyectos recientes</IonCardTitle></IonCardHeader>
          <IonCardContent>
            {latestProjects.length === 0 ? (
              <>
                <p className="muted">Aún no hay proyectos creados.</p>
                <IonButton size="small" routerLink="/projects">Crear proyecto</IonButton>
              </>
            ) : latestProjects.map((project) => (
              <div key={getProjectId(project as Project & Record<string, unknown>) ?? project.code} className="list-row clickable" onClick={() => { const projectId = getProjectId(project as Project & Record<string, unknown>); if (projectId) history.push(`/projects/${projectId}`); }}>
                <div>
                  <strong>{project.name}</strong>
                  <p className="muted">#{project.code}</p>
                </div>
                <IonBadge>{formatStatus(project.status)}</IonBadge>
              </div>
            ))}
          </IonCardContent>
        </IonCard>

        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={businessOutline} />Clientes recientes</IonCardTitle></IonCardHeader>
          <IonCardContent>
            {latestClients.length === 0 ? (
              <>
                <p className="muted">Aún no hay clientes registrados.</p>
                <IonButton size="small" routerLink="/clients/create">Crear cliente</IonButton>
              </>
            ) : latestClients.map((client) => (
              <div key={getClientId(client as Client & Record<string, unknown>) ?? client.name} className="list-row clickable" onClick={() => { const clientId = getClientId(client as Client & Record<string, unknown>); if (clientId) history.push(`/clients/${clientId}`); }}>
                <div>
                  <strong>{client.name}</strong>
                  <p className="muted">{client.industry ?? 'Sin industria'}</p>
                </div>
                <IonBadge color={client.status === 'ACTIVE' ? 'success' : 'medium'}>{formatStatus(client.status)}</IonBadge>
              </div>
            ))}
          </IonCardContent>
        </IonCard>
      </div>

      {completed > 0 && <p className="muted ion-margin-top">Proyectos completados: {completed}</p>}
    </div>
  );
}
