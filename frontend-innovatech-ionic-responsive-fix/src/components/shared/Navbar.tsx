import {
  IonAvatar,
  IonButton,
  IonButtons,
  IonHeader,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { useHistory } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export function Navbar() {
  const { user, logout } = useAuth();
  const history = useHistory();
  const initials = user ? `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}`.toUpperCase() || 'U' : 'U';

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
          <IonButton fill="clear" routerLink="/projects" routerDirection="root">Proyectos</IonButton>
          <IonButton fill="clear" routerLink="/clients" routerDirection="root">Clientes</IonButton>
          <IonButton fill="clear" routerLink="/resources" routerDirection="root">Recursos</IonButton>
          <IonAvatar style={{ width: 34, height: 34, marginInline: 8, background: '#eef2ff', display: 'grid', placeItems: 'center', fontWeight: 800, color: '#4f46e5' }}>
            {initials}
          </IonAvatar>
          <IonButton type="button" color="medium" onClick={handleLogout}>Salir</IonButton>
        </IonButtons>
      </IonToolbar>
    </IonHeader>
  );
}
