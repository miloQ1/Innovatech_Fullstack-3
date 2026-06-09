import { useState, type FormEvent } from 'react';
import { IonButton, IonInput, IonItem, IonList, IonText } from '@ionic/react';

interface InviteUserFormProps {
  onSubmit: (userName: string) => Promise<void>;
  isLoading: boolean;
  successMessage?: string | null;
  errorMessage?: string | null;
}

export function InviteUserForm({ onSubmit, isLoading, successMessage, errorMessage }: InviteUserFormProps) {
  const [userName, setUserName] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    await onSubmit(userName.trim());
    setUserName('');
  };

  return (
    <form onSubmit={handleSubmit}>
      {errorMessage && <IonText color="danger"><p>{errorMessage}</p></IonText>}
      {successMessage && <IonText color="success"><p>{successMessage}</p></IonText>}
      <IonList inset>
        <IonItem><IonInput label="Usuario" labelPlacement="stacked" value={userName} required onIonInput={(e) => setUserName(String(e.detail.value ?? ''))} /></IonItem>
      </IonList>
      <IonButton type="submit" disabled={isLoading || !userName.trim()}>Invitar</IonButton>
    </form>
  );
}
