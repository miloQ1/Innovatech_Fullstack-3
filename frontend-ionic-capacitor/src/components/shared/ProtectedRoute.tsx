import type { ReactNode } from 'react';
import { Redirect } from 'react-router-dom';
import { IonContent, IonPage, IonSpinner } from '@ionic/react';
import { useAuth } from '../../hooks/useAuth';

interface ProtectedRouteProps {
  children: ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <IonPage>
        <IonContent className="ion-padding ion-text-center">
          <IonSpinner name="crescent" />
          <p className="muted">Cargando sesión...</p>
        </IonContent>
      </IonPage>
    );
  }

  if (!isAuthenticated) return <Redirect to="/login" />;

  return <>{children}</>;
}
