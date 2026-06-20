import { useRef, useState, type FormEvent } from 'react';
import { IonButton, IonInput, IonItem, IonList, IonText } from '@ionic/react';
import type { LoginRequest } from '../../types/auth';

interface LoginFormProps {
  onSubmit: (data: LoginRequest) => Promise<void>;
  isLoading: boolean;
}

export function LoginForm({ onSubmit, isLoading }: LoginFormProps) {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [validationError, setValidationError] = useState<string | null>(null);
  const identifierRef = useRef<HTMLIonInputElement>(null);
  const passwordRef = useRef<HTMLIonInputElement>(null);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // Leer el valor directamente del elemento por si el autofill no disparó onIonInput
    const id = identifier || String((await identifierRef.current?.getInputElement())?.value ?? '');
    const pw = password || String((await passwordRef.current?.getInputElement())?.value ?? '');

    if (!id.trim()) {
      setValidationError('El email o usuario es obligatorio.');
      return;
    }
    if (!pw) {
      setValidationError('La contraseña es obligatoria.');
      return;
    }

    await onSubmit({ identifier: id.trim(), password: pw });
  };

  const handleIdentifierInput = (e: CustomEvent) => {
    setIdentifier(String(e.detail.value ?? ''));
    setValidationError(null);
  };

  const handlePasswordInput = (e: CustomEvent) => {
    setPassword(String(e.detail.value ?? ''));
    setValidationError(null);
  };

  return (
    <form onSubmit={handleSubmit}>
      <IonList inset>
        <IonItem>
          <IonInput
            ref={identifierRef}
            label="Email o usuario"
            labelPlacement="stacked"
            type="text"
            placeholder="correo@ejemplo.com o usuario"
            value={identifier}
            autocomplete="username"
            onIonInput={handleIdentifierInput}
            onIonChange={handleIdentifierInput}
          />
        </IonItem>
        <IonItem>
          <IonInput
            ref={passwordRef}
            label="Contraseña"
            labelPlacement="stacked"
            type="password"
            placeholder="••••••••"
            value={password}
            autocomplete="current-password"
            onIonInput={handlePasswordInput}
            onIonChange={handlePasswordInput}
          />
        </IonItem>
      </IonList>
      {validationError && (
        <IonText color="danger"><p className="ion-padding-horizontal">{validationError}</p></IonText>
      )}
      <IonButton expand="block" type="submit" disabled={isLoading}>
        {isLoading ? 'Iniciando sesión...' : 'Iniciar sesión'}
      </IonButton>
    </form>
  );
}
