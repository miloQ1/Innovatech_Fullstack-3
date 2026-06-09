import { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner, IonText } from '@ionic/react';
import { clientService } from '../api/projectService';
import { EmptyState } from '../components/shared/EmptyState';
import type { Client } from '../types/projects';
import { blurActiveElement, getClientId } from '../utils/ids';

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
        <EmptyState icon="🏢" title="No hay clientes" description="Crea tu primer cliente para comenzar a gestionar proyectos." actionLabel="Crear cliente" onAction={() => navigate('/clients/create')} />
      ) : (
        <div className="card-grid">
          {clients.map((client) => {
            const clientId = getClientId(client as Client & Record<string, unknown>);
            return (
              <IonCard key={clientId ?? client.name} className="app-card clickable" onClick={() => clientId && navigate(`/clients/${clientId}`)}>
                <IonCardHeader>
                  <div className="page-header" style={{ marginBottom: 0 }}>
                    <IonCardTitle>{client.name}</IonCardTitle>
                    <IonBadge color={client.status === 'ACTIVE' ? 'success' : 'medium'}>{client.status}</IonBadge>
                  </div>
                </IonCardHeader>
                <IonCardContent>
                  {client.industry && <p>🏭 {client.industry}</p>}
                  {client.contactName && <p>👤 {client.contactName}</p>}
                  {client.contactEmail && <p>✉️ {client.contactEmail}</p>}
                  <div className="button-row ion-margin-top">
                    <IonButton
                      type="button"
                      size="small"
                      fill="outline"
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
