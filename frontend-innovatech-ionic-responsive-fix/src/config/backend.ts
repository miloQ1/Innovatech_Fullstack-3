export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

export const BACKEND_PORTS = {
  bffGateway: 8090,
  authService: 8080,
  projectsService: 8081,
  resourcesService: 8083,
  collaborationService: 8084,
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
  assignments: '/api/assignments',
  availability: '/api/availability',
  absences: '/api/absences',
  threads: '/api/threads',
  comments: '/api/comments',
  attachments: '/api/attachments',
  mentions: '/api/mentions',
  activityLogs: '/api/activity-logs',
} as const;
