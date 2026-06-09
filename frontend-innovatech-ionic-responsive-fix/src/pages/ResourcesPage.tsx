import { useEffect, useState } from 'react';
import { IonBadge, IonButton, IonCard, IonCardContent, IonInput, IonItem, IonLabel, IonSelect, IonSelectOption, IonSpinner, IonText } from '@ionic/react';
import { professionalService } from '../api/resourcesService';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import { ProfessionalModal } from '../components/resources/ProfessionalModal';
import type { Professional } from '../types/resources';
import { getProfessionalId } from '../utils/ids';

export function ResourcesPage() {
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [selectedPro, setSelectedPro] = useState<Professional | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [proToDelete, setProToDelete] = useState<Professional | null>(null);

  const loadProfessionals = async () => {
    setLoading(true);
    setError(null);
    try {
      setProfessionals(await professionalService.getAll());
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudieron cargar los profesionales');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadProfessionals(); }, []);

  const filtered = professionals.filter((pro) => {
    const text = `${pro.firstName ?? ''} ${pro.lastName ?? ''} ${pro.email ?? ''} ${pro.roleName ?? ''}`.toLowerCase();
    const matchSearch = text.includes(search.toLowerCase());
    const matchStatus = filterStatus === 'ALL' || pro.status === filterStatus;
    return matchSearch && matchStatus;
  });

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
      await loadProfessionals();
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
          <h1 className="page-title">👥 Recursos Humanos</h1>
          <p className="page-subtitle">{professionals.length} profesionales registrados</p>
        </div>
        <IonButton type="button" onClick={() => { setSelectedPro(null); setShowModal(true); }}>+ Agregar profesional</IonButton>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}

      <IonCard className="app-card">
        <IonCardContent className="form-grid">
          <IonItem><IonInput label="Buscar" labelPlacement="stacked" placeholder="Nombre, email o rol..." value={search} onIonInput={(e) => setSearch(String(e.detail.value ?? ''))} /></IonItem>
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
        <IonCard className="app-card ion-text-center"><IonCardContent><p className="muted">No hay profesionales para mostrar.</p></IonCardContent></IonCard>
      ) : (
        <div className="card-grid">
          {filtered.map((pro) => {
            const id = getProfessionalId(pro as Professional & Record<string, unknown>);
            return (
              <IonCard key={id ?? pro.email} className="app-card">
                <IonCardContent>
                  <IonItem lines="none">
                    <div slot="start" style={{ width: 42, height: 42, borderRadius: 14, background: '#eef2ff', display: 'grid', placeItems: 'center', color: '#4f46e5', fontWeight: 800 }}>
                      {pro.firstName?.charAt(0)}{pro.lastName?.charAt(0)}
                    </div>
                    <IonLabel>
                      <h2>{pro.firstName} {pro.lastName}</h2>
                      <p>{pro.roleName ?? 'Sin rol'} · {pro.email}</p>
                    </IonLabel>
                    <IonBadge color={pro.status === 'ACTIVE' ? 'success' : pro.status === 'ON_LEAVE' ? 'warning' : 'medium'} slot="end">{pro.status}</IonBadge>
                  </IonItem>
                  <p><strong>ID:</strong> {id ?? '—'}</p>
                  <p><strong>Seniority:</strong> {pro.seniority ?? '—'}</p>
                  <p><strong>Capacidad:</strong> {pro.weeklyCapacityHours ?? '—'}h/semana</p>
                  <p><strong>Ubicación:</strong> {pro.location ?? '—'}</p>
                  <div className="button-row">
                    <IonButton type="button" fill="outline" onClick={() => { setSelectedPro(pro); setShowModal(true); }}>Editar</IonButton>
                    <IonButton type="button" fill="outline" color="danger" onClick={() => setProToDelete(pro)}>Eliminar</IonButton>
                  </div>
                </IonCardContent>
              </IonCard>
            );
          })}
        </div>
      )}

      {showModal && <ProfessionalModal professional={selectedPro} onClose={() => setShowModal(false)} onSave={() => { setShowModal(false); loadProfessionals(); }} />}
      {proToDelete && <ConfirmModal title={`Eliminar a ${proToDelete.firstName} ${proToDelete.lastName}`} message="Esta acción eliminará el profesional permanentemente." confirmLabel="Eliminar" danger onConfirm={handleDelete} onCancel={() => setProToDelete(null)} />}
    </div>
  );
}
