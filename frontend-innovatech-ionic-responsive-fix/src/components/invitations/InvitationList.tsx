import { IonBadge, IonButton, IonItem, IonLabel, IonList } from '@ionic/react';
import type { Invitation } from '../../types/invitation';

interface InvitationListProps {
  invitations: Invitation[];
  showActions?: boolean;
  onAccept?: (id: string) => void;
  onReject?: (id: string) => void;
}

export function InvitationList({ invitations, showActions = false, onAccept, onReject }: InvitationListProps) {
  return (
    <IonList inset>
      {invitations.map((invitation) => (
        <IonItem key={invitation.id}>
          <IonLabel>
            <h2>📩 {invitation.projectName}</h2>
            <p>Invitado por @{invitation.invitedBy} · {new Date(invitation.createdAt).toLocaleDateString('es-CL')}</p>
          </IonLabel>
          {showActions && invitation.status === 'PENDING' ? (
            <div className="button-row" slot="end">
              <IonButton size="small" onClick={() => onAccept?.(invitation.id)}>Aceptar</IonButton>
              <IonButton size="small" color="medium" fill="outline" onClick={() => onReject?.(invitation.id)}>Rechazar</IonButton>
            </div>
          ) : (
            <IonBadge color={invitation.status === 'ACCEPTED' ? 'success' : invitation.status === 'REJECTED' ? 'danger' : 'warning'} slot="end">
              {invitation.status}
            </IonBadge>
          )}
        </IonItem>
      ))}
    </IonList>
  );
}
