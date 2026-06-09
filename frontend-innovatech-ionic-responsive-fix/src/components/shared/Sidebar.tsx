import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  IonContent,
  IonItem,
  IonLabel,
  IonList,
  IonListHeader,
  IonMenu,
  IonMenuToggle,
  IonNote,
} from '@ionic/react';
import type { Client, Project } from '../../types/projects';
import { clientService, projectService } from '../../api/projectService';
import { useAuth } from '../../hooks/useAuth';
import { getClientId, getProjectId } from '../../utils/ids';

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

    let active = true;
    Promise.allSettled([projectService.getAll(), clientService.getAll()]).then(([projectResult, clientResult]) => {
      if (!active) return;
      setProjects(projectResult.status === 'fulfilled' ? projectResult.value : []);
      setClients(clientResult.status === 'fulfilled' ? clientResult.value : []);
    });

    return () => { active = false; };
  }, [isAuthenticated]);

  if (!isAuthenticated) return null;

  const activePath = (path: string) => location.pathname === path;
  const projectClientId = (project: Project) => project.client ? getClientId(project.client as typeof project.client & Record<string, unknown>) : getClientId({ id: project.clientId });

  return (
    <IonMenu contentId="main-content" type="overlay">
      <IonContent>
        <IonList>
          <IonListHeader>Innovatech</IonListHeader>
          <IonNote className="ion-padding-start">Gestión de proyectos</IonNote>

          <IonMenuToggle autoHide>
            <IonItem button detail={false} routerLink="/dashboard" routerDirection="root" color={activePath('/dashboard') ? 'primary' : undefined}>
              <IonLabel>📊 Dashboard</IonLabel>
            </IonItem>
            <IonItem button detail={false} routerLink="/projects" routerDirection="root" color={activePath('/projects') ? 'primary' : undefined}>
              <IonLabel>📁 Proyectos</IonLabel>
            </IonItem>
            <IonItem button detail={false} routerLink="/clients" routerDirection="root" color={activePath('/clients') ? 'primary' : undefined}>
              <IonLabel>🏢 Clientes</IonLabel>
            </IonItem>
            <IonItem button detail={false} routerLink="/resources" routerDirection="root" color={activePath('/resources') ? 'primary' : undefined}>
              <IonLabel>👥 Recursos</IonLabel>
            </IonItem>
          </IonMenuToggle>

          {projects.length > 0 && <IonListHeader>Últimos proyectos</IonListHeader>}
          <IonMenuToggle autoHide>
            {projects.slice(0, 6).map((project) => {
              const projectId = getProjectId(project as Project & Record<string, unknown>);
              const clientId = projectClientId(project);
              if (!projectId) return null;
              return (
                <IonItem
                  key={projectId}
                  button
                  detail={false}
                  routerLink={`/projects/${projectId}`}
                  routerDirection="forward"
                  color={activePath(`/projects/${projectId}`) ? 'primary' : undefined}
                >
                  <IonLabel>
                    <h3>{project.name}</h3>
                    <p>{project.client?.name ?? (clientId ? `Cliente #${clientId}` : 'Sin cliente')}</p>
                  </IonLabel>
                </IonItem>
              );
            })}
          </IonMenuToggle>

          {clients.length > 0 && <IonListHeader>Clientes</IonListHeader>}
          <IonMenuToggle autoHide>
            {clients.slice(0, 6).map((client) => {
              const clientId = getClientId(client as Client & Record<string, unknown>);
              if (!clientId) return null;
              return (
                <IonItem
                  key={clientId}
                  button
                  detail={false}
                  routerLink={`/clients/${clientId}`}
                  routerDirection="forward"
                  color={activePath(`/clients/${clientId}`) ? 'primary' : undefined}
                >
                  <IonLabel>
                    <h3>{client.name}</h3>
                    <p>{client.status}</p>
                  </IonLabel>
                </IonItem>
              );
            })}
          </IonMenuToggle>
        </IonList>
      </IonContent>
    </IonMenu>
  );
}
