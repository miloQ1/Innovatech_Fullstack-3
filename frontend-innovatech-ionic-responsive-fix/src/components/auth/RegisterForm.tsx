import { useState, type FormEvent } from 'react';
import { IonButton, IonInput, IonItem, IonList } from '@ionic/react';
import type { RegisterRequest } from '../../types/auth';

interface RegisterFormProps {
  onSubmit: (data: RegisterRequest) => Promise<void>;
  isLoading: boolean;
}

export function RegisterForm({ onSubmit, isLoading }: RegisterFormProps) {
  const [userName, setUserName] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    await onSubmit({ userName, firstName, lastName, email, password });
  };

  return (
    <form onSubmit={handleSubmit}>
      <IonList inset>
        <IonItem><IonInput label="Usuario" labelPlacement="stacked" value={userName} required onIonInput={(e) => setUserName(String(e.detail.value ?? ''))} /></IonItem>
        <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={firstName} required onIonInput={(e) => setFirstName(String(e.detail.value ?? ''))} /></IonItem>
        <IonItem><IonInput label="Apellido" labelPlacement="stacked" value={lastName} required onIonInput={(e) => setLastName(String(e.detail.value ?? ''))} /></IonItem>
        <IonItem><IonInput label="Email" labelPlacement="stacked" type="email" value={email} autocomplete="email" required onIonInput={(e) => setEmail(String(e.detail.value ?? ''))} /></IonItem>
        <IonItem><IonInput label="Contraseña" labelPlacement="stacked" type="password" value={password} autocomplete="new-password" required minlength={6} onIonInput={(e) => setPassword(String(e.detail.value ?? ''))} /></IonItem>
      </IonList>
      <IonButton expand="block" type="submit" disabled={isLoading}>
        {isLoading ? 'Creando cuenta...' : 'Crear cuenta'}
      </IonButton>
    </form>
  );
}
