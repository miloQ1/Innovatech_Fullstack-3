import { IonAvatar, IonBadge, IonItem, IonLabel, IonList } from '@ionic/react';
import type { ProjectMember } from '../../types/projects';

interface MemberListProps {
  members: ProjectMember[];
}

export function MemberList({ members }: MemberListProps) {
  return (
    <IonList inset>
      {members.map((member) => (
        <IonItem key={member.id}>
          <IonAvatar slot="start" style={{ background: '#eef2ff', display: 'grid', placeItems: 'center', color: '#4f46e5', fontWeight: 800 }}>
            {member.userName.charAt(0).toUpperCase()}
          </IonAvatar>
          <IonLabel>
            <h2>@{member.userName}</h2>
            <p>{member.userId}</p>
          </IonLabel>
          <IonBadge color={member.role === 'OWNER' ? 'warning' : 'medium'} slot="end">{member.role}</IonBadge>
        </IonItem>
      ))}
    </IonList>
  );
}
