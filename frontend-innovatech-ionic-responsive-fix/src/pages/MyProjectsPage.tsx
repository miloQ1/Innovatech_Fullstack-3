import type * as React from 'react';
import { useEffect, useMemo, useState } from 'react';
import { useHistory } from 'react-router-dom';
import {
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonModal,
  IonSelect,
  IonSelectOption,
  IonSpinner,
  IonText,
  IonTextarea,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { clientService, projectService } from '../api/projectService';
import { EmptyState } from '../components/shared/EmptyState';
import { ProjectList } from '../components/projects/ProjectList';
import type { Client, ClientStatus, CreateClientRequest, CreateProjectRequest, Project, ProjectStatus } from '../types/projects';
import { getClientId, getProjectId } from '../utils/ids';

const statusOptions: ProjectStatus[] = ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'];

function slugCode(text: string) {
  const base = text.trim().toUpperCase().replace(/[^A-Z0-9]+/g, '-').replace(/^-|-$/g, '').slice(0, 10);
  return base || 'PROY';
}

export function MyProjectsPage() {
  const history = useHistory();
  const [projects, setProjects] = useState<Project[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [selectedClientId, setSelectedClientId] = useState<number | null>(null);
  const [projCode, setProjCode] = useState('');
  const [projName, setProjName] = useState('');
  const [projDesc, setProjDesc] = useState('');
  const [projStatus, setProjStatus] = useState<ProjectStatus>('PLANNING');
  const [projStart, setProjStart] = useState('');
  const [projEnd, setProjEnd] = useState('');
  const [projError, setProjError] = useState<string | null>(null);
  const [savingProject, setSavingProject] = useState(false);
  const [showClientModal, setShowClientModal] = useState(false);
  const [clientName, setClientName] = useState('');
  const [clientIndustry, setClientIndustry] = useState('');
  const [clientContact, setClientContact] = useState('');
  const [clientEmail, setClientEmail] = useState('');
  const [clientStatus, setClientStatus] = useState<ClientStatus>('ACTIVE');
  const [clientError, setClientError] = useState<string | null>(null);

  const generatedCode = useMemo(() => `${slugCode(projName)}-${String(projects.length + 1).padStart(3, '0')}`, [projName, projects.length]);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const [projectResult, clientResult] = await Promise.allSettled([projectService.getAll(), clientService.getAll()]);
      const projectData = projectResult.status === 'fulfilled' ? projectResult.value : [];
      const clientData = clientResult.status === 'fulfilled' ? clientResult.value : [];
      setProjects(projectData);
      setClients(clientData);
      if (!selectedClientId && clientData.length > 0) {
        const firstClientId = getClientId(clientData[0] as Client & Record<string, unknown>);
        setSelectedClientId(firstClientId);
      }
      if (projectResult.status === 'rejected' || clientResult.status === 'rejected') {
        const failed = [projectResult.status === 'rejected' ? 'proyectos' : '', clientResult.status === 'rejected' ? 'clientes' : ''].filter(Boolean).join(' y ');
        setError(`No se pudieron cargar ${failed}. Revisa que el BFF y los microservicios estén levantados.`);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudieron cargar los proyectos');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const resetProjectForm = () => {
    setProjCode('');
    setProjName('');
    setProjDesc('');
    setProjStatus('PLANNING');
    setProjStart('');
    setProjEnd('');
    setSelectedClientId(clients[0] ? getClientId(clients[0] as Client & Record<string, unknown>) : null);
    setProjError(null);
    setShowForm(false);
  };

  const handleCreateProject = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedClientId) {
      setProjError('Selecciona un cliente o crea uno nuevo.');
      return;
    }
    setSavingProject(true);
    setProjError(null);
    try {
      const data: CreateProjectRequest = {
        code: projCode.trim() || generatedCode,
        name: projName,
        description: projDesc || undefined,
        status: projStatus,
        startDate: projStart || undefined,
        endDate: projEnd || undefined,
      };
      const project = await projectService.create(selectedClientId, data);
      resetProjectForm();
      await loadData();
      
      const projectId = getProjectId(project as typeof project & Record<string, unknown>);
      history.push(projectId ? `/projects/${projectId}` : '/projects');
    } catch (err) {
      setProjError(err instanceof Error ? err.message : 'Error al crear el proyecto');
    } finally {
      setSavingProject(false);
    }
  };

  const handleCreateClient = async (event: React.FormEvent) => {
    event.preventDefault();
    setClientError(null);
    try {
      const data: CreateClientRequest = { name: clientName, industry: clientIndustry || undefined, contactName: clientContact || undefined, contactEmail: clientEmail || undefined, status: clientStatus };
      const client = await clientService.create(data);
      setClients((current) => [...current, client]);
      setSelectedClientId(getClientId(client as typeof client & Record<string, unknown>));
      setShowClientModal(false);
      setClientName(''); setClientIndustry(''); setClientContact(''); setClientEmail(''); setClientStatus('ACTIVE');
    } catch (err) {
      setClientError(err instanceof Error ? err.message : 'Error al crear el cliente');
    }
  };

  if (isLoading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando proyectos...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Mis proyectos</h1>
          <p className="page-subtitle">Crea, revisa, edita y administra tus proyectos.</p>
        </div>
        <IonButton type="button" onClick={() => setShowForm((value) => !value)}>{showForm ? 'Cancelar' : '+ Crear proyecto'}</IonButton>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}

      {showForm && (
        <IonCard className="app-card">
          <IonCardContent>
            {projError && <IonText color="danger"><p>{projError}</p></IonText>}
            <form onSubmit={handleCreateProject}>
              <IonList inset>
                <IonItem>
                  <IonSelect label="Cliente" labelPlacement="stacked" value={selectedClientId ?? ''} onIonChange={(e) => setSelectedClientId(e.detail.value ? Number(e.detail.value) : null)}>
                    <IonSelectOption value="">Selecciona un cliente...</IonSelectOption>
                    {clients.map((client) => {
                      const clientId = getClientId(client as Client & Record<string, unknown>);
                      return <IonSelectOption key={clientId ?? client.name} value={clientId ?? ''}>{client.name}</IonSelectOption>;
                    })}
                  </IonSelect>
                  <IonButton type="button" slot="end" fill="clear" onClick={() => setShowClientModal(true)}>Nuevo</IonButton>
                </IonItem>
                <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={projName} required onIonInput={(e) => setProjName(String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Código" labelPlacement="stacked" value={projCode} placeholder={generatedCode} onIonInput={(e) => setProjCode(String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonTextarea label="Descripción" labelPlacement="stacked" value={projDesc} onIonInput={(e) => setProjDesc(String(e.detail.value ?? ''))} /></IonItem>
                <IonItem>
                  <IonSelect label="Estado" labelPlacement="stacked" value={projStatus} onIonChange={(e) => setProjStatus(e.detail.value as ProjectStatus)}>
                    {statusOptions.map((status) => <IonSelectOption key={status} value={status}>{status.replace(/_/g, ' ')}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem><IonInput label="Inicio" labelPlacement="stacked" type="date" value={projStart} onIonInput={(e) => setProjStart(String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Término" labelPlacement="stacked" type="date" value={projEnd} onIonInput={(e) => setProjEnd(String(e.detail.value ?? ''))} /></IonItem>
              </IonList>
              <div className="button-row">
                <IonButton type="submit" disabled={savingProject || !projName.trim()}>{savingProject ? 'Creando...' : 'Crear proyecto'}</IonButton>
                <IonButton type="button" fill="outline" color="medium" onClick={resetProjectForm}>Cancelar</IonButton>
              </div>
            </form>
          </IonCardContent>
        </IonCard>
      )}

      {!showForm && (projects.length === 0 ? (
        <EmptyState icon="📁" title="No hay proyectos" description="Crea tu primer proyecto para comenzar." actionLabel="Crear proyecto" onAction={() => setShowForm(true)} />
      ) : <ProjectList projects={projects} />)}

      <IonModal isOpen={showClientModal} onDidDismiss={() => setShowClientModal(false)}>
        <IonHeader>
          <IonToolbar>
            <IonTitle>Nuevo cliente</IonTitle>
            <IonButtons slot="end"><IonButton type="button" fill="clear" onClick={() => setShowClientModal(false)}>Cerrar</IonButton></IonButtons>
          </IonToolbar>
        </IonHeader>
        <IonContent className="ion-padding">
          {clientError && <IonText color="danger"><p>{clientError}</p></IonText>}
          <form onSubmit={handleCreateClient}>
            <IonList inset>
              <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={clientName} required onIonInput={(e) => setClientName(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Industria" labelPlacement="stacked" value={clientIndustry} onIonInput={(e) => setClientIndustry(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Contacto" labelPlacement="stacked" value={clientContact} onIonInput={(e) => setClientContact(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Email" labelPlacement="stacked" type="email" value={clientEmail} onIonInput={(e) => setClientEmail(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem>
                <IonSelect label="Estado" labelPlacement="stacked" value={clientStatus} onIonChange={(e) => setClientStatus(e.detail.value as ClientStatus)}>
                  <IonSelectOption value="ACTIVE">Activo</IonSelectOption>
                  <IonSelectOption value="INACTIVE">Inactivo</IonSelectOption>
                </IonSelect>
              </IonItem>
            </IonList>
            <div className="button-row">
              <IonButton type="submit">Crear cliente</IonButton>
              <IonButton type="button" fill="outline" color="medium" onClick={() => setShowClientModal(false)}>Cancelar</IonButton>
            </div>
          </form>
        </IonContent>
      </IonModal>
    </div>
  );
}
