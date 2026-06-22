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
  IonNote,
  IonSelect,
  IonSelectOption,
  IonText,
  IonTitle,
  IonToggle,
  IonToolbar,
} from '@ionic/react';
import { kpiService } from '../../api/analyticsService';
import { FORMULA_TYPE_LABELS, REFRESH_FREQUENCY_LABELS } from '../../types/analytics';
import type { KpiDefinition } from '../../types/analytics';

interface KpiModalProps {
  kpi: KpiDefinition | null;
  onClose: () => void;
  onSave: () => void;
}

export function KpiModal({ kpi, onClose, onSave }: KpiModalProps) {
  const [form, setForm] = useState({
    code: '',
    name: '',
    formulaType: 'PROGRESS',
    unit: '',
    refreshFrequency: 'DAILY',
    isActive: true,
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (kpi) {
      setForm({
        code: kpi.code ?? '',
        name: kpi.name ?? '',
        formulaType: String(kpi.formulaType ?? 'PROGRESS'),
        unit: kpi.unit ?? '',
        refreshFrequency: String(kpi.refreshFrequency ?? 'DAILY'),
        isActive: kpi.isActive ?? true,
      });
    }
  }, [kpi]);

  const setField = <K extends keyof typeof form>(name: K, value: typeof form[K]) =>
    setForm((previous) => ({ ...previous, [name]: value }));

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (kpi) {
        await kpiService.update(kpi.kpiId, form);
      } else {
        await kpiService.create(form);
      }
      onSave();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el KPI');
    } finally {
      setSaving(false);
    }
  };

  return (
    <IonModal isOpen onDidDismiss={onClose}>
      <IonHeader>
        <IonToolbar>
          <IonTitle>{kpi ? 'Editar KPI' : 'Nuevo KPI'}</IonTitle>
          <IonButtons slot="end">
            <IonButton type="button" fill="clear" onClick={onClose}>Cerrar</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        <p className="muted">Un KPI es la métrica que vas a graficar después en un widget (ej. "Avance de proyectos"). El código es un identificador interno corto; el nombre es lo que se ve en pantalla.</p>
        {error && <IonText color="danger"><p>{error}</p></IonText>}
        <form onSubmit={handleSubmit}>
          <IonList inset>
            <IonItem>
              <IonInput label="Código" labelPlacement="stacked" placeholder="ej. PROJ_PROGRESS" value={form.code} required disabled={!!kpi} onIonInput={(e) => setField('code', String(e.detail.value ?? ''))} />
              <IonNote slot="helper">Identificador interno, sin espacios. Los widgets lo usan para saber qué KPI graficar. No se puede editar después.</IonNote>
            </IonItem>
            <IonItem>
              <IonInput label="Nombre" labelPlacement="stacked" placeholder="ej. Avance de proyectos" value={form.name} required onIonInput={(e) => setField('name', String(e.detail.value ?? ''))} />
              <IonNote slot="helper">El texto que se muestra en la página de Analítica.</IonNote>
            </IonItem>
            <IonItem>
              <IonSelect label="Tipo de fórmula" labelPlacement="stacked" value={form.formulaType} onIonChange={(e) => setField('formulaType', String(e.detail.value))}>
                {Object.entries(FORMULA_TYPE_LABELS).map(([value, label]) => <IonSelectOption key={value} value={value}>{label}</IonSelectOption>)}
              </IonSelect>
              <IonNote slot="helper">Categoría descriptiva del KPI, no calcula nada automático todavía.</IonNote>
            </IonItem>
            <IonItem>
              <IonInput label="Unidad" labelPlacement="stacked" placeholder="%, hrs, etc." value={form.unit} onIonInput={(e) => setField('unit', String(e.detail.value ?? ''))} />
              <IonNote slot="helper">En qué se mide el valor (opcional). Ejemplo: % para porcentaje.</IonNote>
            </IonItem>
            <IonItem>
              <IonSelect label="Frecuencia de actualización" labelPlacement="stacked" value={form.refreshFrequency} onIonChange={(e) => setField('refreshFrequency', String(e.detail.value))}>
                {Object.entries(REFRESH_FREQUENCY_LABELS).map(([value, label]) => <IonSelectOption key={value} value={value}>{label}</IonSelectOption>)}
              </IonSelect>
              <IonNote slot="helper">Cada cuánto se espera actualizar este dato. Por ahora es solo informativo.</IonNote>
            </IonItem>
            <IonItem>
              <IonToggle checked={form.isActive} onIonChange={(e) => setField('isActive', e.detail.checked)}>Activo</IonToggle>
              <IonNote slot="helper">Si lo apagas, el KPI deja de estar disponible para crear widgets nuevos.</IonNote>
            </IonItem>
          </IonList>
          <IonButton expand="block" type="submit" disabled={saving}>{saving ? 'Guardando...' : kpi ? 'Guardar cambios' : 'Crear KPI'}</IonButton>
        </form>
      </IonContent>
    </IonModal>
  );
}
