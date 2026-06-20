import type { ReactNode } from 'react';
import { IonContent, IonPage, IonSpinner } from '@ionic/react';
import { useAuth } from '../hooks/useAuth';

interface AuthLayoutProps {
  children: ReactNode;
}

export function AuthLayout({ children }: AuthLayoutProps) {
  const { isLoading } = useAuth();

  if (isLoading) {
    return (
      <IonPage>
        <IonContent className="auth-content ion-padding">
          <div className="auth-card ion-text-center"><IonSpinner /></div>
        </IonContent>
      </IonPage>
    );
  }

  return (
    <IonPage>
      <IonContent className="auth-content ion-padding" fullscreen>
        {children}
      </IonContent>
    </IonPage>
  );
}
