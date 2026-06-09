import type * as React from 'react';
import { useEffect, useState } from 'react';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonModal,
  IonSelect,
  IonSelectOption,
  IonText,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { professionalService } from '../../api/resourcesService';
import type { Professional } from '../../types/resources';
import { getProfessionalId } from '../../utils/ids';

interface ProfessionalModalProps {
  professional: Professional | null;
  onClose: () => void;
  onSave: () => void;
}

export function ProfessionalModal({ professional, onClose, onSave }: ProfessionalModalProps) {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    employeeCode: '',
    roleName: '',
    seniority: 'MID',
    location: '',
    timeZone: 'America/Santiago',
    weeklyCapacityHours: 40,
    status: 'ACTIVE',
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (professional) {
      setForm({
        firstName: professional.firstName ?? '',
        lastName: professional.lastName ?? '',
        email: professional.email ?? '',
        employeeCode: professional.employeeCode ?? '',
        roleName: professional.roleName ?? '',
        seniority: String(professional.seniority ?? 'MID'),
        location: professional.location ?? '',
        timeZone: professional.timeZone ?? 'America/Santiago',
        weeklyCapacityHours: professional.weeklyCapacityHours ?? 40,
        status: String(professional.status ?? 'ACTIVE'),
      });
    }
  }, [professional]);

  const setField = (name: keyof typeof form, value: string | number) => setForm((previous) => ({ ...previous, [name]: value }));

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (professional) {
        const id = getProfessionalId(professional as Professional & Record<string, unknown>);
        if (!id) throw new Error('No se encontró el ID del profesional para editar.');
        await professionalService.update(id, form);
      } else {
        await professionalService.create(form);
      }
      onSave();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el profesional');
    } finally {
      setSaving(false);
    }
  };

  return (
    <IonModal isOpen onDidDismiss={onClose}>
      <IonHeader>
        <IonToolbar>
          <IonTitle>{professional ? 'Editar profesional' : 'Nuevo profesional'}</IonTitle>
          <IonButtons slot="end">
            <IonButton type="button" fill="clear" onClick={onClose}>Cerrar</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        {error && <IonText color="danger"><p>{error}</p></IonText>}
        <form onSubmit={handleSubmit}>
          <IonList inset>
            <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={form.firstName} required onIonInput={(e) => setField('firstName', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem><IonInput label="Apellido" labelPlacement="stacked" value={form.lastName} required onIonInput={(e) => setField('lastName', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem><IonInput label="Email" labelPlacement="stacked" type="email" value={form.email} required onIonInput={(e) => setField('email', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem><IonInput label="Código empleado" labelPlacement="stacked" value={form.employeeCode} onIonInput={(e) => setField('employeeCode', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem><IonInput label="Rol" labelPlacement="stacked" value={form.roleName} onIonInput={(e) => setField('roleName', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem>
              <IonSelect label="Seniority" labelPlacement="stacked" value={form.seniority} onIonChange={(e) => setField('seniority', String(e.detail.value))}>
                {['JUNIOR', 'MID', 'SENIOR', 'LEAD', 'PRINCIPAL'].map((item) => <IonSelectOption key={item} value={item}>{item}</IonSelectOption>)}
              </IonSelect>
            </IonItem>
            <IonItem>
              <IonSelect label="Estado" labelPlacement="stacked" value={form.status} onIonChange={(e) => setField('status', String(e.detail.value))}>
                <IonSelectOption value="ACTIVE">Activo</IonSelectOption>
                <IonSelectOption value="INACTIVE">Inactivo</IonSelectOption>
                <IonSelectOption value="ON_LEAVE">Con licencia</IonSelectOption>
              </IonSelect>
            </IonItem>
            <IonItem><IonInput label="Ubicación" labelPlacement="stacked" value={form.location} onIonInput={(e) => setField('location', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem><IonInput label="Zona horaria" labelPlacement="stacked" value={form.timeZone} onIonInput={(e) => setField('timeZone', String(e.detail.value ?? ''))} /></IonItem>
            <IonItem><IonInput label="Capacidad semanal" labelPlacement="stacked" type="number" value={form.weeklyCapacityHours} onIonInput={(e) => setField('weeklyCapacityHours', Number(e.detail.value ?? 40))} /></IonItem>
          </IonList>
          <IonButton expand="block" type="submit" disabled={saving}>{saving ? 'Guardando...' : professional ? 'Guardar cambios' : 'Crear profesional'}</IonButton>
        </form>
      </IonContent>
    </IonModal>
  );
}
