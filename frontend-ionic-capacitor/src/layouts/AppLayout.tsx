import type { ReactNode } from 'react';
import { IonContent, IonPage } from '@ionic/react';
import { Navbar } from '../components/shared/Navbar';

interface AppLayoutProps {
  children: ReactNode;
}

export function AppLayout({ children }: AppLayoutProps) {
  return (
    <IonPage>
      <Navbar />
      <IonContent className="app-content" fullscreen>
        <div className="ion-padding page-shell">{children}</div>
      </IonContent>
    </IonPage>
  );
}
