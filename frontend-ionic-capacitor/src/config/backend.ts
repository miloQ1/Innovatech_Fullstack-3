import { Capacitor } from '@capacitor/core';

const WEB_API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8090';
const ANDROID_EMULATOR_API_BASE_URL = import.meta.env.VITE_ANDROID_EMULATOR_API_BASE_URL || 'http://10.0.2.2:8090';
const ANDROID_PHYSICAL_API_BASE_URL = import.meta.env.VITE_ANDROID_PHYSICAL_API_BASE_URL || 'http://192.168.1.9:8090';
const ANDROID_API_BASE_URL = import.meta.env.VITE_ANDROID_API_BASE_URL || ANDROID_EMULATOR_API_BASE_URL;

export const API_BASE_URL = Capacitor.isNativePlatform()
  ? ANDROID_API_BASE_URL
  : WEB_API_BASE_URL;

export const API_FALLBACK_BASE_URLS = Capacitor.isNativePlatform()
  ? Array.from(new Set([ANDROID_API_BASE_URL, ANDROID_EMULATOR_API_BASE_URL, ANDROID_PHYSICAL_API_BASE_URL]))
  : [WEB_API_BASE_URL];

export const BACKEND_PORTS = {
  bffGateway: 8090,
  authService: 8080,
  projectsService: 8081,
  resourcesService: 8083,
  collaborationService: 8084,
  notificationsService: 8085,
  analiticaService: 8086,
  clientsService: 8087,
  filesService: 8088,
  auditService: 8089,
  assignmentsService: 8091,
} as const;

export const BACKEND_ROUTES = {
  auth: '/api/auth',
  users: '/api/users',
  clients: '/api/clients',
  projects: '/api/projects',
  phases: '/api/phases',
  tasks: '/api/tasks',
  professionals: '/api/professionals',
  skills: '/api/skills',
  resourceSkills: '/api/resource-skills',
  assignments: '/api/assignments',
  availability: '/api/availability',
  absences: '/api/absences',
  threads: '/api/threads',
  comments: '/api/comments',
  attachments: '/api/attachments',
  mentions: '/api/mentions',
  activityLogs: '/api/activity-logs',
  kpis: '/api/kpis',
  snapshots: '/api/snapshots',
  widgets: '/api/widgets',
  alerts: '/api/alerts',
  layouts: '/api/layouts',
  layoutItems: '/api/layout-items',
  notifications: '/api/notifications',
  notificationTemplates: '/api/templates',
  notificationEvents: '/api/events',
  notificationDispatches: '/api/dispatches',
  notificationPreferences: '/api/preferences',
  notificationWebhooks: '/api/webhooks',
} as const;

export const buildApiUrl = (route: string, baseUrl = API_BASE_URL) => `${baseUrl}${route.startsWith('/') ? route : `/${route}`}`;
