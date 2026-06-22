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
import { widgetService } from '../../api/analyticsService';
import { WIDGET_TYPE_LABELS } from '../../types/analytics';
import type { KpiDefinition, Widget } from '../../types/analytics';

interface WidgetModalProps {
  widget: Widget | null;
  kpis: KpiDefinition[];
  onClose: () => void;
  onSave: () => void;
}

export function WidgetModal({ widget, kpis, onClose, onSave }: WidgetModalProps) {
  const [form, setForm] = useState({
    title: '',
    widgetType: 'CARD',
    sourceKpiCode: kpis[0]?.code ?? '',
    isActive: true,
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (widget) {
      setForm({
        title: widget.title ?? '',
        widgetType: String(widget.widgetType ?? 'CARD'),
        sourceKpiCode: widget.sourceKpiCode ?? kpis[0]?.code ?? '',
        isActive: widget.isActive ?? true,
      });
    }
  }, [widget, kpis]);

  const setField = <K extends keyof typeof form>(name: K, value: typeof form[K]) =>
    setForm((previous) => ({ ...previous, [name]: value }));

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (widget) {
        await widgetService.update(widget.widgetId, form);
      } else {
        await widgetService.create(form);
      }
      onSave();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el widget');
    } finally {
      setSaving(false);
    }
  };

  return (
    <IonModal isOpen onDidDismiss={onClose}>
      <IonHeader>
        <IonToolbar>
          <IonTitle>{widget ? 'Editar widget' : 'Nuevo widget'}</IonTitle>
          <IonButtons slot="end">
            <IonButton type="button" fill="clear" onClick={onClose}>Cerrar</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        <p className="muted">Un widget toma los datos de un KPI y los muestra en el dashboard con el tipo de gráfico que elijas.</p>
        {error && <IonText color="danger"><p>{error}</p></IonText>}
        {kpis.length === 0 && <IonText color="warning"><p>Primero crea un KPI — un widget siempre necesita uno de origen.</p></IonText>}
        <form onSubmit={handleSubmit}>
          <IonList inset>
            <IonItem>
              <IonInput label="Título" labelPlacement="stacked" placeholder="ej. Avance semanal" value={form.title} required onIonInput={(e) => setField('title', String(e.detail.value ?? ''))} />
              <IonNote slot="helper">El nombre que verás arriba de la tarjeta/gráfico.</IonNote>
            </IonItem>
            <IonItem>
              <IonSelect label="Tipo de widget" labelPlacement="stacked" value={form.widgetType} onIonChange={(e) => setField('widgetType', String(e.detail.value))}>
                {Object.entries(WIDGET_TYPE_LABELS).map(([value, label]) => <IonSelectOption key={value} value={value}>{label}</IonSelectOption>)}
              </IonSelect>
              <IonNote slot="helper">Cómo se va a visualizar el dato: tarjeta de número único, gráfico o tabla.</IonNote>
            </IonItem>
            <IonItem>
              <IonSelect label="KPI de origen" labelPlacement="stacked" value={form.sourceKpiCode} onIonChange={(e) => setField('sourceKpiCode', String(e.detail.value))}>
                {kpis.map((kpi) => <IonSelectOption key={kpi.code} value={kpi.code}>{kpi.name} ({kpi.code})</IonSelectOption>)}
              </IonSelect>
              <IonNote slot="helper">De qué KPI viene el dato que se va a graficar.</IonNote>
            </IonItem>
            <IonItem>
              <IonToggle checked={form.isActive} onIonChange={(e) => setField('isActive', e.detail.checked)}>Activo</IonToggle>
              <IonNote slot="helper">Si lo apagas, queda marcado como inactivo en el dashboard.</IonNote>
            </IonItem>
          </IonList>
          <IonButton expand="block" type="submit" disabled={saving || !form.sourceKpiCode}>{saving ? 'Guardando...' : widget ? 'Guardar cambios' : 'Crear widget'}</IonButton>
        </form>
      </IonContent>
    </IonModal>
  );
}
