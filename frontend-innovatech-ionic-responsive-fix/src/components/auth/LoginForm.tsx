import { useState, type FormEvent } from 'react';
import { IonButton, IonInput, IonItem, IonList } from '@ionic/react';
import type { LoginRequest } from '../../types/auth';

interface LoginFormProps {
  onSubmit: (data: LoginRequest) => Promise<void>;
  isLoading: boolean;
}

export function LoginForm({ onSubmit, isLoading }: LoginFormProps) {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    await onSubmit({ identifier, password });
  };

  return (
    <form onSubmit={handleSubmit}>
      <IonList inset>
        <IonItem>
          <IonInput
            label="Email o usuario"
            labelPlacement="stacked"
            type="text"
            placeholder="correo@ejemplo.com o usuario"
            value={identifier}
            autocomplete="username"
            required
            onIonInput={(e) => setIdentifier(String(e.detail.value ?? ''))}
          />
        </IonItem>
        <IonItem>
          <IonInput
            label="Contraseña"
            labelPlacement="stacked"
            type="password"
            placeholder="••••••••"
            value={password}
            autocomplete="current-password"
            required
            onIonInput={(e) => setPassword(String(e.detail.value ?? ''))}
          />
        </IonItem>
      </IonList>
      <IonButton expand="block" type="submit" disabled={isLoading}>
        {isLoading ? 'Iniciando sesión...' : 'Iniciar sesión'}
      </IonButton>
    </form>
  );
}
