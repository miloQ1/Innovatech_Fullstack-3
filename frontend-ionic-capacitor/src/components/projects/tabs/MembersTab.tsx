import type * as React from 'react';
import { useState } from 'react';
import {
  IonAvatar,
  IonBadge,
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonModal,
  IonText,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { peopleOutline } from 'ionicons/icons';
import type { ProjectMember } from '../../../types/projects';
import { authService } from '../../../api/authService';
import { memberService } from '../../../api/projectService';
import { useAuth } from '../../../hooks/useAuth';
import { ConfirmModal } from '../../shared/ConfirmModal';

interface MembersTabProps {
  members: ProjectMember[];
  projectId: number;
  onReload: () => void;
}

export function MembersTab({ members, projectId, onReload }: MembersTabProps) {
  const { user } = useAuth();
  const [username, setUsername] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger'; text: string } | null>(null);
  const [memberToRemove, setMemberToRemove] = useState<ProjectMember | null>(null);
  const [viewMember, setViewMember] = useState<ProjectMember | null>(null);

  const handleAdd = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!username.trim()) return;
    setLoading(true);
    setMessage(null);
    try {
      const found = await authService.getUserByUsername(username.trim());
      await memberService.add(projectId, { userId: found.id, userName: found.userName, firstName: found.firstName, lastName: found.lastName });
      setUsername('');
      setMessage({ type: 'success', text: `@${found.userName} agregado al proyecto.` });
      onReload();
    } catch {
      setMessage({ type: 'danger', text: `No se encontró el usuario @${username}.` });
    } finally {
      setLoading(false);
    }
  };

  const confirmRemove = async () => {
    if (!memberToRemove) return;
    await memberService.remove(projectId, memberToRemove.userId).catch(() => undefined);
    setMemberToRemove(null);
    onReload();
  };

  const displayName = (member: ProjectMember) => {
    if (member.firstName || member.lastName) {
      return `${member.firstName ?? ''} ${member.lastName ?? ''}`.trim();
    }
    if (user?.userName === member.userName) {
      return `${user.firstName} ${user.lastName}`;
    }
    return `@${member.userName}`;
  };

  return (
    <>
      <IonCard className="app-card">
        <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={peopleOutline} />Miembros</IonCardTitle></IonCardHeader>
        <IonCardContent>
          <IonList inset>
            {members.map((member) => (
              <IonItem key={member.id} className="member-item" button detail={false} onClick={() => setViewMember(member)}>
                <IonAvatar slot="start" className="member-avatar">
                  {member.userName.charAt(0).toUpperCase()}
                </IonAvatar>
                <IonLabel>
                  <h2>{displayName(member)}</h2>
                </IonLabel>
                <IonBadge color={member.role === 'OWNER' ? 'warning' : 'medium'} slot="end">
                  {member.role === 'OWNER' ? 'Owner' : 'Miembro'}
                </IonBadge>
              </IonItem>
            ))}
          </IonList>

          <form onSubmit={handleAdd}>
            {message && <IonText color={message.type}><p>{message.text}</p></IonText>}
            <IonItem>
              <IonInput
                label="Invitar por usuario"
                labelPlacement="stacked"
                placeholder="ej: johndoe"
                value={username}
                required
                onIonInput={(e) => { setUsername(String(e.detail.value ?? '')); setMessage(null); }}
              />
            </IonItem>
            <IonButton className="ion-margin-top" type="submit" disabled={loading}>
              {loading ? 'Buscando...' : '+ Invitar'}
            </IonButton>
          </form>
        </IonCardContent>
      </IonCard>

      <IonModal isOpen={!!viewMember} onDidDismiss={() => setViewMember(null)}>
        <IonHeader>
          <IonToolbar>
            <IonTitle>Detalle del miembro</IonTitle>
            <IonButtons slot="end">
              <IonButton fill="clear" onClick={() => setViewMember(null)}>Cerrar</IonButton>
            </IonButtons>
          </IonToolbar>
        </IonHeader>
        <IonContent className="ion-padding">
          {viewMember && (
            <div className="member-detail">
              <div className="member-detail-avatar">
                {viewMember.userName.charAt(0).toUpperCase()}
              </div>
              <h2 className="member-detail-name">{displayName(viewMember)}</h2>
              <p className="muted">@{viewMember.userName}</p>
              <IonBadge color={viewMember.role === 'OWNER' ? 'warning' : 'medium'}>
                {viewMember.role === 'OWNER' ? 'Owner' : 'Miembro'}
              </IonBadge>
              {user?.userName !== viewMember.userName && (
                <IonButton
                  expand="block"
                  color="danger"
                  fill="outline"
                  className="ion-margin-top"
                  onClick={() => { setMemberToRemove(viewMember); setViewMember(null); }}
                >
                  Quitar del proyecto
                </IonButton>
              )}
            </div>
          )}
        </IonContent>
      </IonModal>

      {memberToRemove && (
        <ConfirmModal
          title={`Eliminar a @${memberToRemove.userName}`}
          message="Esta persona perderá acceso al proyecto."
          confirmLabel="Eliminar"
          danger
          onConfirm={confirmRemove}
          onCancel={() => setMemberToRemove(null)}
        />
      )}
    </>
  );
}
