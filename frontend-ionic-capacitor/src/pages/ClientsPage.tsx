import { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonIcon, IonSpinner, IonText } from '@ionic/react';
import { businessOutline, mailOutline, personOutline } from 'ionicons/icons';
import { clientService } from '../api/projectService';
import { EmptyState } from '../components/shared/EmptyState';
import type { Client } from '../types/projects';
import { blurActiveElement, getClientId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

export function ClientsPage() {
  const history = useHistory();
  const [clients, setClients] = useState<Client[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const navigate = (path: string) => {
    blurActiveElement();
    history.push(path);
  };

  const loadClients = async () => {
    setIsLoading(true);
    setError(null);
    try {
      setClients(await clientService.getAll());
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudieron cargar los clientes');
      setClients([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => { loadClients(); }, []);

  if (isLoading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando clientes...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Clientes</h1>
          <p className="page-subtitle">Administra los clientes asociados a tus proyectos.</p>
        </div>
        <IonButton type="button" onClick={() => navigate('/clients/create')}>+ Nuevo cliente</IonButton>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}

      {clients.length === 0 ? (
        <EmptyState icon={businessOutline} title="No hay clientes" description="Crea tu primer cliente para comenzar a gestionar proyectos." actionLabel="Crear cliente" onAction={() => navigate('/clients/create')} />
      ) : (
        <div className="card-grid">
          {clients.map((client) => {
            const clientId = getClientId(client as Client & Record<string, unknown>);
            return (
              <IonCard key={clientId ?? client.name} className="app-card accent-card clickable" onClick={() => clientId && navigate(`/clients/${clientId}`)}>
                <IonCardHeader>
                  <div className="card-title-row">
                    <IonCardTitle className="card-title-main">{client.name}</IonCardTitle>
                    <IonBadge color={client.status === 'ACTIVE' ? 'success' : 'medium'}>{formatStatus(client.status)}</IonBadge>
                  </div>
                </IonCardHeader>
                <IonCardContent>
                  <div className="card-info-list">
                    {client.industry && (
                      <span className="card-info-row">
                        <IonIcon icon={businessOutline} />
                        {client.industry}
                      </span>
                    )}
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
                  <div className="card-actions">
                    <IonButton
                      type="button"
                      size="small"
                      fill="solid"
                      color="primary"
                      disabled={!clientId}
                      onClick={(event) => {
                        event.stopPropagation();
                        if (clientId) navigate(`/clients/${clientId}`);
                      }}
                    >
                      Ver / editar
                    </IonButton>
                    <IonButton
                      type="button"
                      size="small"
                      fill="outline"
                      color="primary"
                      disabled={!clientId}
                      onClick={(event) => {
                        event.stopPropagation();
                        if (clientId) navigate(`/clients/${clientId}/projects/create`);
                      }}
                    >
                      Crear proyecto
                    </IonButton>
                  </div>
                </IonCardContent>
              </IonCard>
            );
          })}
        </div>
      )}
    </div>
  );
}
