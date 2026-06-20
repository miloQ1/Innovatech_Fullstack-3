import type * as React from 'react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useHistory, useParams } from 'react-router-dom';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonIcon, IonInput, IonItem, IonList, IonSelect, IonSelectOption, IonSpinner, IonText } from '@ionic/react';
import { folderOpenOutline, mailOutline, personOutline } from 'ionicons/icons';
import { clientService, projectService } from '../api/projectService';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import type { Client, ClientStatus, Project } from '../types/projects';
import { blurActiveElement, getProjectId, toNumericId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

export function ClientDetailPage() {
  const { id } = useParams<{ id: string }>();
  const clientId = useMemo(() => toNumericId(id), [id]);
  const history = useHistory();
  const [client, setClient] = useState<Client | null>(null);
  const [projects, setProjects] = useState<Project[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [warning, setWarning] = useState<string | null>(null);
  const [form, setForm] = useState({ name: '', industry: '', contactName: '', contactEmail: '', status: 'ACTIVE' as ClientStatus });

  const loadData = useCallback(async () => {
    if (!clientId) {
      setClient(null);
      setProjects([]);
      setError('El ID del cliente no es válido.');
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);
    setWarning(null);

    const [clientResult, projectsResult] = await Promise.allSettled([
      clientService.getById(clientId),
      projectService.getByClient(clientId),
    ]);

    if (clientResult.status === 'fulfilled') {
      const clientData = clientResult.value;
      setClient(clientData);
      setForm({
        name: clientData.name,
        industry: clientData.industry ?? '',
        contactName: clientData.contactName ?? '',
        contactEmail: clientData.contactEmail ?? '',
        status: clientData.status,
      });
    } else {
      setClient(null);
      setError(clientResult.reason instanceof Error ? clientResult.reason.message : 'No se pudo cargar el cliente');
    }

    if (projectsResult.status === 'fulfilled') {
      setProjects(projectsResult.value);
    } else {
      setProjects([]);
      setWarning(projectsResult.reason instanceof Error ? projectsResult.reason.message : 'No se pudieron cargar los proyectos del cliente');
    }

    setIsLoading(false);
  }, [clientId]);

  useEffect(() => { loadData(); }, [loadData]);

  const setField = (name: keyof typeof form, value: string) => setForm((previous) => ({ ...previous, [name]: value }));

  const handleSave = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!clientId) return;
    setError(null);
    try {
      await clientService.update(clientId, {
        name: form.name,
        industry: form.industry || undefined,
        contactName: form.contactName || undefined,
        contactEmail: form.contactEmail || undefined,
        status: form.status,
      });
      setIsEditing(false);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el cliente');
    }
  };

  const handleDelete = async () => {
    if (!clientId) return;
    try {
      await clientService.delete(clientId);
      blurActiveElement();
      history.push('/clients');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el cliente');
      setConfirmDelete(false);
    }
  };

  if (isLoading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando cliente...</p></div>;

  if (!client) {
    return (
      <IonCard className="app-card">
        <IonCardContent>
          <p>Cliente no encontrado.</p>
          {error && <IonText color="danger"><p>{error}</p></IonText>}
          <Link to="/clients">Volver a clientes</Link>
        </IonCardContent>
      </IonCard>
    );
  }

  return (
    <div>
      <Link to="/clients">← Volver a clientes</Link>
      <div className="page-header ion-margin-top">
        <div>
          <h1 className="page-title">{client.name}</h1>
          <p className="page-subtitle">{client.industry ?? 'Sin industria registrada'}</p>
        </div>
        <div className="button-row">
          <IonBadge color={client.status === 'ACTIVE' ? 'success' : 'medium'}>{formatStatus(client.status)}</IonBadge>
          <IonButton fill="outline" onClick={() => setIsEditing((value) => !value)}>{isEditing ? 'Cancelar' : 'Editar'}</IonButton>
          <IonButton color="danger" fill="outline" onClick={() => setConfirmDelete(true)}>Eliminar</IonButton>
        </div>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}
      {warning && <IonText color="warning"><p>{warning}</p></IonText>}

      {isEditing ? (
        <IonCard className="app-card">
          <IonCardContent>
            <form onSubmit={handleSave}>
              <IonList inset>
                <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={form.name} required onIonInput={(e) => setField('name', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Industria" labelPlacement="stacked" value={form.industry} onIonInput={(e) => setField('industry', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Contacto" labelPlacement="stacked" value={form.contactName} onIonInput={(e) => setField('contactName', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Email" labelPlacement="stacked" type="email" value={form.contactEmail} onIonInput={(e) => setField('contactEmail', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem>
                  <IonSelect label="Estado" labelPlacement="stacked" value={form.status} onIonChange={(e) => setField('status', String(e.detail.value))}>
                    <IonSelectOption value="ACTIVE">Activo</IonSelectOption>
                    <IonSelectOption value="INACTIVE">Inactivo</IonSelectOption>
                  </IonSelect>
                </IonItem>
              </IonList>
              <IonButton expand="block" type="submit">Guardar cambios</IonButton>
            </form>
          </IonCardContent>
        </IonCard>
      ) : (
        <IonCard className="app-card accent-card">
          <IonCardContent>
            <div className="card-info-list">
              {client.contactName && (
                <span className="card-info-row">
                  <IonIcon icon={personOutline} />
                  {client.contactName}
                </span>
              )}
              {client.contactEmail && (
                <span className="card-info-row">
                  <IonIcon icon={mailOutline} />
                  {client.contactEmail}
                </span>
              )}
            </div>
          </IonCardContent>
        </IonCard>
      )}

      {!isEditing && (
        <>
          <div className="page-header ion-margin-top">
            <h2 className="section-title"><IonIcon icon={folderOpenOutline} />Proyectos ({projects.length})</h2>
            <IonButton onClick={() => { blurActiveElement(); history.push(`/clients/${clientId}/projects/create`); }}>+ Nuevo proyecto</IonButton>
          </div>
          {projects.length === 0 ? <IonCard className="app-card"><IonCardContent><p className="muted">Este cliente todavía no tiene proyectos.</p></IonCardContent></IonCard> : (
            <div className="card-grid">
              {projects.map((project) => {
                const projectId = getProjectId(project as Project & Record<string, unknown>);
                return (
                  <IonCard key={projectId ?? project.code} className="app-card accent-card clickable" button onClick={() => projectId && history.push(`/projects/${projectId}`)}>
                    <IonCardHeader>
                      <div className="card-title-row">
                        <IonCardTitle className="card-title-main">{project.name}</IonCardTitle>
                        <IonBadge>{formatStatus(project.status)}</IonBadge>
                      </div>
                    </IonCardHeader>
                    <IonCardContent>
                      <div className="card-info-list">
                        <span className="card-info-row">#{project.code}</span>
                        {project.description && <span className="card-info-row">{project.description}</span>}
                      </div>
                    </IonCardContent>
                  </IonCard>
                );
              })}
            </div>
          )}
        </>
      )}

      {confirmDelete && <ConfirmModal title={`Eliminar cliente "${client.name}"`} message="Esta acción eliminará el cliente permanentemente. Los proyectos asociados también podrían eliminarse." confirmLabel="Eliminar" danger onConfirm={handleDelete} onCancel={() => setConfirmDelete(false)} />}
    </div>
  );
}
