import type * as React from 'react';
import { useEffect, useState } from 'react';
import {
  IonBadge,
  IonButton,
  IonCard,
  IonCardContent,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonModal,
  IonSelect,
  IonSelectOption,
  IonTextarea,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import { chatbubbleEllipsesOutline } from 'ionicons/icons';
import type { Task, TaskPriority, TaskStatus } from '../../types/projects';
import { taskService } from '../../api/projectService';
import { professionalService } from '../../api/resourcesService';
import { commentService, threadService } from '../../api/collaborationService';
import { useAuth } from '../../hooks/useAuth';
import { getProfessionalId } from '../../utils/ids';

interface TaskModalProps {
  task: Task;
  projectId: number;
  onClose: () => void;
  onUpdate: () => void;
  onDelete: () => void;
}

const statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE', 'CANCELLED'];
const priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

export function TaskModal({ task, projectId, onClose, onUpdate, onDelete }: TaskModalProps) {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [title, setTitle] = useState(task.title);
  const [description, setDescription] = useState(task.description ?? '');
  const [priority, setPriority] = useState<TaskPriority | null>(task.priority ?? null);
  const [status, setStatus] = useState<TaskStatus>(task.status);
  const [dueDate, setDueDate] = useState(task.dueDate ?? '');
  const [assignedResourceId, setAssignedResourceId] = useState<number | null>(task.assignedResourceId ?? null);
  const [professionals, setProfessionals] = useState<any[]>([]);
  const [thread, setThread] = useState<any | null>(null);
  const [comments, setComments] = useState<any[]>([]);
  const [newComment, setNewComment] = useState('');
  const [saving, setSaving] = useState(false);
  const [sendingComment, setSendingComment] = useState(false);

  useEffect(() => {
    professionalService.getAll().then(setProfessionals).catch(() => setProfessionals([]));
  }, []);

  useEffect(() => {
    const loadComments = async () => {
      try {
        const threads = await threadService.getByProject(projectId);
        const taskThread = threads.find((item: any) => item.taskId === task.taskId);
        if (taskThread) {
          setThread(taskThread);
          setComments(await commentService.getByThread(taskThread.threadId));
        }
      } catch {
        setComments([]);
      }
    };
    loadComments();
  }, [projectId, task.taskId]);

  const handleSave = async () => {
    setSaving(true);
    try {
      await taskService.updateTask(task.taskId, {
        title,
        description: description || undefined,
        priority: priority ?? undefined,
        status,
        dueDate: dueDate || undefined,
        assignedResourceId: assignedResourceId ?? undefined,
      });
      setIsEditing(false);
      onUpdate();
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    await taskService.deleteTask(task.taskId).catch(() => undefined);
    onDelete();
    onClose();
  };

  const handleSendComment = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!newComment.trim()) return;
    setSendingComment(true);
    try {
      let currentThread = thread;
      if (!currentThread) {
        currentThread = await threadService.create({ projectId, taskId: task.taskId, title: task.title, status: 'OPEN', createdByResourceId: assignedResourceId ?? undefined });
        setThread(currentThread);
      }
      await commentService.create({ threadId: currentThread.threadId, content: newComment.trim(), authorResourceId: assignedResourceId ?? undefined });
      setNewComment('');
      setComments(await commentService.getByThread(currentThread.threadId));
    } finally {
      setSendingComment(false);
    }
  };

  const assignedPro = professionals.find((pro) => getProfessionalId(pro) === assignedResourceId);

  return (
    <IonModal isOpen onDidDismiss={onClose}>
      <IonHeader>
        <IonToolbar>
          <IonTitle>{task.taskCode ? `${task.taskCode} · ` : ''}{task.title}</IonTitle>
          <IonButton slot="end" fill="clear" onClick={() => setIsEditing((value) => !value)}>{isEditing ? 'Cancelar' : 'Editar'}</IonButton>
          <IonButton slot="end" fill="clear" color="danger" onClick={handleDelete}>Eliminar</IonButton>
          <IonButton slot="end" fill="clear" onClick={onClose}>Cerrar</IonButton>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        <IonCard className="app-card">
          <IonCardContent>
            {isEditing ? (
              <IonList inset>
                <IonItem><IonInput label="Título" labelPlacement="stacked" value={title} onIonInput={(e) => setTitle(String(e.detail.value ?? ''))} /></IonItem>
                <IonItem>
                  <IonSelect label="Estado" labelPlacement="stacked" value={status} onIonChange={(e) => setStatus(e.detail.value as TaskStatus)}>
                    {statuses.map((item) => <IonSelectOption key={item} value={item}>{item.replace(/_/g, ' ')}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem>
                  <IonSelect label="Prioridad" labelPlacement="stacked" value={priority ?? ''} onIonChange={(e) => setPriority(e.detail.value ? e.detail.value as TaskPriority : null)}>
                    <IonSelectOption value="">Sin prioridad</IonSelectOption>
                    {priorities.map((item) => <IonSelectOption key={item} value={item}>{item}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem><IonInput label="Fecha límite" labelPlacement="stacked" type="date" value={dueDate} onIonInput={(e) => setDueDate(String(e.detail.value ?? ''))} /></IonItem>
                <IonItem>
                  <IonSelect label="Asignado a" labelPlacement="stacked" value={assignedResourceId ?? ''} onIonChange={(e) => setAssignedResourceId(e.detail.value ? Number(e.detail.value) : null)}>
                    <IonSelectOption value="">Sin asignar</IonSelectOption>
                    {professionals.filter((pro) => pro.status === 'ACTIVE').map((pro) => {
                      const resourceId = getProfessionalId(pro);
                      if (!resourceId) return null;
                      return <IonSelectOption key={resourceId} value={resourceId}>{pro.firstName} {pro.lastName} — {pro.roleName ?? 'Sin rol'}</IonSelectOption>;
                    })}
                  </IonSelect>
                </IonItem>
                <IonItem><IonTextarea label="Descripción" labelPlacement="stacked" value={description} autoGrow onIonInput={(e) => setDescription(String(e.detail.value ?? ''))} /></IonItem>
              </IonList>
            ) : (
              <div className="form-grid">
                <p><strong>Estado:</strong><br /><IonBadge color={status === 'DONE' ? 'success' : status === 'CANCELLED' ? 'danger' : 'primary'}>{status.replace(/_/g, ' ')}</IonBadge></p>
                <p><strong>Prioridad:</strong><br />{priority ? <IonBadge color={priority === 'CRITICAL' || priority === 'HIGH' ? 'danger' : 'medium'}>{priority}</IonBadge> : 'Sin prioridad'}</p>
                <p><strong>Vence:</strong><br />{dueDate || '—'}</p>
                <p><strong>Asignado:</strong><br />{assignedPro ? `${assignedPro.firstName} ${assignedPro.lastName}` : 'Sin asignar'}</p>
                <p style={{ gridColumn: '1 / -1' }}><strong>Descripción:</strong><br />{description || 'Sin descripción.'}</p>
              </div>
            )}
            {isEditing && <IonButton expand="block" onClick={handleSave} disabled={saving}>{saving ? 'Guardando...' : 'Guardar cambios'}</IonButton>}
          </IonCardContent>
        </IonCard>

        <IonCard className="app-card">
          <IonCardContent>
            <h2 className="section-title"><IonIcon icon={chatbubbleEllipsesOutline} />Comentarios ({comments.length})</h2>
            {comments.length === 0 ? <p className="muted">No hay comentarios aún.</p> : comments.map((comment) => (
              <IonItem key={comment.commentId ?? comment.id} lines="full">
                <IonLabel>
                  <h3>{user?.firstName} {user?.lastName}</h3>
                  <p>{comment.content}</p>
                  {comment.createdAt && <p>{new Date(comment.createdAt).toLocaleString('es-CL')}</p>}
                </IonLabel>
              </IonItem>
            ))}
            <form onSubmit={handleSendComment} className="inline-row ion-margin-top">
              <IonInput fill="outline" placeholder="Escribe un comentario..." value={newComment} onIonInput={(e) => setNewComment(String(e.detail.value ?? ''))} />
              <IonButton type="submit" disabled={sendingComment || !newComment.trim()}>Enviar</IonButton>
            </form>
          </IonCardContent>
        </IonCard>
      </IonContent>
    </IonModal>
  );
}
