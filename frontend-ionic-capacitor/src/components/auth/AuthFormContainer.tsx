import type { ReactNode } from 'react';
import { IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle, IonText } from '@ionic/react';

interface AuthFormContainerProps {
  title: string;
  subtitle?: string;
  error?: string | null;
  footer?: ReactNode;
  children: ReactNode;
}

export function AuthFormContainer({ title, subtitle, error, footer, children }: AuthFormContainerProps) {
  return (
    <IonCard className="auth-card app-card">
      <IonCardHeader className="ion-text-center">
        <div className="logo-badge">IT</div>
        <IonCardTitle>{title}</IonCardTitle>
        {subtitle && <IonCardSubtitle>{subtitle}</IonCardSubtitle>}
      </IonCardHeader>
      <IonCardContent>
        {error && <IonText color="danger"><p>{error}</p></IonText>}
        {children}
        {footer && <div className="ion-text-center ion-margin-top muted">{footer}</div>}
      </IonCardContent>
    </IonCard>
  );
}
