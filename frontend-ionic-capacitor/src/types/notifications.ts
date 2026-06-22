export type NotificationChannel = 'EMAIL' | 'IN_APP' | 'WEBHOOK';
export type DeliveryStatus = 'PENDING' | 'SENT' | 'FAILED' | 'SKIPPED';
export type EventStatus = 'PENDING' | 'PROCESSED' | 'FAILED';
export type NotificationFrequency = 'IMMEDIATE' | 'DAILY' | 'WEEKLY';

export interface DispatchResult {
  eventId: number;
  dispatchId: number;
  recipientResourceId: number;
  channel: NotificationChannel;
  deliveryStatus: DeliveryStatus;
  subject?: string;
  body?: string;
  sentAt?: string;
  errorMessage?: string;
}

export interface NotificationRequest {
  sourceService: string;
  eventType: string;
  entityId?: number;
  recipientResourceIds: number[];
  channels?: NotificationChannel[];
  payload?: Record<string, unknown>;
}

export interface NotificationResponse {
  message: string;
  totalDispatches: number;
  dispatches: DispatchResult[];
}

export interface NotificationTemplate {
  templateId: number;
  eventType: string;
  channel: NotificationChannel;
  subjectTemplate?: string;
  bodyTemplate: string;
  language?: string;
  isActive?: boolean;
}

export interface NotificationPreference {
  preferenceId: number;
  resourceId: number;
  channel: NotificationChannel;
  enabled: boolean;
  frequency: NotificationFrequency;
  updatedAt?: string;
}

export interface NotificationEvent {
  eventId: number;
  sourceService: string;
  eventType: string;
  entityId?: number;
  payloadJson?: string;
  eventStatus?: EventStatus;
  createdAt?: string;
}

export interface Dispatch {
  dispatchId: number;
  recipientResourceId: number;
  channel: NotificationChannel;
  deliveryStatus: DeliveryStatus;
  renderedSubject?: string;
  renderedBody?: string;
  retryCount?: number;
  sentAt?: string;
  errorMessage?: string;
}

export interface WebhookSubscription {
  subscriptionId: number;
  targetSystem: string;
  eventType: string;
  endpointUrl: string;
  secretKey?: string;
  isActive?: boolean;
  createdAt?: string;
}
