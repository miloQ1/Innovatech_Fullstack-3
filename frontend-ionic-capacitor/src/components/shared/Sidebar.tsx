import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  IonContent,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenu,
  IonMenuToggle,
} from '@ionic/react';
import { analyticsOutline, businessOutline, chatbubblesOutline, folderOpenOutline, gridOutline, notificationsOutline, peopleOutline } from 'ionicons/icons';
import type { Client, Project } from '../../types/projects';
import { clientService, projectService } from '../../api/projectService';
import { useAuth } from '../../hooks/useAuth';
import { getClientId, getProjectId } from '../../utils/ids';
import { formatStatus } from '../../utils/formatStatus';

const INACTIVE_STATUSES = new Set(['COMPLETED', 'CANCELLED']);

export function Sidebar() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  const [projects, setProjects] = useState<Project[]>([]);
  const [clients, setClients] = useState<Client[]>([]);

  useEffect(() => {
    if (!isAuthenticated) {
      setProjects([]);
      setClients([]);
      return;
    }

    let mounted = true;
    Promise.allSettled([projectService.getAll(), clientService.getAll()]).then(([projectResult, clientResult]) => {
      if (!mounted) return;
      setProjects(projectResult.status === 'fulfilled' ? projectResult.value : []);
      setClients(clientResult.status === 'fulfilled' ? clientResult.value : []);
    });

    return () => { mounted = false; };
  }, [isAuthenticated, location.pathname]);

  if (!isAuthenticated) return null;

  const isActive = (path: string) => location.pathname === path;

  const activeProjects = projects
    .filter((p) => !INACTIVE_STATUSES.has(p.status))
    .slice(0, 5);

  const activeClients = clients
    .filter((c) => c.status === 'ACTIVE')
    .slice(0, 5);

  return (
    <IonMenu contentId="main-content" type="overlay">
      <IonContent>
        <div className="sidebar-brand">
          <span className="sidebar-brand-name">Innovatech</span>
          <span className="sidebar-brand-sub">Gestión de proyectos</span>
        </div>

        <IonList lines="none">
          <p className="sidebar-section-label">Menú</p>
          <IonMenuToggle autoHide>
            <IonItem className={`sidebar-link ${isActive('/dashboard') ? 'active-link' : ''}`} button detail={false} routerLink="/dashboard" routerDirection="root">
              <IonIcon icon={gridOutline} slot="start" />
              <IonLabel>Dashboard</IonLabel>
            </IonItem>
            <IonItem className={`sidebar-link ${isActive('/projects') ? 'active-link' : ''}`} button detail={false} routerLink="/projects" routerDirection="root">
              <IonIcon icon={folderOpenOutline} slot="start" />
              <IonLabel>Proyectos</IonLabel>
            </IonItem>
            <IonItem className={`sidebar-link ${isActive('/clients') ? 'active-link' : ''}`} button detail={false} routerLink="/clients" routerDirection="root">
              <IonIcon icon={businessOutline} slot="start" />
              <IonLabel>Clientes</IonLabel>
            </IonItem>
            <IonItem className={`sidebar-link ${isActive('/resources') ? 'active-link' : ''}`} button detail={false} routerLink="/resources" routerDirection="root">
              <IonIcon icon={peopleOutline} slot="start" />
              <IonLabel>Recursos</IonLabel>
            </IonItem>
            <IonItem className={`sidebar-link ${isActive('/collaboration') ? 'active-link' : ''}`} button detail={false} routerLink="/collaboration" routerDirection="root">
              <IonIcon icon={chatbubblesOutline} slot="start" />
              <IonLabel>Colaboración</IonLabel>
            </IonItem>
            <IonItem className={`sidebar-link ${isActive('/analytics') ? 'active-link' : ''}`} button detail={false} routerLink="/analytics" routerDirection="root">
              <IonIcon icon={analyticsOutline} slot="start" />
              <IonLabel>Analítica</IonLabel>
            </IonItem>
            <IonItem className={`sidebar-link ${isActive('/notifications') ? 'active-link' : ''}`} button detail={false} routerLink="/notifications" routerDirection="root">
              <IonIcon icon={notificationsOutline} slot="start" />
              <IonLabel>Notificaciones</IonLabel>
            </IonItem>
          </IonMenuToggle>

          {activeProjects.length > 0 && (
            <>
              <p className="sidebar-section-label">Proyectos activos</p>
              <IonMenuToggle autoHide>
                {activeProjects.map((project) => {
                  const projectId = getProjectId(project as Project & Record<string, unknown>);
                  if (!projectId) return null;
                  const clientName = project.client?.name ?? null;
                  return (
                    <IonItem
                      key={projectId}
                      className={`sidebar-link ${isActive(`/projects/${projectId}`) ? 'active-link' : ''}`}
                      button
                      detail={false}
                      routerLink={`/projects/${projectId}`}
                      routerDirection="forward"
                    >
                      <IonLabel>
                        <h3>{project.name}</h3>
                        <p>{formatStatus(project.status)}{clientName ? ` · ${clientName}` : ''}</p>
                      </IonLabel>
                    </IonItem>
                  );
                })}
              </IonMenuToggle>
            </>
          )}

          {activeClients.length > 0 && (
            <>
              <p className="sidebar-section-label">Clientes activos</p>
              <IonMenuToggle autoHide>
                {activeClients.map((client) => {
                  const clientId = getClientId(client as Client & Record<string, unknown>);
                  if (!clientId) return null;
                  return (
                    <IonItem
                      key={clientId}
                      className={`sidebar-link ${isActive(`/clients/${clientId}`) ? 'active-link' : ''}`}
                      button
                      detail={false}
                      routerLink={`/clients/${clientId}`}
                      routerDirection="forward"
                    >
                      <IonLabel>
                        <h3>{client.name}</h3>
                        <p>{client.industry ?? 'Sin industria'}</p>
                      </IonLabel>
                    </IonItem>
                  );
                })}
              </IonMenuToggle>
            </>
          )}
        </IonList>
      </IonContent>
    </IonMenu>
  );
}
