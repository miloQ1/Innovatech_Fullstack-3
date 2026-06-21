import type * as React from 'react';
import { useMemo, useState } from 'react';
import {
  IonBadge,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonSelect,
  IonSelectOption,
  IonText,
} from '@ionic/react';
import { assignmentService } from '../../../api/resourcesService';
import type { Assignment, AssignmentRequest, Professional } from '../../../types/resources';
import { getProfessionalId } from '../../../utils/ids';
import { formatStatus } from '../../../utils/formatStatus';

interface AssignmentsTabProps {
  projectId: number;
  assignments: Assignment[];
  professionals: Professional[];
  onReload: () => void;
}

const statusOptions = ['ACTIVE', 'PLANNED', 'COMPLETED', 'CANCELLED'];

function professionalName(professional?: Professional) {
  if (!professional) return 'Recurso no encontrado';
  const name = `${professional.firstName ?? ''} ${professional.lastName ?? ''}`.trim();
  return name || professional.email || `Recurso #${getProfessionalId(professional as Professional & Record<string, unknown>) ?? ''}`;
}

export function AssignmentsTab({ projectId, assignments, professionals, onReload }: AssignmentsTabProps) {
  const [showForm, setShowForm] = useState(false);
  const [resourceId, setResourceId] = useState<number | null>(null);
  const [projectRole, setProjectRole] = useState('Developer');
  const [allocationPct, setAllocationPct] = useState(100);
  const [plannedHours, setPlannedHours] = useState(40);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [assignmentStatus, setAssignmentStatus] = useState('ACTIVE');
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger'; text: string } | null>(null);

  const professionalsById = useMemo(() => new Map(
    professionals
      .map((item) => [getProfessionalId(item as Professional & Record<string, unknown>), item] as const)
      .filter(([id]) => !!id),
  ), [professionals]);

  const resetForm = () => {
    setResourceId(null);
    setProjectRole('Developer');
    setAllocationPct(100);
    setPlannedHours(40);
    setStartDate('');
    setEndDate('');
    setAssignmentStatus('ACTIVE');
  };

  const handleCreate = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!resourceId) {
      setMessage({ type: 'danger', text: 'Selecciona un profesional.' });
      return;
    }

    setSaving(true);
    setMessage(null);
    try {
      const payload: AssignmentRequest = {
        resourceId,
        projectId,
        projectRole: projectRole || undefined,
        allocationPct,
        plannedHours,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
        assignmentStatus,
      };
      await assignmentService.create(payload);
      resetForm();
      setShowForm(false);
      setMessage({ type: 'success', text: 'Asignación creada correctamente.' });
      onReload();
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear la asignación.' });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (assignmentId: number) => {
    setMessage(null);
    try {
      await assignmentService.delete(assignmentId);
      onReload();
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo eliminar la asignación.' });
    }
  };

  return (
    <IonCard className="app-card">
      <IonCardHeader>
        <div className="card-title-row">
          <IonCardTitle className="section-title">Asignaciones del proyecto</IonCardTitle>
          <IonButton type="button" size="small" fill="outline" onClick={() => setShowForm((value) => !value)}>
            {showForm ? 'Cancelar' : '+ Asignar recurso'}
          </IonButton>
        </div>
      </IonCardHeader>
      <IonCardContent>
        {message && <IonText color={message.type}><p>{message.text}</p></IonText>}

        {showForm && (
          <form onSubmit={handleCreate} className="form-grid ion-margin-bottom">
            <IonItem>
              <IonSelect
                label="Profesional"
                labelPlacement="stacked"
                value={resourceId ?? ''}
                required
                onIonChange={(event) => setResourceId(event.detail.value ? Number(event.detail.value) : null)}
              >
                <IonSelectOption value="">Selecciona un profesional...</IonSelectOption>
                {professionals.map((professional) => {
                  const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                  return id ? <IonSelectOption key={id} value={id}>{professionalName(professional)}</IonSelectOption> : null;
                })}
              </IonSelect>
            </IonItem>
            <IonItem>
              <IonInput label="Rol en proyecto" labelPlacement="stacked" value={projectRole} onIonInput={(e) => setProjectRole(String(e.detail.value ?? ''))} />
            </IonItem>
            <IonItem>
              <IonInput label="Asignación %" labelPlacement="stacked" type="number" value={allocationPct} onIonInput={(e) => setAllocationPct(Number(e.detail.value ?? 100))} />
            </IonItem>
            <IonItem>
              <IonInput label="Horas planificadas" labelPlacement="stacked" type="number" value={plannedHours} onIonInput={(e) => setPlannedHours(Number(e.detail.value ?? 40))} />
            </IonItem>
            <IonItem>
              <IonInput label="Inicio" labelPlacement="stacked" type="date" value={startDate} onIonInput={(e) => setStartDate(String(e.detail.value ?? ''))} />
            </IonItem>
            <IonItem>
              <IonInput label="Término" labelPlacement="stacked" type="date" value={endDate} onIonInput={(e) => setEndDate(String(e.detail.value ?? ''))} />
            </IonItem>
            <IonItem>
              <IonSelect label="Estado" labelPlacement="stacked" value={assignmentStatus} onIonChange={(e) => setAssignmentStatus(String(e.detail.value))}>
                {statusOptions.map((status) => <IonSelectOption key={status} value={status}>{formatStatus(status)}</IonSelectOption>)}
              </IonSelect>
            </IonItem>
            <IonButton type="submit" disabled={saving || !resourceId}>{saving ? 'Guardando...' : 'Guardar asignación'}</IonButton>
          </form>
        )}

        {assignments.length === 0 ? (
          <p className="muted">Este proyecto aún no tiene recursos asignados.</p>
        ) : (
          <IonList inset>
            {assignments.map((assignment) => {
              const professional = professionalsById.get(assignment.resourceId);
              return (
                <IonItem key={assignment.assignmentId}>
                  <IonLabel>
                    <h2>{professionalName(professional)}</h2>
                    <p>{assignment.projectRole ?? 'Rol no definido'} · {assignment.allocationPct ?? 0}% · {assignment.plannedHours ?? 0}h</p>
                    <p className="muted">{assignment.startDate ?? 'Sin inicio'} → {assignment.endDate ?? 'Sin término'}</p>
                  </IonLabel>
                  <IonBadge color={assignment.assignmentStatus === 'COMPLETED' ? 'success' : assignment.assignmentStatus === 'CANCELLED' ? 'danger' : 'primary'} slot="end">
                    {formatStatus(assignment.assignmentStatus ?? 'ACTIVE')}
                  </IonBadge>
                  <IonButton slot="end" size="small" fill="clear" color="danger" onClick={() => handleDelete(assignment.assignmentId)}>
                    Quitar
                  </IonButton>
                </IonItem>
              );
            })}
          </IonList>
        )}
      </IonCardContent>
    </IonCard>
  );
}
