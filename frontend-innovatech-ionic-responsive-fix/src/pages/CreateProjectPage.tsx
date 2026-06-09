import { useEffect, useMemo, useState, type FormEvent } from 'react';
import { Link, useHistory, useParams } from 'react-router-dom';
import { IonButton, IonCard, IonCardContent, IonInput, IonItem, IonList, IonSelect, IonSelectOption, IonTextarea, IonText } from '@ionic/react';
import { clientService, projectService } from '../api/projectService';
import type { Client, CreateProjectRequest, ProjectStatus } from '../types/projects';
import { getProjectId, toNumericId } from '../utils/ids';

const statusOptions: ProjectStatus[] = ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'];

const generateCode = (tag: string, count: number) => {
  const base = tag.trim().toUpperCase().replace(/\s+/g, '').replace(/[^A-Z0-9]/g, '');
  const num = String(count + 1).padStart(3, '0');
  return base ? `${base}-${num}` : '';
};

export function CreateProjectPage() {
  const { clientId: rawClientId } = useParams<{ clientId: string }>();
  const clientId = useMemo(() => toNumericId(rawClientId), [rawClientId]);
  const history = useHistory();
  const [client, setClient] = useState<Client | null>(null);
  const [projectCount, setProjectCount] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [name, setName] = useState('');
  const [tag, setTag] = useState('');
  const [code, setCode] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState<ProjectStatus>('PLANNING');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    if (!clientId) {
      setError('El ID del cliente no es válido.');
      return;
    }

    clientService.getById(clientId).then(setClient).catch((err) => setError(err instanceof Error ? err.message : 'No se pudo cargar el cliente'));
    projectService.getByClient(clientId).then((projects) => setProjectCount(projects.length)).catch(() => setProjectCount(0));
  }, [clientId]);

  const handleTagChange = (value: string) => {
    setTag(value);
    setCode(generateCode(value, projectCount));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!clientId) {
      setError('El ID del cliente no es válido.');
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      const data: CreateProjectRequest = { code, name, description: description || undefined, status, startDate: startDate || undefined, endDate: endDate || undefined };
      const project = await projectService.create(clientId, data);
      const newProjectId = getProjectId(project as typeof project & Record<string, unknown>);
      history.push(newProjectId ? `/projects/${newProjectId}` : `/clients/${clientId}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el proyecto');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <Link to={clientId ? `/clients/${clientId}` : '/clients'}>← Volver a {client?.name ?? 'cliente'}</Link>
      <div className="page-header ion-margin-top">
        <div>
          <h1 className="page-title">Nuevo proyecto</h1>
          <p className="page-subtitle">Cliente: <strong>{client?.name ?? 'Cargando...'}</strong></p>
        </div>
      </div>
      <IonCard className="app-card">
        <IonCardContent>
          {error && <IonText color="danger"><p>{error}</p></IonText>}
          <form onSubmit={handleSubmit}>
            <IonList inset>
              <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={name} required onIonInput={(e) => setName(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Tag" labelPlacement="stacked" value={tag} maxlength={8} required onIonInput={(e) => handleTagChange(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Código generado" labelPlacement="stacked" value={code} readonly /></IonItem>
              <IonItem><IonTextarea label="Descripción" labelPlacement="stacked" value={description} onIonInput={(e) => setDescription(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem>
                <IonSelect label="Estado" labelPlacement="stacked" value={status} onIonChange={(e) => setStatus(e.detail.value as ProjectStatus)}>
                  {statusOptions.map((item) => <IonSelectOption key={item} value={item}>{item.replace(/_/g, ' ')}</IonSelectOption>)}
                </IonSelect>
              </IonItem>
              <IonItem><IonInput label="Inicio" labelPlacement="stacked" type="date" value={startDate} onIonInput={(e) => setStartDate(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Término" labelPlacement="stacked" type="date" value={endDate} onIonInput={(e) => setEndDate(String(e.detail.value ?? ''))} /></IonItem>
            </IonList>
            <IonButton expand="block" type="submit" disabled={isLoading || !code || !clientId}>{isLoading ? 'Creando...' : 'Crear proyecto'}</IonButton>
          </form>
        </IonCardContent>
      </IonCard>
    </div>
  );
}
