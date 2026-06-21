import { BACKEND_ROUTES } from '../config/backend';
import type { KpiDefinition, KpiSnapshot, Widget } from '../types/analytics';
import { apiClient } from './apiClient';

export const kpiService = {
  getAll(): Promise<KpiDefinition[]> {
    return apiClient.get<KpiDefinition[]>(BACKEND_ROUTES.kpis, true);
  },
  getById(id: number): Promise<KpiDefinition> {
    return apiClient.get<KpiDefinition>(`${BACKEND_ROUTES.kpis}/${id}`, true);
  },
  create(data: Partial<KpiDefinition>): Promise<KpiDefinition> {
    return apiClient.post<KpiDefinition>(BACKEND_ROUTES.kpis, data, true);
  },
  update(id: number, data: Partial<KpiDefinition>): Promise<KpiDefinition> {
    return apiClient.put<KpiDefinition>(`${BACKEND_ROUTES.kpis}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.kpis}/${id}`, true);
  },
};

export const kpiSnapshotService = {
  getAll(): Promise<KpiSnapshot[]> {
    return apiClient.get<KpiSnapshot[]>(BACKEND_ROUTES.snapshots, true);
  },
  getById(id: number): Promise<KpiSnapshot> {
    return apiClient.get<KpiSnapshot>(`${BACKEND_ROUTES.snapshots}/${id}`, true);
  },
  getByKpi(kpiId: number): Promise<KpiSnapshot[]> {
    return apiClient.get<KpiSnapshot[]>(`${BACKEND_ROUTES.snapshots}/kpi/${kpiId}`, true);
  },
  create(kpiId: number, data: Partial<KpiSnapshot>): Promise<KpiSnapshot> {
    return apiClient.post<KpiSnapshot>(`${BACKEND_ROUTES.snapshots}/kpi/${kpiId}`, data, true);
  },
  update(id: number, data: Partial<KpiSnapshot>): Promise<KpiSnapshot> {
    return apiClient.put<KpiSnapshot>(`${BACKEND_ROUTES.snapshots}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.snapshots}/${id}`, true);
  },
};

export const widgetService = {
  getAll(): Promise<Widget[]> {
    return apiClient.get<Widget[]>(BACKEND_ROUTES.widgets, true);
  },
  getById(id: number): Promise<Widget> {
    return apiClient.get<Widget>(`${BACKEND_ROUTES.widgets}/${id}`, true);
  },
  create(data: Partial<Widget>): Promise<Widget> {
    return apiClient.post<Widget>(BACKEND_ROUTES.widgets, data, true);
  },
  update(id: number, data: Partial<Widget>): Promise<Widget> {
    return apiClient.put<Widget>(`${BACKEND_ROUTES.widgets}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.widgets}/${id}`, true);
  },
};
