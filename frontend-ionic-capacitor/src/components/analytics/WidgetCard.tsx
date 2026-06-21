import { useEffect, useState } from 'react';
import { IonBadge, IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner } from '@ionic/react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { kpiSnapshotService } from '../../api/analyticsService';
import { WIDGET_TYPE_LABELS, translateLabel } from '../../types/analytics';
import type { KpiDefinition, KpiSnapshot, Widget } from '../../types/analytics';

interface WidgetCardProps {
  widget: Widget;
  kpis: KpiDefinition[];
  onEdit: (widget: Widget) => void;
  onDelete: (widget: Widget) => void;
  onAddData: (kpi: KpiDefinition) => void;
}

const PIE_COLORS = ['#4f46e5', '#059669', '#d97706', '#2563eb', '#dc2626', '#0891b2'];

export function WidgetCard({ widget, kpis, onEdit, onDelete, onAddData }: WidgetCardProps) {
  const [snapshots, setSnapshots] = useState<KpiSnapshot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const kpi = kpis.find((item) => item.code === widget.sourceKpiCode) ?? null;

  useEffect(() => {
    if (!kpi) {
      setLoading(false);
      setError('KPI de origen no encontrado.');
      return;
    }

    let mounted = true;
    setLoading(true);
    setError(null);
    kpiSnapshotService.getByKpi(kpi.kpiId)
      .then((data) => {
        if (!mounted) return;
        setSnapshots([...data].sort((a, b) => a.periodEnd.localeCompare(b.periodEnd)));
      })
      .catch((err) => {
        if (!mounted) return;
        setError(err instanceof Error ? err.message : 'No se pudieron cargar los datos');
      })
      .finally(() => { if (mounted) setLoading(false); });

    return () => { mounted = false; };
  }, [kpi]);

  const chartData = snapshots.map((snap) => ({ period: snap.periodEnd, value: snap.numericValue ?? 0 }));
  const latest = snapshots[snapshots.length - 1];

  function renderBody() {
    if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /></div>;
    if (error) return <p className="muted">{error}</p>;
    if (snapshots.length === 0) {
      return (
        <div className="ion-text-center">
          <p className="muted">Sin datos aún.</p>
          {kpi && <IonButton size="small" onClick={() => onAddData(kpi)}>Agregar dato</IonButton>}
        </div>
      );
    }

    switch (widget.widgetType) {
      case 'CARD':
        return (
          <div className="stat-card">
            <div className="stat-body">
              <p className="stat-value">{latest.numericValue ?? latest.textValue ?? '—'}{kpi?.unit ? ` ${kpi.unit}` : ''}</p>
              <p className="stat-label">{kpi?.name} · {latest.periodEnd}</p>
            </div>
          </div>
        );
      case 'TABLE':
        return snapshots.slice().reverse().map((snap) => (
          <div key={snap.snapshotId} className="list-row">
            <span>{snap.periodEnd}</span>
            <IonBadge>{snap.numericValue ?? snap.textValue ?? '—'}{kpi?.unit ? ` ${kpi.unit}` : ''}</IonBadge>
          </div>
        ));
      case 'LINE_CHART':
        return (
          <div style={{ width: '100%', height: 240 }}>
            <ResponsiveContainer>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="period" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="value" stroke="#4f46e5" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        );
      case 'BAR_CHART':
        return (
          <div style={{ width: '100%', height: 240 }}>
            <ResponsiveContainer>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="period" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="value" fill="#4f46e5" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        );
      case 'PIE_CHART':
        return (
          <div style={{ width: '100%', height: 240 }}>
            <ResponsiveContainer>
              <PieChart>
                <Tooltip />
                <Pie data={chartData} dataKey="value" nameKey="period" outerRadius={90} label>
                  {chartData.map((entry, index) => (
                    <Cell key={entry.period} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                  ))}
                </Pie>
              </PieChart>
            </ResponsiveContainer>
          </div>
        );
      default:
        return <p className="muted">Tipo de widget no soportado: {translateLabel(WIDGET_TYPE_LABELS, String(widget.widgetType))}</p>;
    }
  }

  return (
    <IonCard className="app-card">
      <IonCardHeader>
        <div className="card-title-row">
          <div className="card-title-main">
            <IonCardTitle>{widget.title}</IonCardTitle>
            <p className="muted">{kpi?.name ?? widget.sourceKpiCode}</p>
          </div>
          {!widget.isActive && <IonBadge color="medium">Inactivo</IonBadge>}
        </div>
      </IonCardHeader>
      <IonCardContent>
        {renderBody()}
        <div className="card-actions">
          <IonButton type="button" size="small" fill="solid" color="primary" onClick={() => onEdit(widget)}>Editar</IonButton>
          <IonButton type="button" size="small" fill="outline" color="danger" onClick={() => onDelete(widget)}>Eliminar</IonButton>
        </div>
      </IonCardContent>
    </IonCard>
  );
}
