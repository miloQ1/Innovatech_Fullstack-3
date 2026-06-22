import { BACKEND_ROUTES } from '../config/backend';
import type {
  Dispatch,
  DispatchResult,
  NotificationEvent,
  NotificationPreference,
  NotificationRequest,
  NotificationResponse,
  NotificationTemplate,
  WebhookSubscription,
} from '../types/notifications';
import { apiClient } from './apiClient';

export const notificationService = {
  send(data: NotificationRequest): Promise<NotificationResponse> {
    return apiClient.post<NotificationResponse>(`${BACKEND_ROUTES.notifications}/send`, data, true);
  },
  getMyInbox(): Promise<DispatchResult[]> {
    return apiClient.get<DispatchResult[]>(`${BACKEND_ROUTES.notifications}/me`, true);
  },
  sendTestToMe(): Promise<NotificationResponse> {
    return apiClient.post<NotificationResponse>(`${BACKEND_ROUTES.notifications}/test-me`, {}, true);
  },
  getInboxByRecipient(recipientResourceId: number): Promise<DispatchResult[]> {
    return apiClient.get<DispatchResult[]>(`${BACKEND_ROUTES.notifications}/recipient/${recipientResourceId}`, true);
  },
};

export const notificationTemplateService = {
  getAll(): Promise<NotificationTemplate[]> {
    return apiClient.get<NotificationTemplate[]>(BACKEND_ROUTES.notificationTemplates, true);
  },
  create(data: Partial<NotificationTemplate>): Promise<NotificationTemplate> {
    return apiClient.post<NotificationTemplate>(BACKEND_ROUTES.notificationTemplates, data, true);
  },
  update(id: number, data: Partial<NotificationTemplate>): Promise<NotificationTemplate> {
    return apiClient.put<NotificationTemplate>(`${BACKEND_ROUTES.notificationTemplates}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.notificationTemplates}/${id}`, true);
  },
};

export const notificationPreferenceService = {
  getAll(): Promise<NotificationPreference[]> {
    return apiClient.get<NotificationPreference[]>(BACKEND_ROUTES.notificationPreferences, true);
  },
  getMine(): Promise<NotificationPreference[]> {
    return apiClient.get<NotificationPreference[]>(`${BACKEND_ROUTES.notificationPreferences}/me`, true);
  },
  saveMine(data: Partial<NotificationPreference>): Promise<NotificationPreference> {
    return apiClient.post<NotificationPreference>(`${BACKEND_ROUTES.notificationPreferences}/me`, data, true);
  },
  getByResource(resourceId: number): Promise<NotificationPreference[]> {
    return apiClient.get<NotificationPreference[]>(`${BACKEND_ROUTES.notificationPreferences}/resource/${resourceId}`, true);
  },
  create(data: Partial<NotificationPreference>): Promise<NotificationPreference> {
    return apiClient.post<NotificationPreference>(BACKEND_ROUTES.notificationPreferences, data, true);
  },
  update(id: number, data: Partial<NotificationPreference>): Promise<NotificationPreference> {
    return apiClient.put<NotificationPreference>(`${BACKEND_ROUTES.notificationPreferences}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.notificationPreferences}/${id}`, true);
  },
};

export const notificationEventService = {
  getAll(): Promise<NotificationEvent[]> {
    return apiClient.get<NotificationEvent[]>(BACKEND_ROUTES.notificationEvents, true);
  },
};

export const notificationDispatchService = {
  getAll(): Promise<Dispatch[]> {
    return apiClient.get<Dispatch[]>(BACKEND_ROUTES.notificationDispatches, true);
  },
  getByRecipient(recipientResourceId: number): Promise<Dispatch[]> {
    return apiClient.get<Dispatch[]>(`${BACKEND_ROUTES.notificationDispatches}/recipient/${recipientResourceId}`, true);
  },
};

export const webhookSubscriptionService = {
  getAll(): Promise<WebhookSubscription[]> {
    return apiClient.get<WebhookSubscription[]>(BACKEND_ROUTES.notificationWebhooks, true);
  },
  create(data: Partial<WebhookSubscription>): Promise<WebhookSubscription> {
    return apiClient.post<WebhookSubscription>(BACKEND_ROUTES.notificationWebhooks, data, true);
  },
  update(id: number, data: Partial<WebhookSubscription>): Promise<WebhookSubscription> {
    return apiClient.put<WebhookSubscription>(`${BACKEND_ROUTES.notificationWebhooks}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.notificationWebhooks}/${id}`, true);
  },
};
