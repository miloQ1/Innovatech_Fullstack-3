import { BACKEND_ROUTES } from '../config/backend';
import type {
  ActivityLog,
  Attachment,
  CollaborationThread,
  CollaborationThreadRequest,
  Comment,
  CommentRequest,
  Mention,
} from '../types/collaboration';
import { apiClient } from './apiClient';

export const threadService = {
  getAll(): Promise<CollaborationThread[]> {
    return apiClient.get<CollaborationThread[]>(BACKEND_ROUTES.threads, true);
  },
  getById(id: number): Promise<CollaborationThread> {
    return apiClient.get<CollaborationThread>(`${BACKEND_ROUTES.threads}/${id}`, true);
  },
  getByProject(projectId: number): Promise<CollaborationThread[]> {
    return apiClient.get<CollaborationThread[]>(`${BACKEND_ROUTES.threads}/project/${projectId}`, true);
  },
  create(data: CollaborationThreadRequest): Promise<CollaborationThread> {
    return apiClient.post<CollaborationThread>(BACKEND_ROUTES.threads, data, true);
  },
  update(id: number, data: Partial<CollaborationThreadRequest>): Promise<CollaborationThread> {
    return apiClient.put<CollaborationThread>(`${BACKEND_ROUTES.threads}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.threads}/${id}`, true);
  },
};

export const commentService = {
  getAll(): Promise<Comment[]> {
    return apiClient.get<Comment[]>(BACKEND_ROUTES.comments, true);
  },
  getById(id: number): Promise<Comment> {
    return apiClient.get<Comment>(`${BACKEND_ROUTES.comments}/${id}`, true);
  },
  getByThread(threadId: number): Promise<Comment[]> {
    return apiClient.get<Comment[]>(`${BACKEND_ROUTES.comments}/thread/${threadId}`, true);
  },
  create(data: CommentRequest): Promise<Comment> {
    return apiClient.post<Comment>(BACKEND_ROUTES.comments, data, true);
  },
  update(id: number, data: Partial<CommentRequest>): Promise<Comment> {
    return apiClient.put<Comment>(`${BACKEND_ROUTES.comments}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.comments}/${id}`, true);
  },
};

export const attachmentService = {
  getByComment(commentId: number): Promise<Attachment[]> {
    return apiClient.get<Attachment[]>(`${BACKEND_ROUTES.attachments}/comment/${commentId}`, true);
  },
  create(data: Partial<Attachment>): Promise<Attachment> {
    return apiClient.post<Attachment>(BACKEND_ROUTES.attachments, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.attachments}/${id}`, true);
  },
};

export const mentionService = {
  getByResource(resourceId: number): Promise<Mention[]> {
    return apiClient.get<Mention[]>(`${BACKEND_ROUTES.mentions}/resource/${resourceId}`, true);
  },
  create(data: Partial<Mention>): Promise<Mention> {
    return apiClient.post<Mention>(BACKEND_ROUTES.mentions, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.mentions}/${id}`, true);
  },
};

export const activityLogService = {
  // El microservicio de colaboración expone /api/activity-logs.
  // Revisa el BFF: actualmente en application.properties aparece /api/activity/**,
  // por lo que se debe agregar una ruta para /api/activity-logs/** si quieres usarlo vía gateway.
  getByProject(projectId: number): Promise<ActivityLog[]> {
    return apiClient.get<ActivityLog[]>(`${BACKEND_ROUTES.activityLogs}/project/${projectId}`, true);
  },
  create(data: Partial<ActivityLog>): Promise<ActivityLog> {
    return apiClient.post<ActivityLog>(BACKEND_ROUTES.activityLogs, data, true);
  },
};
