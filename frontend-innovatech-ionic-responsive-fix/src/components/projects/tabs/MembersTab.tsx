import type * as React from 'react';
import { useState } from 'react';
import { IonAvatar, IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonInput, IonItem, IonLabel, IonList, IonText } from '@ionic/react';
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

  const handleAdd = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!username.trim()) return;
    setLoading(true);
    setMessage(null);
    try {
      const found = await authService.getUserByUsername(username.trim());
      await memberService.add(projectId, { userId: found.id, userName: found.userName });
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

  return (
    <IonCard className="app-card">
      <IonCardHeader><IonCardTitle>👥 Miembros</IonCardTitle></IonCardHeader>
      <IonCardContent>
        <IonList inset>
          {members.map((member) => (
            <IonItem key={member.id}>
              <IonAvatar slot="start" style={{ background: '#eef2ff', display: 'grid', placeItems: 'center', color: '#4f46e5', fontWeight: 800 }}>
                {member.userName.charAt(0).toUpperCase()}
              </IonAvatar>
              <IonLabel>
                <h2>{user?.userName === member.userName ? `${user.firstName} ${user.lastName}` : `@${member.userName}`}</h2>
                <p>@{member.userName}</p>
              </IonLabel>
              <IonBadge color={member.role === 'OWNER' ? 'warning' : 'medium'} slot="end">{member.role === 'OWNER' ? 'Owner' : 'Miembro'}</IonBadge>
              {user?.userName !== member.userName && <IonButton color="danger" fill="clear" slot="end" onClick={() => setMemberToRemove(member)}>Quitar</IonButton>}
            </IonItem>
          ))}
        </IonList>

        <form onSubmit={handleAdd}>
          {message && <IonText color={message.type}><p>{message.text}</p></IonText>}
          <IonItem>
            <IonInput label="Invitar por usuario" labelPlacement="stacked" placeholder="ej: johndoe" value={username} required onIonInput={(e) => { setUsername(String(e.detail.value ?? '')); setMessage(null); }} />
          </IonItem>
          <IonButton className="ion-margin-top" type="submit" disabled={loading}>{loading ? 'Buscando...' : '+ Invitar'}</IonButton>
        </form>
      </IonCardContent>

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
    </IonCard>
  );
}
