import { useState, type FormEvent } from 'react';
import { useHistory } from 'react-router-dom';
import { IonButton, IonCard, IonCardContent, IonInput, IonItem, IonList, IonSelect, IonSelectOption, IonText } from '@ionic/react';
import { clientService } from '../api/projectService';
import { getClientId } from '../utils/ids';
import type { ClientStatus, CreateClientRequest } from '../types/projects';

export function CreateClientPage() {
  const history = useHistory();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [name, setName] = useState('');
  const [industry, setIndustry] = useState('');
  const [contactName, setContactName] = useState('');
  const [contactEmail, setContactEmail] = useState('');
  const [status, setStatus] = useState<ClientStatus>('ACTIVE');

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setIsLoading(true);
    setError(null);
    try {
      const data: CreateClientRequest = { name, industry: industry || undefined, contactName: contactName || undefined, contactEmail: contactEmail || undefined, status };
      const client = await clientService.create(data);
      
      const clientId = getClientId(client as typeof client & Record<string, unknown>);
      history.push(clientId ? `/clients/${clientId}` : '/clients');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el cliente');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Nuevo cliente</h1>
          <p className="page-subtitle">Registra un cliente para asociar proyectos.</p>
        </div>
      </div>
      <IonCard className="app-card">
        <IonCardContent>
          {error && <IonText color="danger"><p>{error}</p></IonText>}
          <form onSubmit={handleSubmit}>
            <IonList inset>
              <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={name} required onIonInput={(e) => setName(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Industria" labelPlacement="stacked" value={industry} onIonInput={(e) => setIndustry(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Contacto" labelPlacement="stacked" value={contactName} onIonInput={(e) => setContactName(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Email contacto" labelPlacement="stacked" type="email" value={contactEmail} onIonInput={(e) => setContactEmail(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem>
                <IonSelect label="Estado" labelPlacement="stacked" value={status} onIonChange={(e) => setStatus(e.detail.value as ClientStatus)}>
                  <IonSelectOption value="ACTIVE">Activo</IonSelectOption>
                  <IonSelectOption value="INACTIVE">Inactivo</IonSelectOption>
                </IonSelect>
              </IonItem>
            </IonList>
            <IonButton expand="block" type="submit" disabled={isLoading}>{isLoading ? 'Creando...' : 'Crear cliente'}</IonButton>
          </form>
        </IonCardContent>
      </IonCard>
    </div>
  );
}
