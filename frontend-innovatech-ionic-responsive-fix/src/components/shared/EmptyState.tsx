import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle } from '@ionic/react';

interface EmptyStateProps {
  icon?: string;
  title: string;
  description?: string;
  actionLabel?: string;
  onAction?: () => void;
}

export function EmptyState({ icon = '📭', title, description, actionLabel, onAction }: EmptyStateProps) {
  return (
    <IonCard className="app-card ion-text-center">
      <IonCardHeader>
        <div style={{ fontSize: 42 }}>{icon}</div>
        <IonCardTitle>{title}</IonCardTitle>
      </IonCardHeader>
      <IonCardContent>
        {description && <p className="muted">{description}</p>}
        {actionLabel && onAction && <IonButton onClick={onAction}>{actionLabel}</IonButton>}
      </IonCardContent>
    </IonCard>
  );
}
