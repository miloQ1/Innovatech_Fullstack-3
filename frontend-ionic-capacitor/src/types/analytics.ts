export type FormulaType = 'PROGRESS' | 'UTILIZATION' | 'DELAY_RISK';
export type RefreshFrequency = 'DAILY' | 'WEEKLY' | 'MONTHLY';
export type ScopeType = 'GLOBAL' | 'PROJECT' | 'RESOURCE';
export type WidgetType = 'CARD' | 'LINE_CHART' | 'BAR_CHART' | 'PIE_CHART' | 'TABLE';

export interface KpiDefinition {
  kpiId: number;
  code: string;
  name: string;
  formulaType: FormulaType | string;
  unit?: string;
  refreshFrequency: RefreshFrequency | string;
  isActive?: boolean;
}

export interface KpiSnapshot {
  snapshotId: number;
  scopeType: ScopeType | string;
  scopeId?: number;
  periodStart: string;
  periodEnd: string;
  numericValue?: number;
  textValue?: string;
  generatedAt?: string;
  sourceTraceJson?: string;
}

export interface Widget {
  widgetId: number;
  widgetType: WidgetType | string;
  title: string;
  sourceKpiCode?: string;
  configurationJson?: string;
  isActive?: boolean;
}

export const FORMULA_TYPE_LABELS: Record<string, string> = {
  PROGRESS: 'Avance / Progreso',
  UTILIZATION: 'Utilización de recursos',
  DELAY_RISK: 'Riesgo de atraso',
};

export const REFRESH_FREQUENCY_LABELS: Record<string, string> = {
  DAILY: 'Diaria',
  WEEKLY: 'Semanal',
  MONTHLY: 'Mensual',
};

export const SCOPE_TYPE_LABELS: Record<string, string> = {
  GLOBAL: 'Global (toda la empresa)',
  PROJECT: 'Un proyecto específico',
  RESOURCE: 'Un recurso específico',
};

export const WIDGET_TYPE_LABELS: Record<string, string> = {
  CARD: 'Tarjeta (último valor)',
  LINE_CHART: 'Gráfico de línea',
  BAR_CHART: 'Gráfico de barras',
  PIE_CHART: 'Gráfico de torta',
  TABLE: 'Tabla',
};

export function translateLabel(map: Record<string, string>, value: string): string {
  return map[value] ?? value;
}
