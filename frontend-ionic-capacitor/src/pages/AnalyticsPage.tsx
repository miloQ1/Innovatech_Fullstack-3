import { useEffect, useState } from 'react';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonIcon, IonSpinner, IonText } from '@ionic/react';
import { addCircleOutline, statsChartOutline } from 'ionicons/icons';
import { kpiService, widgetService } from '../api/analyticsService';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import { KpiModal } from '../components/analytics/KpiModal';
import { SnapshotModal } from '../components/analytics/SnapshotModal';
import { WidgetModal } from '../components/analytics/WidgetModal';
import { WidgetCard } from '../components/analytics/WidgetCard';
import { FORMULA_TYPE_LABELS, REFRESH_FREQUENCY_LABELS, translateLabel } from '../types/analytics';
import type { KpiDefinition, Widget } from '../types/analytics';

export function AnalyticsPage() {
  const [kpis, setKpis] = useState<KpiDefinition[]>([]);
  const [widgets, setWidgets] = useState<Widget[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [selectedKpi, setSelectedKpi] = useState<KpiDefinition | null>(null);
  const [showKpiModal, setShowKpiModal] = useState(false);
  const [kpiToDelete, setKpiToDelete] = useState<KpiDefinition | null>(null);
  const [snapshotKpi, setSnapshotKpi] = useState<KpiDefinition | null>(null);

  const [selectedWidget, setSelectedWidget] = useState<Widget | null>(null);
  const [showWidgetModal, setShowWidgetModal] = useState(false);
  const [widgetToDelete, setWidgetToDelete] = useState<Widget | null>(null);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [kpiResult, widgetResult] = await Promise.allSettled([kpiService.getAll(), widgetService.getAll()]);
      setKpis(kpiResult.status === 'fulfilled' ? kpiResult.value : []);
      setWidgets(widgetResult.status === 'fulfilled' ? widgetResult.value : []);
      if (kpiResult.status === 'rejected' || widgetResult.status === 'rejected') {
        setError('Algunos datos no se pudieron cargar. Revisa que el BFF y analitica-service estén levantados.');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo cargar la analítica');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleDeleteKpi = async () => {
    if (!kpiToDelete) return;
    try {
      await kpiService.delete(kpiToDelete.kpiId);
      setKpiToDelete(null);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el KPI');
      setKpiToDelete(null);
    }
  };

  const handleDeleteWidget = async () => {
    if (!widgetToDelete) return;
    try {
      await widgetService.delete(widgetToDelete.widgetId);
      setWidgetToDelete(null);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el widget');
      setWidgetToDelete(null);
    }
  };

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando analítica...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title"><IonIcon icon={statsChartOutline} />Analítica</h1>
          <p className="page-subtitle">KPIs, datos históricos y widgets del dashboard.</p>
        </div>
        <IonButton type="button" onClick={() => { setSelectedKpi(null); setShowKpiModal(true); }}>+ Nuevo KPI</IonButton>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}

      <div className="section-bg">
      <IonCard className="app-card">
        <IonCardHeader><IonCardTitle className="section-title">KPIs</IonCardTitle></IonCardHeader>
        <IonCardContent>
          {kpis.length === 0 ? (
            <p className="muted">Aún no hay KPIs creados. Crea el primero para poder armar widgets.</p>
          ) : kpis.map((kpi) => (
            <div key={kpi.kpiId} className="list-row">
              <div>
                <strong>{kpi.name}</strong>
                <p className="muted">{kpi.code} · {translateLabel(FORMULA_TYPE_LABELS, String(kpi.formulaType))} · {translateLabel(REFRESH_FREQUENCY_LABELS, String(kpi.refreshFrequency))}{kpi.unit ? ` · ${kpi.unit}` : ''}</p>
              </div>
              <div className="button-row">
                <IonBadge color={kpi.isActive ? 'success' : 'medium'}>{kpi.isActive ? 'Activo' : 'Inactivo'}</IonBadge>
                <IonButton size="small" fill="clear" onClick={() => setSnapshotKpi(kpi)}><IonIcon icon={addCircleOutline} slot="start" />Dato</IonButton>
                <IonButton size="small" fill="clear" onClick={() => { setSelectedKpi(kpi); setShowKpiModal(true); }}>Editar</IonButton>
                <IonButton size="small" fill="clear" color="danger" onClick={() => setKpiToDelete(kpi)}>Eliminar</IonButton>
              </div>
            </div>
          ))}
        </IonCardContent>
      </IonCard>
      </div>

      <div className="page-header ion-margin-top">
        <h2 className="page-title" style={{ fontSize: '1.3rem' }}>Widgets</h2>
        <IonButton type="button" disabled={kpis.length === 0} onClick={() => { setSelectedWidget(null); setShowWidgetModal(true); }}>+ Nuevo widget</IonButton>
      </div>

      {widgets.length === 0 ? (
        <IonCard className="app-card ion-text-center"><IonCardContent><p className="muted">No hay widgets configurados aún.</p></IonCardContent></IonCard>
      ) : (
        <div className="section-bg card-grid">
          {widgets.map((widget) => (
            <WidgetCard
              key={widget.widgetId}
              widget={widget}
              kpis={kpis}
              onEdit={(w) => { setSelectedWidget(w); setShowWidgetModal(true); }}
              onDelete={(w) => setWidgetToDelete(w)}
              onAddData={(kpi) => setSnapshotKpi(kpi)}
            />
          ))}
        </div>
      )}

      {showKpiModal && <KpiModal kpi={selectedKpi} onClose={() => setShowKpiModal(false)} onSave={() => { setShowKpiModal(false); loadData(); }} />}
      {kpiToDelete && (
        <ConfirmModal
          title={`Eliminar KPI "${kpiToDelete.name}"`}
          message="Esta acción eliminará el KPI y todos sus datos/snapshots asociados."
          confirmLabel="Eliminar"
          danger
          onConfirm={handleDeleteKpi}
          onCancel={() => setKpiToDelete(null)}
        />
      )}
      {snapshotKpi && <SnapshotModal kpi={snapshotKpi} onClose={() => setSnapshotKpi(null)} onSave={() => { setSnapshotKpi(null); loadData(); }} />}

      {showWidgetModal && <WidgetModal widget={selectedWidget} kpis={kpis} onClose={() => setShowWidgetModal(false)} onSave={() => { setShowWidgetModal(false); loadData(); }} />}
      {widgetToDelete && (
        <ConfirmModal
          title={`Eliminar widget "${widgetToDelete.title}"`}
          message="Esta acción eliminará el widget del dashboard."
          confirmLabel="Eliminar"
          danger
          onConfirm={handleDeleteWidget}
          onCancel={() => setWidgetToDelete(null)}
        />
      )}
    </div>
  );
}
