import { IonAvatar, IonBadge, IonItem, IonLabel, IonList } from '@ionic/react';
import type { ProjectMember } from '../../types/projects';

interface MemberListProps {
  members: ProjectMember[];
}

export function MemberList({ members }: MemberListProps) {
  return (
    <IonList inset>
      {members.map((member) => (
        <IonItem key={member.id} className="member-item">
          <IonAvatar slot="start" className="member-avatar">
            {member.userName.charAt(0).toUpperCase()}
          </IonAvatar>
          <IonLabel>
            <h2>@{member.userName}</h2>
          </IonLabel>
          <IonBadge color={member.role === 'OWNER' ? 'warning' : 'medium'} slot="end">{member.role === 'OWNER' ? 'Owner' : 'Miembro'}</IonBadge>
        </IonItem>
      ))}
    </IonList>
  );
}
