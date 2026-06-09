export interface CollaborationThread {
  threadId: number;
  projectId: number;
  taskId?: number;
  title: string;
  status: string;
  createdByResourceId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CollaborationThreadRequest {
  projectId: number;
  taskId?: number;
  title: string;
  status: string;
  createdByResourceId?: number;
}

export interface Comment {
  commentId: number;
  threadId: number;
  authorResourceId?: number;
  content: string;
  isEdited?: boolean;
  createdAt?: string;
  updatedAt?: string;
  deletedAt?: string;
}

export interface CommentRequest {
  threadId: number;
  authorResourceId?: number;
  content: string;
}

export interface Attachment {
  attachmentId: number;
  commentId: number;
  fileName: string;
  fileUrl: string;
  mimeType?: string;
  sizeBytes?: number;
  uploadedAt?: string;
}

export interface Mention {
  mentionId: number;
  commentId: number;
  mentionedResourceId: number;
  mentionStatus?: string;
  createdAt?: string;
}

export interface ActivityLog {
  activityId: number;
  projectId: number;
  taskId?: number;
  actorResourceId?: number;
  actionType: string;
  description?: string;
  createdAt?: string;
}
