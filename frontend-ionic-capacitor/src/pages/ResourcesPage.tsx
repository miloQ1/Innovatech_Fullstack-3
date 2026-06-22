import { useEffect, useMemo, useState } from 'react';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonIcon, IonInput, IonItem, IonSelect, IonSelectOption, IonSpinner, IonText } from '@ionic/react';
import { locationOutline, mailOutline, peopleOutline, personCircleOutline, starOutline, timeOutline } from 'ionicons/icons';
import { authService } from '../api/authService';
import { professionalService } from '../api/resourcesService';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import { ProfessionalModal } from '../components/resources/ProfessionalModal';
import { useAuth } from '../hooks/useAuth';
import type { User } from '../types/auth';
import type { Professional } from '../types/resources';
import { getProfessionalId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

type DisplayResource = Professional & {
  source: 'professional' | 'auth-user';
  authUserId?: string;
  userName?: string;
};

function mapUserToResource(user: User): DisplayResource {
  return {
    resourceId: 0,
    authUserId: user.id,
    userName: user.userName,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    roleName: 'Usuario registrado',
    status: user.enabled && user.status === 'ACTIVE' ? 'ACTIVE' : 'INACTIVE',
    createdAt: user.createdAt,
    updatedAt: user.updatedAt,
    source: 'auth-user',
  };
}

export function ResourcesPage() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [authUsers, setAuthUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [warning, setWarning] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [selectedPro, setSelectedPro] = useState<Professional | null>(null);
  const [initialProfessionalData, setInitialProfessionalData] = useState<Partial<Professional> | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [proToDelete, setProToDelete] = useState<Professional | null>(null);

  const loadResources = async () => {
    setLoading(true);
    setError(null);
    setWarning(null);

    const [professionalResult, userResult] = await Promise.allSettled([
      professionalService.getAll(),
      authService.getAllUsers(),
    ]);

    if (professionalResult.status === 'fulfilled') {
      setProfessionals(professionalResult.value);
    } else {
      setProfessionals([]);
      setError(professionalResult.reason instanceof Error ? professionalResult.reason.message : 'No se pudieron cargar los profesionales');
    }

    if (userResult.status === 'fulfilled') {
      setAuthUsers(userResult.value);
    } else {
      setAuthUsers([]);
      setWarning('No se pudieron cargar los usuarios registrados desde Auth. Revisa la ruta /api/users en el BFF.');
    }

    setLoading(false);
  };

  useEffect(() => { loadResources(); }, []);

  const displayResources = useMemo<DisplayResource[]>(() => {
    const professionalEmails = new Set(
      professionals
        .map((pro) => pro.email?.trim().toLowerCase())
        .filter(Boolean),
    );

    const professionalItems = professionals.map((pro) => ({
      ...pro,
      source: 'professional' as const,
    }));

    const authOnlyItems = authUsers
      .filter((user) => !professionalEmails.has(user.email?.trim().toLowerCase()))
      .map(mapUserToResource);

    return [...authOnlyItems, ...professionalItems];
  }, [professionals, authUsers]);

  const filtered = displayResources.filter((resource) => {
    const text = `${resource.firstName ?? ''} ${resource.lastName ?? ''} ${resource.email ?? ''} ${resource.roleName ?? ''} ${resource.userName ?? ''}`.toLowerCase();
    const matchSearch = text.includes(search.toLowerCase());
    const matchStatus = filterStatus === 'ALL' || resource.status === filterStatus;
    return matchSearch && matchStatus;
  });

  const openEmptyModal = () => {
    setSelectedPro(null);
    setInitialProfessionalData(null);
    setShowModal(true);
  };

  const openProfessionalModalFromUser = (userResource: DisplayResource) => {
    setSelectedPro(null);
    setInitialProfessionalData({
      firstName: userResource.firstName,
      lastName: userResource.lastName,
      email: userResource.email,
      roleName: '',
      seniority: 'MID',
      location: 'santiago',
      timeZone: 'America/Santiago',
      weeklyCapacityHours: 40,
      status: 'ACTIVE',
    });
    setShowModal(true);
  };

  const handleDelete = async () => {
    if (!proToDelete) return;
    const id = getProfessionalId(proToDelete as Professional & Record<string, unknown>);
    if (!id) {
      setError('No se encontró el ID del profesional para eliminar.');
      setProToDelete(null);
      return;
    }

    try {
      await professionalService.delete(id);
      setProToDelete(null);
      await loadResources();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el profesional');
      setProToDelete(null);
    }
  };

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando recursos...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title"><IonIcon icon={peopleOutline} />Recursos Humanos</h1>
          <p className="page-subtitle">
            {displayResources.length} recursos visibles · {professionals.length} profesionales · {authUsers.length} usuarios registrados
          </p>
        </div>
        {isAdmin && <IonButton type="button" onClick={openEmptyModal}>+ Agregar profesional</IonButton>}
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}
      {warning && <IonText color="warning"><p>{warning}</p></IonText>}

      <IonCard className="app-card">
        <IonCardContent className="form-grid">
          <IonItem><IonInput label="Buscar" labelPlacement="stacked" placeholder="Nombre, usuario, email o rol..." value={search} onIonInput={(e) => setSearch(String(e.detail.value ?? ''))} /></IonItem>
          <IonItem>
            <IonSelect label="Estado" labelPlacement="stacked" value={filterStatus} onIonChange={(e) => setFilterStatus(String(e.detail.value))}>
              <IonSelectOption value="ALL">Todos</IonSelectOption>
              <IonSelectOption value="ACTIVE">Activo</IonSelectOption>
              <IonSelectOption value="INACTIVE">Inactivo</IonSelectOption>
              <IonSelectOption value="ON_LEAVE">Con licencia</IonSelectOption>
            </IonSelect>
          </IonItem>
        </IonCardContent>
      </IonCard>

      {filtered.length === 0 ? (
        <IonCard className="app-card ion-text-center"><IonCardContent><p className="muted">No hay recursos para mostrar.</p></IonCardContent></IonCard>
      ) : (
        <div className="card-grid">
          {filtered.map((resource) => {
            const id = getProfessionalId(resource as unknown as Professional & Record<string, unknown>);
            const key = resource.source === 'auth-user' ? `user-${resource.authUserId}` : `pro-${id ?? resource.email}`;
            return (
              <IonCard key={key} className="app-card accent-card">
                <IonCardHeader>
                  <div className="card-title-row">
                    <div className="resource-avatar">
                      {resource.firstName?.charAt(0)}{resource.lastName?.charAt(0)}
                    </div>
                    <div className="card-title-main">
                      <IonCardTitle>{resource.firstName} {resource.lastName}</IonCardTitle>
                      <p className="muted">{resource.roleName ?? 'Sin rol'}{resource.userName ? ` · @${resource.userName}` : ''}</p>
                    </div>
                    <div className="button-row">
                      <IonBadge color={resource.source === 'professional' ? 'primary' : 'medium'}>
                        {resource.source === 'professional' ? 'Profesional' : 'Usuario'}
                      </IonBadge>
                      <IonBadge color={resource.status === 'ACTIVE' ? 'success' : resource.status === 'ON_LEAVE' ? 'warning' : 'medium'}>
                        {formatStatus(resource.status)}
                      </IonBadge>
                    </div>
                  </div>
                </IonCardHeader>
                <IonCardContent>
                  <div className="card-info-list">
                    {resource.email && (
                      <span className="card-info-row">
                        <IonIcon icon={mailOutline} />
                        {resource.email}
                      </span>
                    )}
                    {resource.seniority && (
                      <span className="card-info-row">
                        <IonIcon icon={starOutline} />
                        {resource.seniority}
                      </span>
                    )}
                    {resource.weeklyCapacityHours && (
                      <span className="card-info-row">
                        <IonIcon icon={timeOutline} />
                        {resource.weeklyCapacityHours}h / semana
                      </span>
                    )}
                    {resource.location && (
                      <span className="card-info-row">
                        <IonIcon icon={locationOutline} />
                        {resource.location}
                      </span>
                    )}
                    {resource.source === 'auth-user' && (
                      <span className="card-info-row muted">
                        <IonIcon icon={personCircleOutline} />
                        Usuario creado desde registro. Para asignarlo a proyectos, crea su ficha profesional.
                      </span>
                    )}
                  </div>
                  {isAdmin && (
                    <div className="card-actions">
                      {resource.source === 'professional' ? (
                        <>
                          <IonButton type="button" size="small" fill="solid" color="primary" onClick={() => { setSelectedPro(resource); setInitialProfessionalData(null); setShowModal(true); }}>
                            Editar
                          </IonButton>
                          <IonButton type="button" size="small" fill="outline" color="danger" onClick={() => setProToDelete(resource)}>
                            Eliminar
                          </IonButton>
                        </>
                      ) : (
                        <IonButton type="button" size="small" fill="solid" color="primary" onClick={() => openProfessionalModalFromUser(resource)}>
                          Crear ficha profesional
                        </IonButton>
                      )}
                    </div>
                  )}
                </IonCardContent>
              </IonCard>
            );
          })}
        </div>
      )}

      {showModal && (
        <ProfessionalModal
          professional={selectedPro}
          initialData={initialProfessionalData}
          onClose={() => setShowModal(false)}
          onSave={() => { setShowModal(false); setInitialProfessionalData(null); loadResources(); }}
        />
      )}
      {proToDelete && <ConfirmModal title={`Eliminar a ${proToDelete.firstName} ${proToDelete.lastName}`} message="Esta acción eliminará el profesional permanentemente." confirmLabel="Eliminar" danger onConfirm={handleDelete} onCancel={() => setProToDelete(null)} />}
    </div>
  );
}
