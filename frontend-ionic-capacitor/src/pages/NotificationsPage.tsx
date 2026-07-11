import type * as React from 'react';
import { useCallback, useEffect, useState } from 'react';
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
import { documentTextOutline, mailOutline, notificationsOutline, paperPlaneOutline, settingsOutline } from 'ionicons/icons';
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
const defaultEventTypes = ['ASSIGNMENT_CREATED', 'MENTION_CREATED', 'TASK_STATUS_CHANGED', 'PROJECT_STATUS_CHANGED', 'KPI_ALERT', 'WELCOME', 'TEST_NOTIFICATION'];

type TabValue = 'inbox' | 'test' | 'preferences' | 'templates';

function professionalName(professional?: Professional | null) {
  if (!professional) return 'Mi usuario';
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

export function NotificationsPage() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const [myProfessional, setMyProfessional] = useState<Professional | null>(null);
  const [inbox, setInbox] = useState<Awaited<ReturnType<typeof notificationService.getMyInbox>>>([]);
  const [preferences, setPreferences] = useState<NotificationPreference[]>([]);
  const [templates, setTemplates] = useState<NotificationTemplate[]>([]);
  const [activeTab, setActiveTab] = useState<TabValue>('inbox');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger' | 'warning'; text: string } | null>(null);

  const [templateForm, setTemplateForm] = useState({
    eventType: 'ASSIGNMENT_CREATED',
    channel: 'IN_APP' as NotificationChannel,
    subjectTemplate: 'Nueva notificación: {{eventType}}',
    bodyTemplate: 'Se generó el evento {{eventType}} desde {{sourceService}}. {{message}}',
    language: 'es',
  });

  const loadPage = useCallback(async () => {
    setLoading(true);
    setMessage(null);
    try {
      const [professionalResult, inboxResult, preferencesResult, templateResult] = await Promise.allSettled([
        professionalService.getMe(),
        notificationService.getMyInbox(),
        notificationPreferenceService.getMine(),
        isAdmin ? notificationTemplateService.getAll() : Promise.resolve([] as NotificationTemplate[]),
      ]);

      setMyProfessional(professionalResult.status === 'fulfilled' ? professionalResult.value : null);
      setInbox(inboxResult.status === 'fulfilled' ? inboxResult.value : []);
      setPreferences(preferencesResult.status === 'fulfilled' ? preferencesResult.value : []);
      setTemplates(templateResult.status === 'fulfilled' ? templateResult.value : []);

      if (inboxResult.status === 'rejected') {
        setMessage({ type: 'warning', text: inboxResult.reason instanceof Error ? inboxResult.reason.message : 'No se pudo cargar tu bandeja de notificaciones.' });
      }
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo cargar Notificaciones.' });
    } finally {
      setLoading(false);
    }
  }, [isAdmin]);

  useEffect(() => { loadPage(); }, [loadPage]);

  const handleSendTest = async () => {
    setSaving(true);
    setMessage(null);
    try {
      const response = await notificationService.sendTestToMe();
      setMessage({ type: 'success', text: response.message || 'Notificación de prueba enviada a tu bandeja.' });
      setInbox(await notificationService.getMyInbox());
      setActiveTab('inbox');
    } catch (error) {
      setMessage({ type: 'danger', text: error instanceof Error ? error.message : 'No se pudo enviar la notificación de prueba.' });
    } finally {
      setSaving(false);
    }
  };

  const savePreference = async (channel: NotificationChannel, enabled: boolean) => {
    setMessage(null);
    try {
      await notificationPreferenceService.saveMine({ channel, enabled, frequency: 'IMMEDIATE' });
      setPreferences(await notificationPreferenceService.getMine());
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

  if (loading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando notificaciones...</p></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title"><IonIcon icon={notificationsOutline} />Notificaciones</h1>
          <p className="page-subtitle">
            Bandeja personal de {professionalName(myProfessional)}. Por seguridad, cada usuario solo puede ver sus propias notificaciones.
          </p>
        </div>
        <IonButton type="button" fill="outline" onClick={loadPage}>Actualizar</IonButton>
      </div>

      {message && <IonText color={message.type}><p>{message.text}</p></IonText>}

      <IonCard className="app-card ion-margin-bottom">
        <IonCardContent>
          <p className="muted">
            La notificación de bienvenida se crea automáticamente al abrir esta bandeja o al registrarse. Las demás llegan automáticamente desde Asignaciones, Proyectos, Tareas y Colaboración cuando esos módulos generan eventos.
          </p>
        </IonCardContent>
      </IonCard>

      <IonSegment value={activeTab} scrollable onIonChange={(event) => setActiveTab((event.detail.value as TabValue) ?? 'inbox')}>
        <IonSegmentButton value="inbox">Mi bandeja ({inbox.length})</IonSegmentButton>
        <IonSegmentButton value="test">Prueba</IonSegmentButton>
        <IonSegmentButton value="preferences">Mis preferencias</IonSegmentButton>
        {isAdmin && <IonSegmentButton value="templates">Plantillas ({templates.length})</IonSegmentButton>}
      </IonSegment>

      {activeTab === 'inbox' && (
        <div className="section-bg">
        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={mailOutline} />Mi bandeja de entrada</IonCardTitle></IonCardHeader>
          <IonCardContent>
            {inbox.length === 0 ? (
              <p className="muted">Todavía no tienes notificaciones.</p>
            ) : (
              <IonList inset>
                {inbox.map((item) => (
                  <IonItem key={item.dispatchId}>
                    <IonLabel>
                      <h2>{item.subject || `Evento #${item.eventId}`}</h2>
                      <p>{item.body || 'Sin contenido renderizado.'}</p>
                      <p className="muted">{formatDate(item.sentAt)}</p>
                      {item.errorMessage && <p className="muted">Error: {item.errorMessage}</p>}
                    </IonLabel>
                    <IonBadge color={statusColor(item.deliveryStatus)} slot="end">{formatStatus(item.deliveryStatus)}</IonBadge>
                  </IonItem>
                ))}
              </IonList>
            )}
          </IonCardContent>
        </IonCard>
        </div>
      )}

      {activeTab === 'test' && (
        <div className="section-bg">
        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={paperPlaneOutline} />Enviar prueba a mi bandeja</IonCardTitle></IonCardHeader>
          <IonCardContent>
            <p className="muted">Esta acción crea una notificación IN_APP solo para tu usuario logeado.</p>
            <IonButton type="button" onClick={handleSendTest} disabled={saving}>{saving ? 'Enviando...' : 'Enviar notificación de prueba'}</IonButton>
          </IonCardContent>
        </IonCard>
        </div>
      )}

      {activeTab === 'preferences' && (
        <div className="section-bg">
        <IonCard className="app-card">
          <IonCardHeader><IonCardTitle className="section-title"><IonIcon icon={settingsOutline} />Mis preferencias</IonCardTitle></IonCardHeader>
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
        </div>
      )}

      {isAdmin && activeTab === 'templates' && (
        <div className="section-bg card-grid ion-margin-top">
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
