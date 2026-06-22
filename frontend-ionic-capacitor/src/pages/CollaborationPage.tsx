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
  IonSelect,
  IonSelectOption,
  IonSpinner,
  IonText,
  IonTextarea,
} from '@ionic/react';
import { arrowBackOutline, attachOutline, chatbubblesOutline, sendOutline } from 'ionicons/icons';
import {
  attachmentService,
  commentService,
  threadService,
} from '../api/collaborationService';
import { projectService } from '../api/projectService';
import { assignmentService, professionalService } from '../api/resourcesService';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import { useAuth } from '../hooks/useAuth';
import type { Attachment, CollaborationThread, Comment as ThreadComment } from '../types/collaboration';
import type { Project } from '../types/projects';
import type { Professional } from '../types/resources';
import { getProfessionalId, getProjectId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

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

function sortByCreatedAtAsc<T extends { createdAt?: string }>(items: T[]): T[] {
  return [...items].sort((a, b) => new Date(a.createdAt ?? 0).getTime() - new Date(b.createdAt ?? 0).getTime());
}

export function CollaborationPage() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const [projects, setProjects] = useState<Project[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [myProfessional, setMyProfessional] = useState<Professional | null>(null);
  const [hasOwnProfessional, setHasOwnProfessional] = useState(true);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [selectedThreadId, setSelectedThreadId] = useState<number | null>(null);
  const [threads, setThreads] = useState<CollaborationThread[]>([]);
  const [commentsByThread, setCommentsByThread] = useState<Record<number, ThreadComment[]>>({});
  const [attachmentsByComment, setAttachmentsByComment] = useState<Record<number, Attachment[]>>({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger' | 'warning'; text: string } | null>(null);
  const [showComposerAttach, setShowComposerAttach] = useState(false);
  const [editingThreadId, setEditingThreadId] = useState<number | null>(null);
  const [editTitle, setEditTitle] = useState('');
  const [threadToDelete, setThreadToDelete] = useState<CollaborationThread | null>(null);

  const [threadForm, setThreadForm] = useState({ title: '', description: '', createdByResourceId: '' });
  const [commentForm, setCommentForm] = useState({ content: '', authorResourceId: '' });
  const [attachmentForm, setAttachmentForm] = useState({ fileName: '', fileUrl: '', mimeType: '', sizeBytes: '' });

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

  const myResourceId = myProfessional ? getProfessionalId(myProfessional as Professional & Record<string, unknown>) : undefined;
  const canManageThread = useCallback(
    (thread: CollaborationThread) => isAdmin || (myResourceId != null && thread.createdByResourceId === myResourceId),
    [isAdmin, myResourceId],
  );

  const selectedThread = selectedThreadId ? threads.find((thread) => thread.threadId === selectedThreadId) : undefined;
  const selectedThreadComments = useMemo(
    () => sortByCreatedAtAsc(selectedThreadId ? commentsByThread[selectedThreadId] ?? [] : []),
    [selectedThreadId, commentsByThread],
  );

  const threadDescription = useCallback(
    (threadId: number) => sortByCreatedAtAsc(commentsByThread[threadId] ?? [])[0]?.content,
    [commentsByThread],
  );

  const loadProjectData = useCallback(async (projectId: number) => {
    const threadResult = await threadService.getByProject(projectId).then(
      (value) => ({ status: 'fulfilled' as const, value }),
      () => ({ status: 'rejected' as const, value: [] as CollaborationThread[] }),
    );

    const loadedThreads = threadResult.value;
    setThreads(loadedThreads);
    setSelectedThreadId(null);

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

    if (threadResult.status === 'rejected') {
      setMessage({ type: 'warning', text: 'No se pudieron cargar los hilos. Revisa la ruta de Colaboración en el BFF.' });
    }
  }, []);

  const loadPage = useCallback(async () => {
    setLoading(true);
    setMessage(null);
    try {
      const [projectResult, professionalResult] = await Promise.allSettled([
        projectService.getAll(),
        professionalService.getAll(),
      ]);

      const allProjects = projectResult.status === 'fulfilled' ? projectResult.value : [];
      setProfessionals(professionalResult.status === 'fulfilled' ? professionalResult.value : []);

      let scopedProjects = allProjects;

      if (isAdmin) {
        setHasOwnProfessional(true);
      } else {
        const me = await professionalService.getMe();
        setMyProfessional(me);
        setHasOwnProfessional(!!me);

        if (me) {
          const meId = getProfessionalId(me as Professional & Record<string, unknown>);
          const myAssignments = meId ? await assignmentService.getByResource(meId) : [];
          const myProjectIds = new Set(myAssignments.map((assignment) => assignment.projectId));
          scopedProjects = allProjects.filter((project) => myProjectIds.has(getProjectId(project as Project & Record<string, unknown>) ?? -1));
        } else {
          scopedProjects = [];
        }
      }

      setProjects(scopedProjects);

      const projectId = selectedProjectId && scopedProjects.some((project) => getProjectId(project as Project & Record<string, unknown>) === selectedProjectId)
        ? selectedProjectId
        : (scopedProjects[0] ? getProjectId(scopedProjects[0] as Project & Record<string, unknown>) : null);
      setSelectedProjectId(projectId);

      if (projectId) {
        await loadProjectData(projectId);
      } else {
        setThreads([]);
        setCommentsByThread({});
        setAttachmentsByComment({});
        if (isAdmin || hasOwnProfessional) {
          setMessage({ type: 'warning', text: 'No hay proyectos disponibles para mostrar colaboración.' });
        }
      }
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo cargar Colaboración.' });
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAdmin, loadProjectData]);

  useEffect(() => { loadPage(); }, [loadPage]);

  const handleProjectChange = async (projectId: number) => {
    setSelectedProjectId(projectId);
    setMessage(null);
    setLoading(true);
    try {
      await loadProjectData(projectId);
    } finally {
      setLoading(false);
    }
  };

  const firstProfessionalId = isAdmin
    ? (professionals[0] ? getProfessionalId(professionals[0] as Professional & Record<string, unknown>) : undefined)
    : (myProfessional ? getProfessionalId(myProfessional as Professional & Record<string, unknown>) : undefined);

  const handleCreateThread = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedProjectId) return;
    setSaving(true);
    setMessage(null);
    try {
      const authorId = threadForm.createdByResourceId ? Number(threadForm.createdByResourceId) : firstProfessionalId;
      const created = await threadService.create({
        projectId: selectedProjectId,
        title: threadForm.title,
        status: 'OPEN',
        createdByResourceId: authorId,
      });
      await commentService.create({
        threadId: created.threadId,
        authorResourceId: authorId,
        content: threadForm.description,
      });
      setThreadForm({ title: '', description: '', createdByResourceId: '' });
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Hilo creado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear el hilo.' });
    } finally {
      setSaving(false);
    }
  };

  const handleStartEditThread = (thread: CollaborationThread) => {
    setEditingThreadId(thread.threadId);
    setEditTitle(thread.title);
  };

  const handleSaveEditThread = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!editingThreadId || !selectedProjectId) return;
    setSaving(true);
    setMessage(null);
    try {
      await threadService.update(editingThreadId, { title: editTitle });
      setEditingThreadId(null);
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Hilo actualizado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo actualizar el hilo.' });
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteThread = async () => {
    if (!threadToDelete || !selectedProjectId) return;
    setMessage(null);
    try {
      await threadService.delete(threadToDelete.threadId);
      setThreadToDelete(null);
      await loadProjectData(selectedProjectId);
      setMessage({ type: 'success', text: 'Hilo eliminado correctamente.' });
    } catch (error) {
      setThreadToDelete(null);
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo eliminar el hilo.' });
    }
  };

  const handleCreateComment = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedProjectId || !selectedThreadId) return;
    setSaving(true);
    setMessage(null);
    try {
      const createdComment = await commentService.create({
        threadId: selectedThreadId,
        authorResourceId: commentForm.authorResourceId ? Number(commentForm.authorResourceId) : firstProfessionalId,
        content: commentForm.content,
      });

      if (attachmentForm.fileName && attachmentForm.fileUrl) {
        await attachmentService.create({
          commentId: createdComment.commentId,
          fileName: attachmentForm.fileName,
          fileUrl: attachmentForm.fileUrl,
          mimeType: attachmentForm.mimeType || undefined,
        });
      }

      setCommentForm({ content: '', authorResourceId: '' });
      setAttachmentForm({ fileName: '', fileUrl: '', mimeType: '', sizeBytes: '' });
      setShowComposerAttach(false);
      await loadProjectData(selectedProjectId);
      setSelectedThreadId(selectedThreadId);
      setMessage({ type: 'success', text: 'Comentario creado correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear el comentario.' });
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
          <h1 className="page-title"><IonIcon icon={chatbubblesOutline} />Colaboración</h1>
          <p className="page-subtitle">
            {isAdmin ? 'Hilos, comentarios y archivos de todos los proyectos.' : 'Hilos, comentarios y archivos de los proyectos en los que estás asignado.'}
          </p>
        </div>
        <IonButton type="button" fill="outline" onClick={loadPage}>Actualizar</IonButton>
      </div>

      {message && <IonText color={message.type}><p>{message.text}</p></IonText>}

      {!isAdmin && !hasOwnProfessional ? (
        <IonText color="warning"><p>Tu cuenta todavía no tiene una ficha profesional vinculada, así que no se pueden determinar tus proyectos.</p></IonText>
      ) : (
        <>
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
              {projects.length === 0 && <p className="muted ion-padding-start">No tienes proyectos asignados todavía.</p>}
            </IonCardContent>
          </IonCard>

          {!selectedThread && (
            <div className="card-grid ion-margin-top">
              <IonCard className="app-card">
                <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={chatbubblesOutline} />Nuevo hilo</IonCardTitle></IonCardHeader>
                <IonCardContent>
                  <form onSubmit={handleCreateThread} className="form-grid">
                    <IonItem><IonInput label="Título" labelPlacement="stacked" value={threadForm.title} required onIonInput={(event) => setThreadForm((current) => ({ ...current, title: String(event.detail.value ?? '') }))} /></IonItem>
                    <IonItem className="form-grid-span"><IonTextarea label="Descripción" labelPlacement="stacked" value={threadForm.description} autoGrow required placeholder="¿De qué trata este hilo?" onIonInput={(event) => setThreadForm((current) => ({ ...current, description: String(event.detail.value ?? '') }))} /></IonItem>
                    {isAdmin && (
                      <IonItem>
                        <IonSelect label="Creador" labelPlacement="stacked" value={threadForm.createdByResourceId} onIonChange={(event) => setThreadForm((current) => ({ ...current, createdByResourceId: String(event.detail.value ?? '') }))}>
                          <IonSelectOption value="">Usar primer profesional disponible</IonSelectOption>
                          {professionals.map((professional) => {
                            const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                            return id ? <IonSelectOption key={id} value={String(id)}>{professionalName(professional)}</IonSelectOption> : null;
                          })}
                        </IonSelect>
                      </IonItem>
                    )}
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
                        editingThreadId === thread.threadId ? (
                          <div key={thread.threadId} className="ion-padding-vertical">
                            <form onSubmit={handleSaveEditThread} className="form-grid">
                              <IonInput label="Título" labelPlacement="stacked" value={editTitle} required onIonInput={(event) => setEditTitle(String(event.detail.value ?? ''))} />
                              <div className="button-row">
                                <IonButton type="submit" size="small" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</IonButton>
                                <IonButton type="button" size="small" fill="clear" onClick={() => setEditingThreadId(null)}>Cancelar</IonButton>
                              </div>
                            </form>
                          </div>
                        ) : (
                          <IonItem key={thread.threadId} detail={false} lines="full">
                            <IonLabel className="thread-row">
                              <p className="thread-title" onClick={() => setSelectedThreadId(thread.threadId)}>{thread.title}</p>
                              <p className="thread-description">{threadDescription(thread.threadId) ?? 'Sin descripción'}</p>
                              <p className="muted">{professionalName(professionalsById.get(thread.createdByResourceId ?? 0))} · {formatDate(thread.createdAt)}</p>
                              <div className="thread-row-actions">
                                <IonBadge color={threadStatusColor(thread.status)}>{formatStatus(thread.status)}</IonBadge>
                                {canManageThread(thread) && (
                                  <>
                                    <IonButton size="small" fill="clear" onClick={() => handleStartEditThread(thread)}>Editar</IonButton>
                                    <IonButton size="small" fill="clear" color="danger" onClick={() => setThreadToDelete(thread)}>Eliminar</IonButton>
                                  </>
                                )}
                              </div>
                            </IonLabel>
                          </IonItem>
                        )
                      ))}
                    </IonList>
                  )}
                </IonCardContent>
              </IonCard>
            </div>
          )}

          {selectedThread && (
            <div className="comment-feed ion-margin-top">
              <IonButton type="button" fill="clear" onClick={() => setSelectedThreadId(null)}>
                <IonIcon icon={arrowBackOutline} slot="start" />Volver a hilos
              </IonButton>

              <IonCard className="app-card">
                <IonCardHeader>
                  <div className="card-title-row">
                    <IonCardTitle>{selectedThread.title}</IonCardTitle>
                    <IonBadge color={threadStatusColor(selectedThread.status)}>{formatStatus(selectedThread.status)}</IonBadge>
                  </div>
                  <p className="muted">{professionalName(professionalsById.get(selectedThread.createdByResourceId ?? 0))} · {formatDate(selectedThread.createdAt)}</p>
                </IonCardHeader>
              </IonCard>

              <IonCard className="app-card">
                <IonCardContent>
                  <form onSubmit={handleCreateComment} className="form-grid">
                    {isAdmin && (
                      <IonItem>
                        <IonSelect label="Autor" labelPlacement="stacked" value={commentForm.authorResourceId} onIonChange={(event) => setCommentForm((current) => ({ ...current, authorResourceId: String(event.detail.value ?? '') }))}>
                          <IonSelectOption value="">Usar primer profesional disponible</IonSelectOption>
                          {professionals.map((professional) => {
                            const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                            return id ? <IonSelectOption key={id} value={String(id)}>{professionalName(professional)}</IonSelectOption> : null;
                          })}
                        </IonSelect>
                      </IonItem>
                    )}
                    <IonItem className="form-grid-span"><IonTextarea label="Escribe un comentario..." labelPlacement="stacked" value={commentForm.content} autoGrow required onIonInput={(event) => setCommentForm((current) => ({ ...current, content: String(event.detail.value ?? '') }))} /></IonItem>

                    {showComposerAttach && (
                      <>
                        <IonItem><IonInput label="Nombre archivo" labelPlacement="stacked" value={attachmentForm.fileName} onIonInput={(event) => setAttachmentForm((current) => ({ ...current, fileName: String(event.detail.value ?? '') }))} /></IonItem>
                        <IonItem><IonInput label="URL archivo" labelPlacement="stacked" value={attachmentForm.fileUrl} onIonInput={(event) => setAttachmentForm((current) => ({ ...current, fileUrl: String(event.detail.value ?? '') }))} /></IonItem>
                      </>
                    )}

                    <div className="comment-composer-actions">
                      <IonButton type="button" size="small" fill={showComposerAttach ? 'solid' : 'outline'} onClick={() => setShowComposerAttach((value) => !value)}>
                        <IonIcon icon={attachOutline} slot="start" />Adjuntar archivo
                      </IonButton>
                      <IonButton type="submit" disabled={saving}>
                        <IonIcon icon={sendOutline} slot="start" />{saving ? 'Guardando...' : 'Comentar'}
                      </IonButton>
                    </div>
                  </form>
                </IonCardContent>
              </IonCard>

              {selectedThreadComments.length === 0 ? (
                <p className="muted ion-text-center">No hay comentarios en este hilo todavía. Sé el primero en comentar.</p>
              ) : (
                selectedThreadComments.map((comment) => (
                  <div key={comment.commentId}>
                    <div className="comment-bubble">
                      <p>{comment.content}</p>
                    </div>
                    <div className="comment-meta muted">
                      <strong>{professionalName(professionalsById.get(comment.authorResourceId ?? 0))}</strong>
                      <span>· {formatDate(comment.createdAt)}</span>
                    </div>
                    {(attachmentsByComment[comment.commentId] ?? []).map((attachment) => (
                      <div key={attachment.attachmentId} className="comment-attachment">
                        <IonIcon icon={attachOutline} />
                        <a href={attachment.fileUrl} target="_blank" rel="noreferrer">{attachment.fileName}</a>
                      </div>
                    ))}
                  </div>
                ))
              )}
            </div>
          )}
        </>
      )}

      {threadToDelete && (
        <ConfirmModal
          title={`Eliminar hilo "${threadToDelete.title}"`}
          message="Esta acción eliminará el hilo y sus comentarios permanentemente."
          confirmLabel="Eliminar"
          danger
          onConfirm={handleDeleteThread}
          onCancel={() => setThreadToDelete(null)}
        />
      )}
    </div>
  );
}
