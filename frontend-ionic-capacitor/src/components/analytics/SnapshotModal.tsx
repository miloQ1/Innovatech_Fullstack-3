import type * as React from 'react';
import { useState } from 'react';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonModal,
  IonNote,
  IonSelect,
  IonSelectOption,
  IonText,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { kpiSnapshotService } from '../../api/analyticsService';
import { SCOPE_TYPE_LABELS } from '../../types/analytics';
import type { KpiDefinition } from '../../types/analytics';

interface SnapshotModalProps {
  kpi: KpiDefinition;
  onClose: () => void;
  onSave: () => void;
}

export function SnapshotModal({ kpi, onClose, onSave }: SnapshotModalProps) {
  const today = new Date().toISOString().slice(0, 10);
  const [form, setForm] = useState({
    scopeType: 'GLOBAL',
    periodStart: today,
    periodEnd: today,
    numericValue: 0,
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const setField = <K extends keyof typeof form>(name: K, value: typeof form[K]) =>
    setForm((previous) => ({ ...previous, [name]: value }));

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await kpiSnapshotService.create(kpi.kpiId, form);
      onSave();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el dato');
    } finally {
      setSaving(false);
    }
  };

  return (
    <IonModal isOpen onDidDismiss={onClose}>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Nuevo dato — {kpi.name}</IonTitle>
          <IonButtons slot="end">
            <IonButton type="button" fill="clear" onClick={onClose}>Cerrar</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        <p className="muted">Agrega un valor puntual para este KPI en un período de tiempo. Cada dato que agregues acá es un punto en el gráfico del widget.</p>
        {error && <IonText color="danger"><p>{error}</p></IonText>}
        <form onSubmit={handleSubmit}>
          <IonList inset>
            <IonItem>
              <IonSelect label="Alcance" labelPlacement="stacked" value={form.scopeType} onIonChange={(e) => setField('scopeType', String(e.detail.value))}>
                {Object.entries(SCOPE_TYPE_LABELS).map(([value, label]) => <IonSelectOption key={value} value={value}>{label}</IonSelectOption>)}
              </IonSelect>
              <IonNote slot="helper">A qué alcance aplica este dato. Para la mayoría de los casos, "Global" está bien.</IonNote>
            </IonItem>
            <IonItem>
              <IonInput label="Inicio del período" labelPlacement="stacked" type="date" value={form.periodStart} required onIonInput={(e) => setField('periodStart', String(e.detail.value ?? ''))} />
              <IonNote slot="helper">Desde cuándo aplica este dato (ej. inicio de la semana medida).</IonNote>
            </IonItem>
            <IonItem>
              <IonInput label="Fin del período" labelPlacement="stacked" type="date" value={form.periodEnd} required onIonInput={(e) => setField('periodEnd', String(e.detail.value ?? ''))} />
              <IonNote slot="helper">Hasta cuándo aplica. Esta fecha es la que se usa como eje X en los gráficos.</IonNote>
            </IonItem>
            <IonItem>
              <IonInput label={`Valor${kpi.unit ? ` (${kpi.unit})` : ''}`} labelPlacement="stacked" type="number" value={form.numericValue} required onIonInput={(e) => setField('numericValue', Number(e.detail.value ?? 0))} />
              <IonNote slot="helper">El número que se va a graficar para este período.</IonNote>
            </IonItem>
          </IonList>
          <IonButton expand="block" type="submit" disabled={saving}>{saving ? 'Guardando...' : 'Agregar dato'}</IonButton>
        </form>
      </IonContent>
    </IonModal>
  );
}
