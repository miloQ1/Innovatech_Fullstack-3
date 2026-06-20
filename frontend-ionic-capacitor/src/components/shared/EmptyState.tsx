import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonIcon } from '@ionic/react';
import { fileTrayOutline } from 'ionicons/icons';

interface EmptyStateProps {
  icon?: string;
  title: string;
  description?: string;
  actionLabel?: string;
  onAction?: () => void;
}

export function EmptyState({ icon = fileTrayOutline, title, description, actionLabel, onAction }: EmptyStateProps) {
  return (
    <IonCard className="app-card ion-text-center">
      <IonCardHeader>
        <div className="empty-state-icon">
          <IonIcon icon={icon} />
        </div>
        <IonCardTitle>{title}</IonCardTitle>
      </IonCardHeader>
      <IonCardContent>
        {description && <p className="muted">{description}</p>}
        {actionLabel && onAction && <IonButton onClick={onAction}>{actionLabel}</IonButton>}
      </IonCardContent>
    </IonCard>
  );
}
