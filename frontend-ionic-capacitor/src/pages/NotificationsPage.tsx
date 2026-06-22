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
  IonToggle,
} from '@ionic/react';
import { mailOutline, notificationsOutline, paperPlaneOutline, settingsOutline, documentTextOutline } from 'ionicons/icons';
import {
  notificationPreferenceService,
  notificationService,
  notificationTemplateService,
} from '../api/notificationService';
import { professionalService } from '../api/resourcesService';
import { useAuth } from '../hooks/useAuth';
import type { NotificationChannel, NotificationPreference, NotificationTemplate } from '../types/notifications';
import type { Professional } from '../types/resources';
import { getProfessionalId } from '../utils/ids';
import { formatStatus } from '../utils/formatStatus';

const channels: NotificationChannel[] = ['IN_APP', 'EMAIL', 'WEBHOOK'];
const defaultEventTypes = ['ASSIGNMENT_CREATED', 'MENTION_CREATED', 'TASK_STATUS_CHANGED', 'PROJECT_STATUS_CHANGED', 'KPI_ALERT'];

type TabValue = 'inbox' | 'send' | 'preferences' | 'templates';

function professionalName(professional?: Professional) {
  if (!professional) return 'Recurso no encontrado';
  const name = `${professional.firstName ?? ''} ${professional.lastName ?? ''}`.trim();
  return name || professional.email || `Recurso #${getProfessionalId(professional as Professional & Record<string, unknown>) ?? ''}`;
}

function statusColor(status?: string) {
  if (status === 'SENT' || status === 'PROCESSED') return 'success';
  if (status === 'FAILED') return 'danger';
  if (status === 'SKIPPED') return 'medium';
  return 'warning';
}

function channelLabel(channel: NotificationChannel) {
  if (channel === 'IN_APP') return 'In-app';
  if (channel === 'EMAIL') return 'Email';
  return 'Webhook';
}

function formatDate(value?: string) {
  if (!value) return 'Sin fecha';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('es-CL', { dateStyle: 'short', timeStyle: 'short' });
}

function asChannelArray(value: unknown): NotificationChannel[] {
  if (Array.isArray(value)) return value.filter((item): item is NotificationChannel => channels.includes(item as NotificationChannel));
  if (typeof value === 'string' && channels.includes(value as NotificationChannel)) return [value as NotificationChannel];
  return ['IN_APP'];
}

export function NotificationsPage() {
  const { user } = useAuth();
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [selectedResourceId, setSelectedResourceId] = useState<number | null>(null);
  const [inbox, setInbox] = useState<Awaited<ReturnType<typeof notificationService.getInboxByRecipient>>>([]);
  const [preferences, setPreferences] = useState<NotificationPreference[]>([]);
  const [templates, setTemplates] = useState<NotificationTemplate[]>([]);
  const [activeTab, setActiveTab] = useState<TabValue>('inbox');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger' | 'warning'; text: string } | null>(null);

  const [sendForm, setSendForm] = useState({
    eventType: 'ASSIGNMENT_CREATED',
    entityId: '',
    channels: ['IN_APP'] as NotificationChannel[],
    projectName: '',
    body: 'Tienes una nueva notificación en Innovatech.',
  });

  const [templateForm, setTemplateForm] = useState({
    eventType: 'ASSIGNMENT_CREATED',
    channel: 'IN_APP' as NotificationChannel,
    subjectTemplate: 'Nueva notificación: {{eventType}}',
    bodyTemplate: 'Se generó el evento {{eventType}} desde {{sourceService}}. {{message}}',
    language: 'es',
  });

  const professionalsById = useMemo(() => new Map(
    professionals
      .map((item) => [getProfessionalId(item as Professional & Record<string, unknown>), item] as const)
      .filter(([id]) => !!id),
  ), [professionals]);

  const loadInbox = useCallback(async (resourceId: number) => {
    const [inboxResult, preferencesResult] = await Promise.allSettled([
      notificationService.getInboxByRecipient(resourceId),
      notificationPreferenceService.getByResource(resourceId),
    ]);

    setInbox(inboxResult.status === 'fulfilled' ? inboxResult.value : []);
    setPreferences(preferencesResult.status === 'fulfilled' ? preferencesResult.value : []);

    if (inboxResult.status === 'rejected' || preferencesResult.status === 'rejected') {
      setMessage({ type: 'warning', text: 'No se pudieron cargar todas las notificaciones. Revisa que el microservicio Notificaciones y sus rutas en el BFF estén levantados.' });
    }
  }, []);

  const loadPage = useCallback(async () => {
    setLoading(true);
    setMessage(null);
    try {
      const [professionalResult, templateResult] = await Promise.allSettled([
        professionalService.getAll(),
        notificationTemplateService.getAll(),
      ]);

      const loadedProfessionals = professionalResult.status === 'fulfilled' ? professionalResult.value : [];
      setProfessionals(loadedProfessionals);
      setTemplates(templateResult.status === 'fulfilled' ? templateResult.value : []);

      const currentProfessional = loadedProfessionals.find((item) => item.email?.toLowerCase() === user?.email?.toLowerCase());
      const currentId = currentProfessional ? getProfessionalId(currentProfessional as Professional & Record<string, unknown>) : undefined;
      const fallbackId = loadedProfessionals[0] ? getProfessionalId(loadedProfessionals[0] as Professional & Record<string, unknown>) : undefined;
      const resourceId = selectedResourceId ?? currentId ?? fallbackId ?? null;
      setSelectedResourceId(resourceId);

      if (resourceId) {
        await loadInbox(resourceId);
      } else {
        setInbox([]);
        setPreferences([]);
        setMessage({ type: 'warning', text: 'No hay profesionales creados. Para probar la bandeja, primero crea una ficha profesional en Recursos Humanos.' });
      }
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo cargar Notificaciones.' });
    } finally {
      setLoading(false);
    }
  }, [loadInbox, selectedResourceId, user?.email]);

  useEffect(() => { loadPage(); }, [loadPage]);

  const handleRecipientChange = async (resourceId: number) => {
    setSelectedResourceId(resourceId);
    setMessage(null);
    setLoading(true);
    try {
      await loadInbox(resourceId);
    } finally {
      setLoading(false);
    }
  };

  const handleSendNotification = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedResourceId) {
      setMessage({ type: 'danger', text: 'Selecciona un destinatario.' });
      return;
    }

    setSaving(true);
    setMessage(null);
    try {
      const response = await notificationService.send({
        sourceService: 'frontend-ionic-capacitor',
        eventType: sendForm.eventType,
        entityId: sendForm.entityId ? Number(sendForm.entityId) : undefined,
        recipientResourceIds: [selectedResourceId],
        channels: sendForm.channels,
        payload: {
          projectName: sendForm.projectName,
          message: sendForm.body,
          userName: user?.userName,
        },
      });
      setMessage({ type: 'success', text: response.message || 'Notificación enviada correctamente.' });
      await loadInbox(selectedResourceId);
      setActiveTab('inbox');
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo enviar la notificación.' });
    } finally {
      setSaving(false);
    }
  };

  const savePreference = async (channel: NotificationChannel, enabled: boolean) => {
    if (!selectedResourceId) return;
    setMessage(null);
    try {
      const existing = preferences.find((item) => item.channel === channel);
      if (existing) {
        await notificationPreferenceService.update(existing.preferenceId, { ...existing, enabled });
      } else {
        await notificationPreferenceService.create({ resourceId: selectedResourceId, channel, enabled, frequency: 'IMMEDIATE' });
      }
      await loadInbox(selectedResourceId);
      setMessage({ type: 'success', text: 'Preferencia guardada.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo guardar la preferencia.' });
    }
  };

  const handleCreateTemplate = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setMessage(null);
    try {
      const created = await notificationTemplateService.create({ ...templateForm, isActive: true });
      setTemplates((current) => [created, ...current]);
      setMessage({ type: 'success', text: 'Plantilla creada correctamente.' });
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo crear la plantilla.' });
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteTemplate = async (templateId: number) => {
    setMessage(null);
    try {
      await notificationTemplateService.delete(templateId);
      setTemplates((current) => current.filter((item) => item.templateId !== templateId));
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo eliminar la plantilla.' });
    }
  };

  const selectedProfessional = selectedResourceId ? professionalsById.get(selectedResourceId) : undefined;

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando notificaciones...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title"><IonIcon icon={notificationsOutline} />Notificaciones</h1>
          <p className="page-subtitle">Bandeja, preferencias, plantillas y prueba de despachos del microservicio Notificaciones.</p>
        </div>
        <IonButton type="button" fill="outline" onClick={loadPage}>Actualizar</IonButton>
      </div>

      {message && <IonText color={message.type}><p>{message.text}</p></IonText>}

      <IonCard className="app-card ion-margin-bottom">
        <IonCardContent>
          <IonItem>
            <IonSelect
              label="Ver bandeja de"
              labelPlacement="stacked"
              value={selectedResourceId ?? ''}
              onIonChange={(event) => handleRecipientChange(Number(event.detail.value))}
            >
              {professionals.map((professional) => {
                const id = getProfessionalId(professional as Professional & Record<string, unknown>);
                return id ? <IonSelectOption key={id} value={id}>{professionalName(professional)} · #{id}</IonSelectOption> : null;
              })}
            </IonSelect>
          </IonItem>
          {selectedProfessional && <p className="muted ion-padding-start">Mostrando notificaciones de {professionalName(selectedProfessional)}.</p>}
        </IonCardContent>
      </IonCard>

      <IonSegment value={activeTab} scrollable onIonChange={(event) => setActiveTab((event.detail.value as TabValue) ?? 'inbox')}>
        <IonSegmentButton value="inbox">Bandeja ({inbox.length})</IonSegmentButton>
        <IonSegmentButton value="send">Enviar prueba</IonSegmentButton>
        <IonSegmentButton value="preferences">Preferencias</IonSegmentButton>
        <IonSegmentButton value="templates">Plantillas ({templates.length})</IonSegmentButton>
      </IonSegment>

      {activeTab === 'inbox' && (
        <IonCard className="app-card ion-margin-top">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={mailOutline} />Bandeja de entrada</IonCardTitle></IonCardHeader>
          <IonCardContent>
            {inbox.length === 0 ? (
              <p className="muted">Este recurso todavía no tiene notificaciones.</p>
            ) : (
              <IonList inset>
                {inbox.map((item) => (
                  <IonItem key={item.dispatchId}>
                    <IonLabel>
                      <h2>{item.subject || `Evento #${item.eventId}`}</h2>
                      <p>{item.body || 'Sin contenido renderizado.'}</p>
                      <p className="muted">{channelLabel(item.channel)} · {formatDate(item.sentAt)}</p>
                      {item.errorMessage && <p className="muted">Error: {item.errorMessage}</p>}
                    </IonLabel>
                    <IonBadge color={statusColor(item.deliveryStatus)} slot="end">{formatStatus(item.deliveryStatus)}</IonBadge>
                  </IonItem>
                ))}
              </IonList>
            )}
          </IonCardContent>
        </IonCard>
      )}

      {activeTab === 'send' && (
        <IonCard className="app-card ion-margin-top">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={paperPlaneOutline} />Enviar notificación de prueba</IonCardTitle></IonCardHeader>
          <IonCardContent>
            <form onSubmit={handleSendNotification} className="form-grid">
              <IonItem>
                <IonSelect label="Tipo de evento" labelPlacement="stacked" value={sendForm.eventType} onIonChange={(event) => setSendForm((current) => ({ ...current, eventType: String(event.detail.value) }))}>
                  {defaultEventTypes.map((eventType) => <IonSelectOption key={eventType} value={eventType}>{eventType}</IonSelectOption>)}
                </IonSelect>
              </IonItem>
              <IonItem><IonInput label="ID entidad relacionada" labelPlacement="stacked" type="number" value={sendForm.entityId} onIonInput={(event) => setSendForm((current) => ({ ...current, entityId: String(event.detail.value ?? '') }))} /></IonItem>
              <IonItem>
                <IonSelect multiple label="Canales" labelPlacement="stacked" value={sendForm.channels} onIonChange={(event) => setSendForm((current) => ({ ...current, channels: asChannelArray(event.detail.value) }))}>
                  {channels.map((channel) => <IonSelectOption key={channel} value={channel}>{channelLabel(channel)}</IonSelectOption>)}
                </IonSelect>
              </IonItem>
              <IonItem><IonInput label="Proyecto o contexto" labelPlacement="stacked" value={sendForm.projectName} onIonInput={(event) => setSendForm((current) => ({ ...current, projectName: String(event.detail.value ?? '') }))} /></IonItem>
              <IonItem className="form-grid-span"><IonTextarea label="Mensaje" labelPlacement="stacked" value={sendForm.body} autoGrow onIonInput={(event) => setSendForm((current) => ({ ...current, body: String(event.detail.value ?? '') }))} /></IonItem>
              <IonButton type="submit" disabled={saving || !selectedResourceId}>{saving ? 'Enviando...' : 'Enviar notificación'}</IonButton>
            </form>
          </IonCardContent>
        </IonCard>
      )}

      {activeTab === 'preferences' && (
        <IonCard className="app-card ion-margin-top">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={settingsOutline} />Preferencias del destinatario</IonCardTitle></IonCardHeader>
          <IonCardContent>
            <IonList inset>
              {channels.map((channel) => {
                const preference = preferences.find((item) => item.channel === channel);
                const checked = preference?.enabled ?? true;
                return (
                  <IonItem key={channel}>
                    <IonLabel>
                      <h2>{channelLabel(channel)}</h2>
                      <p>Frecuencia: {formatStatus(preference?.frequency ?? 'IMMEDIATE')}</p>
                    </IonLabel>
                    <IonToggle checked={checked} onIonChange={(event) => savePreference(channel, event.detail.checked)} slot="end" />
                  </IonItem>
                );
              })}
            </IonList>
          </IonCardContent>
        </IonCard>
      )}

      {activeTab === 'templates' && (
        <div className="card-grid ion-margin-top">
          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={documentTextOutline} />Nueva plantilla</IonCardTitle></IonCardHeader>
            <IonCardContent>
              <form onSubmit={handleCreateTemplate} className="form-grid">
                <IonItem>
                  <IonSelect label="Evento" labelPlacement="stacked" value={templateForm.eventType} onIonChange={(event) => setTemplateForm((current) => ({ ...current, eventType: String(event.detail.value) }))}>
                    {defaultEventTypes.map((eventType) => <IonSelectOption key={eventType} value={eventType}>{eventType}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem>
                  <IonSelect label="Canal" labelPlacement="stacked" value={templateForm.channel} onIonChange={(event) => setTemplateForm((current) => ({ ...current, channel: event.detail.value as NotificationChannel }))}>
                    {channels.map((channel) => <IonSelectOption key={channel} value={channel}>{channelLabel(channel)}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem><IonInput label="Asunto" labelPlacement="stacked" value={templateForm.subjectTemplate} onIonInput={(event) => setTemplateForm((current) => ({ ...current, subjectTemplate: String(event.detail.value ?? '') }))} /></IonItem>
                <IonItem className="form-grid-span"><IonTextarea label="Cuerpo" labelPlacement="stacked" value={templateForm.bodyTemplate} autoGrow required onIonInput={(event) => setTemplateForm((current) => ({ ...current, bodyTemplate: String(event.detail.value ?? '') }))} /></IonItem>
                <IonButton type="submit" disabled={saving}>{saving ? 'Guardando...' : 'Crear plantilla'}</IonButton>
              </form>
            </IonCardContent>
          </IonCard>

          <IonCard className="app-card">
            <IonCardHeader><IonCardTitle>Plantillas registradas</IonCardTitle></IonCardHeader>
            <IonCardContent>
              {templates.length === 0 ? <p className="muted">No hay plantillas creadas.</p> : (
                <IonList inset>
                  {templates.map((template) => (
                    <IonItem key={template.templateId}>
                      <IonLabel>
                        <h2>{template.eventType} · {channelLabel(template.channel)}</h2>
                        <p>{template.subjectTemplate || 'Sin asunto'}</p>
                        <p className="muted">{template.bodyTemplate}</p>
                      </IonLabel>
                      <IonBadge color={template.isActive === false ? 'medium' : 'success'} slot="end">{template.isActive === false ? 'Inactiva' : 'Activa'}</IonBadge>
                      <IonButton slot="end" size="small" fill="clear" color="danger" onClick={() => handleDeleteTemplate(template.templateId)}>Eliminar</IonButton>
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
