import {
  IonAvatar,
  IonButton,
  IonButtons,
  IonHeader,
  IonIcon,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { analyticsOutline, businessOutline, chatbubblesOutline, folderOpenOutline, logOutOutline, notificationsOutline, peopleOutline } from 'ionicons/icons';
import { useHistory, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export function Navbar() {
  const { user, logout } = useAuth();
  const history = useHistory();
  const location = useLocation();
  const initials = user ? `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}`.toUpperCase() || 'U' : 'U';

  const isActive = (path: string) => location.pathname.startsWith(path);

  const handleLogout = async () => {
    await logout();
    history.replace('/login');
  };

  return (
    <IonHeader translucent>
      <IonToolbar>
        <IonButtons slot="start">
          <IonMenuButton />
        </IonButtons>
        <IonTitle>Innovatech</IonTitle>
        <IonButtons slot="end">
          <IonButton fill="clear" className={`navbar-link ${isActive('/projects') ? 'active-link' : ''}`} routerLink="/projects" routerDirection="root">
            <IonIcon icon={folderOpenOutline} slot="start" />
            Proyectos
          </IonButton>
          <IonButton fill="clear" className={`navbar-link ${isActive('/clients') ? 'active-link' : ''}`} routerLink="/clients" routerDirection="root">
            <IonIcon icon={businessOutline} slot="start" />
            Clientes
          </IonButton>
          <IonButton fill="clear" className={`navbar-link ${isActive('/resources') ? 'active-link' : ''}`} routerLink="/resources" routerDirection="root">
            <IonIcon icon={peopleOutline} slot="start" />
            Recursos
          </IonButton>
          <IonButton fill="clear" className={`navbar-link ${isActive('/collaboration') ? 'active-link' : ''}`} routerLink="/collaboration" routerDirection="root">
            <IonIcon icon={chatbubblesOutline} slot="start" />
            Colaboración
          </IonButton>
          <IonButton fill="clear" className={`navbar-link ${isActive('/analytics') ? 'active-link' : ''}`} routerLink="/analytics" routerDirection="root">
            <IonIcon icon={analyticsOutline} slot="start" />
            Analítica
          </IonButton>
          <IonButton fill="clear" className={`navbar-link ${isActive('/notifications') ? 'active-link' : ''}`} routerLink="/notifications" routerDirection="root">
            <IonIcon icon={notificationsOutline} slot="start" />
            Notificaciones
          </IonButton>
          <IonAvatar className="user-avatar">
            {initials}
          </IonAvatar>
          <IonButton type="button" fill="clear" color="medium" onClick={handleLogout}>
            <IonIcon icon={logOutOutline} slot="start" />
            Salir
          </IonButton>
        </IonButtons>
      </IonToolbar>
    </IonHeader>
  );
}
