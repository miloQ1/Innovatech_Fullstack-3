import { useState, type FormEvent } from 'react';
import { IonButton, IonInput, IonItem, IonList, IonSelect, IonSelectOption, IonTextarea, IonText } from '@ionic/react';
import type { CreateProjectRequest, ProjectStatus } from '../../types/projects';

interface CreateProjectFormProps {
  onSubmit: (data: CreateProjectRequest) => Promise<void>;
  isLoading: boolean;
  errorMessage?: string | null;
}

const statusOptions: ProjectStatus[] = ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'];

export function CreateProjectForm({ onSubmit, isLoading, errorMessage }: CreateProjectFormProps) {
  const [data, setData] = useState<CreateProjectRequest>({ code: '', name: '', status: 'PLANNING' });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    await onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit}>
      {errorMessage && <IonText color="danger"><p>{errorMessage}</p></IonText>}
      <IonList inset>
        <IonItem><IonInput label="Código" labelPlacement="stacked" value={data.code} required onIonInput={(e) => setData((p) => ({ ...p, code: String(e.detail.value ?? '') }))} /></IonItem>
        <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={data.name} required onIonInput={(e) => setData((p) => ({ ...p, name: String(e.detail.value ?? '') }))} /></IonItem>
        <IonItem><IonTextarea label="Descripción" labelPlacement="stacked" value={data.description} onIonInput={(e) => setData((p) => ({ ...p, description: String(e.detail.value ?? '') }))} /></IonItem>
        <IonItem>
          <IonSelect label="Estado" labelPlacement="stacked" value={data.status} onIonChange={(e) => setData((p) => ({ ...p, status: e.detail.value as ProjectStatus }))}>
            {statusOptions.map((status) => <IonSelectOption key={status} value={status}>{status.replace(/_/g, ' ')}</IonSelectOption>)}
          </IonSelect>
        </IonItem>
      </IonList>
      <IonButton expand="block" type="submit" disabled={isLoading}>{isLoading ? 'Creando...' : 'Crear proyecto'}</IonButton>
    </form>
  );
}
