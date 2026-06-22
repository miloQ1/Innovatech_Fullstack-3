import type * as React from 'react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  IonBadge,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonSegment,
  IonSegmentButton,
  IonSelect,
  IonSelectOption,
  IonSpinner,
  IonText,
  IonTextarea,
} from '@ionic/react';
import { attachOutline, chatbubblesOutline, documentAttachOutline, pulseOutline, sendOutline } from 'ionicons/icons';
import {
  activityLogService,
  attachmentService,
  commentService,
  threadService,
} from '../api/collaborationService';
import { projectService } from '../api/projectService';
import { professionalService } from '../api/resourcesService';
import type { ActivityLog, Attachment, CollaborationThread, Comment as ThreadComment } from '../types/collaboration';
import type { Project } from '../types/projects';
import type { Professional } from '../types/resources';
import { getProfessionalId, getProjectId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

type TabValue = 'threads' | 'comments' | 'attachments' | 'activity';

function professionalName(professional?: Professional) {
  if (!professional) return 'Sin recurso';
  const name = `${professional.firstName ?? ''} ${professional.lastName ?? ''}`.trim();
  return name || professional.email || `Recurso #${getProfessionalId(professional as Professional & Record<string, unknown>) ?? ''}`;
}

function formatDate(value?: string) {
  if (!value) return 'Sin fecha';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('es-CL', { dateStyle: 'short', timeStyle: 'short' });
}

function threadStatusColor(status?: string) {
  if (status === 'CLOSED' || status === 'RESOLVED') return 'success';
  if (status === 'ARCHIVED') return 'medium';
  return 'primary';
}

export function CollaborationPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [selectedThreadId, setSelectedThreadId] = useState<number | null>(null);
  const [threads, setThreads] = useState<CollaborationThread[]>([]);
  const [commentsByThread, setCommentsByThread] = useState<Record<number, ThreadComment[]>>({});
  const [attachmentsByComment, setAttachmentsByComment] = useState<Record<number, Attachment[]>>({});
  const [activityLogs, setActivityLogs] = useState<ActivityLog[]>([]);
  const [activeTab, setActiveTab] = useState<TabValue>('threads');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger' | 'warning'; text: string } | null>(null);

  const [threadForm, setThreadForm] = useState({ title: '', status: 'OPEN', createdByResourceId: '' });
  const [commentForm, setCommentForm] = useState({ content: '', authorResourceId: '' });
  const [attachmentForm, setAttachmentForm] = useState({ commentId: '', fileName: '', fileUrl: '', mimeType: '', sizeBytes: '' });
  const [activityForm, setActivityForm] = useState({ actionType: 'MANUAL_NOTE', description: '', actorResourceId: '' });

  const projectsById = useMemo(() => new Map(
    projects
      .map((item) => [getProjectId(item as Project & Record<string, unknown>), item] as const)
      .filter(([id]) => !!id),
  ), [projects]);

  const professionalsById = useMemo(() => new Map(
    professionals
      .map((item) => [getProfessionalId(item as Professional & Record<string, unknown>), item] as const)
      .filter(([id]) => !!id),
  ), [professionals]);

  const selectedThread = selectedThreadId ? threads.find((thread) => thread.threadId === selectedThreadId) : undefined;
  const selectedThreadComments = selectedThreadId ? commentsByThread[selectedThreadId] ?? [] : [];
  const allComments = useMemo(() => Object.values(commentsByThread).flat(), [commentsByThread]);
  const allAttachments = useMemo(() => Object.values(attachmentsByComment).flat(), [attachmentsByComment]);

  const loadProjectData = useCallback(async (projectId: number) => {
    const [threadResult, activityResult] = await Promise.allSettled([
      threadService.getByProject(projectId),
      activityLogService.getByProject(projectId),
    ]);

    const loadedThreads = threadResult.status === 'fulfilled' ? threadResult.value : [];
    setThreads(loadedThreads);
    setActivityLogs(activityResult.status === 'fulfilled' ? activityResult.value : []);

    const nextThreadId = selectedThreadId && loadedThreads.some((thread) => thread.threadId === selectedThreadId)
      ? selectedThreadId
      : loadedThreads[0]?.threadId ?? null;
    setSelectedThreadId(nextThreadId);

    const commentEntries = await Promise.all(loadedThreads.map(async (thread) => {
      try {
        const comments = await commentService.getByThread(thread.threadId);
        return [thread.threadId, comments] as const;
      } catch {
        return [thread.threadId, []] as const;
      }
    }));

    const nextCommentsByThread = Object.fromEntries(commentEntries) as Record<number, ThreadComment[]>;
    setCommentsByThread(nextCommentsByThread);

    const loadedComments = Object.values(nextCommentsByThread).flat();
    const attachmentEntries = await Promise.all(loadedComments.map(async (comment) => {
      try {
        const attachments = await attachmentService.getByComment(comment.commentId);
        return [comment.commentId, attachments] as const;
      } catch {
        return [comment.commentId, []] as const;
      }
    }));
    setAttachmentsByComment(Object.fromEntries(attachmentEntries) as Record<number, Attachment[]>);

    if (threadResult.status === 'rejected' || activityResult.status === 'rejected') {
      setMessage({ type: 'warning', text: 'No se pudieron cargar todos los datos de colaboración. Revisa rutas de Colaboración, Archivos y Auditoría en el BFF.' });
    }
  }, [selectedThreadId]);

  const loadPage = useCallback(async () => {
    setLoading(true);
    setMessage(null);
    try {
      const [projectResult, professionalResult] = await Promise.allSettled([
        projectService.getAll(),
        professionalService.getAll(),
      ]);

      const loadedProjects = projectResult.status === 'fulfilled' ? projectResult.value : [];
      const loadedProfessionals = professionalResult.status === 'fulfilled' ? professionalResult.value : [];
      setProjects(loadedProjects);
      setProfessionals(loadedProfessionals);

      const projectId = selectedProjectId ?? (loadedProjects[0] ? getProjectId(loadedProjects[0] as Project & Record<string, unknown>) : null);
      setSelectedProjectId(projectId);

      if (projectId) {
        await loadProjectData(projectId);
      } else {
        setThreads([]);
        setCommentsByThread({});
        setAttachmentsByComment({});
        setActivityLogs([]);
        setMessage({ type: 'warning', text: 'No hay proyectos creados para mostrar colaboración.' });
      }
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo cargar Colaboración.' });
    } finally {
      setLoading(false);
    }
  }, [loadProjectData, selectedProjectId]);

  useEffect(() => { loadPage(); }, [loadPage]);

  const handleProjectChange = async (projectId: number) => {
    setSelectedProjectId(projectId);
    setSelectedThreadId(null);
    setMessage(null);
    setLoading(true);
    try {
      await loadProjectData(projectId);
    } finally {
      setLoading(false);
    }
  };

  const firstProfessionalId = professionals[0] ? getProfessionalId(professionals[0] as Professional & Record<string, unknown>) : undefined;

  const handleCreateThread = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedProjectId) return;
    setSaving(true);
    setMessage(null);
    try {
      const created = await threadService.create({
        projectId: selectedProjectId,
        title: threadForm.title,
        status: threadForm.status || 'OPEN',
        createdByResourceId: threadForm.createdByResourceId ? Number(threadForm.createdByResourceId) : firstProfessionalId,
      });
      setThreadForm({ title: '', status: 'OPEN', createdByResourceId: '' });
      setSelectedThreadId(created.threadId);
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Hilo creado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear el hilo.' });
    } finally {
      setSaving(false);
    }
  };

  const handleCreateComment = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedProjectId || !selectedThreadId) return;
    setSaving(true);
    setMessage(null);
    try {
      await commentService.create({
        threadId: selectedThreadId,
        authorResourceId: commentForm.authorResourceId ? Number(commentForm.authorResourceId) : firstProfessionalId,
        content: commentForm.content,
      });
      setCommentForm({ content: '', authorResourceId: '' });
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Comentario creado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear el comentario.' });
    } finally {
      setSaving(false);
    }
  };

  const handleCreateAttachment = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedProjectId || !attachmentForm.commentId) return;
    setSaving(true);
    setMessage(null);
    try {
      await attachmentService.create({
        commentId: Number(attachmentForm.commentId),
        fileName: attachmentForm.fileName,
        fileUrl: attachmentForm.fileUrl,
        mimeType: attachmentForm.mimeType || undefined,
        sizeBytes: attachmentForm.sizeBytes ? Number(attachmentForm.sizeBytes) : undefined,
      });
      setAttachmentForm({ commentId: '', fileName: '', fileUrl: '', mimeType: '', sizeBytes: '' });
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Archivo adjunto registrado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo registrar el archivo.' });
    } finally {
      setSaving(false);
    }
  };

  const handleCreateActivity = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedProjectId) return;
    setSaving(true);
    setMessage(null);
    try {
      await activityLogService.create({
        projectId: selectedProjectId,
        actorResourceId: activityForm.actorResourceId ? Number(activityForm.actorResourceId) : firstProfessionalId,
        actionType: activityForm.actionType,
        description: activityForm.description,
      });
      setActivityForm({ actionType: 'MANUAL_NOTE', description: '', actorResourceId: '' });
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Registro de auditoría creado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear el registro de auditoría.' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando colaboración...</p></div>;

  const selectedProject = selectedProjectId ? projectsById.get(selectedProjectId) : undefined;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title"><IonIcon icon={chatbubblesOutline} />Colaboración, Archivos y Auditoría</h1>
          <p className="page-subtitle">Vista para probar hilos, comentarios, adjuntos y trazabilidad de los microservicios separados.</p>
        </div>
        <IonButton type="button" fill="outline" onClick={loadPage}>Actualizar</IonButton>
      </div>

      {message && <IonText color={message.type}><p>{message.text}</p></IonText>}

      <IonCard className="app-card ion-margin-bottom">
        <IonCardContent>
          <IonItem>
            <IonSelect label="Proyecto" labelPlacement="stacked" value={selectedProjectId ?? ''} onIonChange={(event) => handleProjectChange(Number(event.detail.value))}>
              {projects.map((project) => {
                const id = getProjectId(project as Project & Record<string, unknown>);
                return id ? <IonSelectOption key={id} value={id}>{project.name} · #{project.code}</IonSelectOption> : null;
              })}
            </IonSelect>
          </IonItem>
          {selectedProject && <p className="muted ion-padding-start">Trabajando sobre {selectedProject.name}.</p>}
        </IonCardContent>
      </IonCard>

      <IonSegment value={activeTab} scrollable onIonChange={(event) => setActiveTab((event.detail.value as TabValue) ?? 'threads')}>
        <IonSegmentButton value="threads">Hilos ({threads.length})</IonSegmentButton>
        <IonSegmentButton value="comments">Comentarios ({allComments.length})</IonSegmentButton>
        <IonSegmentButton value="attachments">Archivos ({allAttachments.length})</IonSegmentButton>
        <IonSegmentButton value="activity">Auditoría ({activityLogs.length})</IonSegmentButton>
      </IonSegment>

      {activeTab === 'threads' && (
        <div className="card-grid ion-margin-top">
          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={chatbubblesOutline} />Nuevo hilo</IonCardTitle></IonCardHeader>
            <IonCardContent>
              <form onSubmit={handleCreateThread} className="form-grid">
                <IonItem><IonInput label="Título" labelPlacement="stacked" value={threadForm.title} required onIonInput={(event) => setThreadForm((current) => ({ ...current, title: String(event.detail.value ?? '') }))} /></IonItem>
                <IonItem><IonInput label="Estado" labelPlacement="stacked" value={threadForm.status} required onIonInput={(event) => setThreadForm((current) => ({ ...current, status: String(event.detail.value ?? 'OPEN') }))} /></IonItem>
                <IonItem>
                  <IonSelect label="Creador" labelPlacement="stacked" value={threadForm.createdByResourceId} onIonChange={(event) => setThreadForm((current) => ({ ...current, createdByResourceId: String(event.detail.value ?? '') }))}>
                    <IonSelectOption value="">Usar primer profesional disponible</IonSelectOption>
                    {professionals.map((professional) => {
                      const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                      return id ? <IonSelectOption key={id} value={String(id)}>{professionalName(professional)}</IonSelectOption> : null;
                    })}
                  </IonSelect>
                </IonItem>
                <IonButton type="submit" disabled={saving || !selectedProjectId}>{saving ? 'Guardando...' : 'Crear hilo'}</IonButton>
              </form>
            </IonCardContent>
          </IonCard>

          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle>Hilos del proyecto</IonCardTitle></IonCardHeader>
            <IonCardContent>
              {threads.length === 0 ? <p className="muted">Este proyecto todavía no tiene hilos.</p> : (
                <IonList inset>
                  {threads.map((thread) => (
                    <IonItem key={thread.threadId} button detail={false} onClick={() => { setSelectedThreadId(thread.threadId); setActiveTab('comments'); }}>
                      <IonLabel>
                        <h2>{thread.title}</h2>
                        <p>{professionalName(professionalsById.get(thread.createdByResourceId ?? 0))} · {formatDate(thread.createdAt)}</p>
                      </IonLabel>
                      <IonBadge color={threadStatusColor(thread.status)} slot="end">{formatStatus(thread.status)}</IonBadge>
                    </IonItem>
                  ))}
                </IonList>
              )}
            </IonCardContent>
          </IonCard>
        </div>
      )}

      {activeTab === 'comments' && (
        <div className="card-grid ion-margin-top">
          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={sendOutline} />Nuevo comentario</IonCardTitle></IonCardHeader>
            <IonCardContent>
              <IonItem className="ion-margin-bottom">
                <IonSelect label="Hilo" labelPlacement="stacked" value={selectedThreadId ?? ''} onIonChange={(event) => setSelectedThreadId(Number(event.detail.value))}>
                  {threads.map((thread) => <IonSelectOption key={thread.threadId} value={thread.threadId}>{thread.title}</IonSelectOption>)}
                </IonSelect>
              </IonItem>
              <form onSubmit={handleCreateComment} className="form-grid">
                <IonItem>
                  <IonSelect label="Autor" labelPlacement="stacked" value={commentForm.authorResourceId} onIonChange={(event) => setCommentForm((current) => ({ ...current, authorResourceId: String(event.detail.value ?? '') }))}>
                    <IonSelectOption value="">Usar primer profesional disponible</IonSelectOption>
                    {professionals.map((professional) => {
                      const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                      return id ? <IonSelectOption key={id} value={String(id)}>{professionalName(professional)}</IonSelectOption> : null;
                    })}
                  </IonSelect>
                </IonItem>
                <IonItem className="form-grid-span"><IonTextarea label="Comentario" labelPlacement="stacked" value={commentForm.content} autoGrow required onIonInput={(event) => setCommentForm((current) => ({ ...current, content: String(event.detail.value ?? '') }))} /></IonItem>
                <IonButton type="submit" disabled={saving || !selectedThreadId}>{saving ? 'Guardando...' : 'Comentar'}</IonButton>
              </form>
            </IonCardContent>
          </IonCard>

          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle>{selectedThread ? `Comentarios: ${selectedThread.title}` : 'Comentarios'}</IonCardTitle></IonCardHeader>
            <IonCardContent>
              {selectedThreadComments.length === 0 ? <p className="muted">No hay comentarios en este hilo.</p> : (
                <IonList inset>
                  {selectedThreadComments.map((comment) => (
                    <IonItem key={comment.commentId}>
                      <IonLabel>
                        <h2>{professionalName(professionalsById.get(comment.authorResourceId ?? 0))}</h2>
                        <p>{comment.content}</p>
                        <p className="muted">Comentario #{comment.commentId} · {formatDate(comment.createdAt)}</p>
                      </IonLabel>
                      <IonButton slot="end" size="small" fill="clear" onClick={() => { setAttachmentForm((current) => ({ ...current, commentId: String(comment.commentId) })); setActiveTab('attachments'); }}>Adjuntar</IonButton>
                    </IonItem>
                  ))}
                </IonList>
              )}
            </IonCardContent>
          </IonCard>
        </div>
      )}

      {activeTab === 'attachments' && (
        <div className="card-grid ion-margin-top">
          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={attachOutline} />Registrar archivo</IonCardTitle></IonCardHeader>
            <IonCardContent>
              <form onSubmit={handleCreateAttachment} className="form-grid">
                <IonItem>
                  <IonSelect label="Comentario" labelPlacement="stacked" value={attachmentForm.commentId} required onIonChange={(event) => setAttachmentForm((current) => ({ ...current, commentId: String(event.detail.value ?? '') }))}>
                    {allComments.map((comment) => <IonSelectOption key={comment.commentId} value={String(comment.commentId)}>#{comment.commentId} · {comment.content.slice(0, 35)}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem><IonInput label="Nombre archivo" labelPlacement="stacked" value={attachmentForm.fileName} required onIonInput={(event) => setAttachmentForm((current) => ({ ...current, fileName: String(event.detail.value ?? '') }))} /></IonItem>
                <IonItem><IonInput label="URL archivo" labelPlacement="stacked" value={attachmentForm.fileUrl} required onIonInput={(event) => setAttachmentForm((current) => ({ ...current, fileUrl: String(event.detail.value ?? '') }))} /></IonItem>
                <IonItem><IonInput label="MIME type" labelPlacement="stacked" value={attachmentForm.mimeType} placeholder="application/pdf" onIonInput={(event) => setAttachmentForm((current) => ({ ...current, mimeType: String(event.detail.value ?? '') }))} /></IonItem>
                <IonItem><IonInput label="Tamaño bytes" labelPlacement="stacked" type="number" value={attachmentForm.sizeBytes} onIonInput={(event) => setAttachmentForm((current) => ({ ...current, sizeBytes: String(event.detail.value ?? '') }))} /></IonItem>
                <IonButton type="submit" disabled={saving || !attachmentForm.commentId}>{saving ? 'Guardando...' : 'Guardar archivo'}</IonButton>
              </form>
            </IonCardContent>
          </IonCard>

          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={documentAttachOutline} />Archivos registrados</IonCardTitle></IonCardHeader>
            <IonCardContent>
              {allAttachments.length === 0 ? <p className="muted">No hay archivos adjuntos.</p> : (
                <IonList inset>
                  {allAttachments.map((attachment) => (
                    <IonItem key={attachment.attachmentId}>
                      <IonLabel>
                        <h2>{attachment.fileName}</h2>
                        <p>Comentario #{attachment.commentId} · {attachment.mimeType ?? 'tipo no informado'}</p>
                        <p className="muted">{attachment.sizeBytes ? `${attachment.sizeBytes} bytes` : 'Sin tamaño'} · {formatDate(attachment.uploadedAt)}</p>
                      </IonLabel>
                      <IonButton slot="end" size="small" fill="clear" href={attachment.fileUrl} target="_blank">Abrir</IonButton>
                    </IonItem>
                  ))}
                </IonList>
              )}
            </IonCardContent>
          </IonCard>
        </div>
      )}

      {activeTab === 'activity' && (
        <div className="card-grid ion-margin-top">
          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={pulseOutline} />Crear registro de auditoría</IonCardTitle></IonCardHeader>
            <IonCardContent>
              <form onSubmit={handleCreateActivity} className="form-grid">
                <IonItem><IonInput label="Tipo acción" labelPlacement="stacked" value={activityForm.actionType} required onIonInput={(event) => setActivityForm((current) => ({ ...current, actionType: String(event.detail.value ?? '') }))} /></IonItem>
                <IonItem>
                  <IonSelect label="Actor" labelPlacement="stacked" value={activityForm.actorResourceId} onIonChange={(event) => setActivityForm((current) => ({ ...current, actorResourceId: String(event.detail.value ?? '') }))}>
                    <IonSelectOption value="">Usar primer profesional disponible</IonSelectOption>
                    {professionals.map((professional) => {
                      const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                      return id ? <IonSelectOption key={id} value={String(id)}>{professionalName(professional)}</IonSelectOption> : null;
                    })}
                  </IonSelect>
                </IonItem>
                <IonItem className="form-grid-span"><IonTextarea label="Descripción" labelPlacement="stacked" value={activityForm.description} autoGrow onIonInput={(event) => setActivityForm((current) => ({ ...current, description: String(event.detail.value ?? '') }))} /></IonItem>
                <IonButton type="submit" disabled={saving || !selectedProjectId}>{saving ? 'Guardando...' : 'Crear registro'}</IonButton>
              </form>
            </IonCardContent>
          </IonCard>

          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle>Historial de actividad</IonCardTitle></IonCardHeader>
            <IonCardContent>
              {activityLogs.length === 0 ? <p className="muted">No hay actividad registrada para este proyecto.</p> : (
                <IonList inset>
                  {activityLogs.map((activity) => (
                    <IonItem key={activity.activityId}>
                      <IonLabel>
                        <h2>{activity.actionType}</h2>
                        <p>{activity.description || 'Sin descripción'}</p>
                        <p className="muted">{professionalName(professionalsById.get(activity.actorResourceId ?? 0))} · {formatDate(activity.createdAt)}</p>
                      </IonLabel>
                    </IonItem>
                  ))}
                </IonList>
              )}
            </IonCardContent>
          </IonCard>
        </div>
      )}
    </div>
  );
}
