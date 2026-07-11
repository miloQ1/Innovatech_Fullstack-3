import type * as React from 'react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useHistory, useParams } from 'react-router-dom';
import { formatStatus } from '../utils/formatStatus';
import {
  IonBadge,
  IonButton,
  IonCard,
  IonCardContent,
  IonInput,
  IonItem,
  IonList,
  IonSegment,
  IonSegmentButton,
  IonSelect,
  IonSelectOption,
  IonSpinner,
  IonText,
  IonTextarea,
} from '@ionic/react';
import { IonIcon } from '@ionic/react';
import { informationCircleOutline, chevronDownOutline, chevronUpOutline } from 'ionicons/icons';
import { apiClient } from '../api/apiClient';
import { projectService, taskService, phaseService } from '../api/projectService';
import { assignmentService, professionalService } from '../api/resourcesService';
import { InfoTab } from '../components/projects/tabs/OverviewTab';
import { PhasesTab } from '../components/projects/tabs/PhasesTab';
import { AssignmentsTab } from '../components/projects/tabs/AssignmentsTab';
import { PhaseBoard } from '../components/projects/PhaseBoard';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import type { Phase, Project, ProjectStatus, Task } from '../types/projects';
import type { Assignment, Professional } from '../types/resources';
import { blurActiveElement, getClientId, toNumericId } from '../utils/ids';

const projectStatuses: ProjectStatus[] = ['IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'];

function statusColor(status: string) {
  if (status === 'COMPLETED' || status === 'DONE') return 'success';
  if (status === 'CANCELLED') return 'danger';
  if (status === 'ON_HOLD') return 'warning';
  return 'primary';
}

export function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const projectId = useMemo(() => toNumericId(id), [id]);
  const history = useHistory();
  const [project, setProject] = useState<Project | null>(null);
  const [phases, setPhases] = useState<Phase[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [warning, setWarning] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState('phases');
  const [infoExpanded, setInfoExpanded] = useState(true);
  const [isEditingProject, setIsEditingProject] = useState(false);
  const [projectForm, setProjectForm] = useState({ code: '', name: '', description: '', status: 'IN_PROGRESS' as ProjectStatus, startDate: '', endDate: '', budget: '' });
  const [savingProject, setSavingProject] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);

  const loadData = useCallback(async () => {
    if (!projectId) {
      setProject(null);
      setError('El ID del proyecto no es válido.');
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);
    setWarning(null);

    const [projectResult, phaseResult, taskResult, assignmentResult, professionalResult] = await Promise.allSettled([
      projectService.getById(projectId),
      phaseService.getByProject(projectId),
      taskService.getByProject(projectId),
      assignmentService.getByProject(projectId),
      professionalService.getAll(),
    ]);

    if (projectResult.status === 'fulfilled') {
      const projectData = projectResult.value;
      setProject(projectData);
      setProjectForm({
        code: projectData.code ?? '',
        name: projectData.name ?? '',
        description: projectData.description ?? '',
        status: projectData.status ?? 'IN_PROGRESS',
        startDate: projectData.startDate ?? '',
        endDate: projectData.endDate ?? '',
        budget: projectData.budget !== undefined && projectData.budget !== null ? String(projectData.budget) : '',
      });
    } else {
      setProject(null);
      setError(projectResult.reason instanceof Error ? projectResult.reason.message : 'No se pudo cargar el proyecto');
    }

    const warnings: string[] = [];
    if (phaseResult.status === 'fulfilled') {
      setPhases(phaseResult.value.sort((a, b) => (a.sequenceOrder ?? 0) - (b.sequenceOrder ?? 0)));
    } else {
      setPhases([]);
      warnings.push('fases');
    }

    if (taskResult.status === 'fulfilled') {
      setTasks(taskResult.value);
    } else {
      setTasks([]);
      warnings.push('tareas');
    }

    if (assignmentResult.status === 'fulfilled') {
      setAssignments(assignmentResult.value);
    } else {
      setAssignments([]);
      warnings.push('asignaciones');
    }

    if (professionalResult.status === 'fulfilled') {
      setProfessionals(professionalResult.value);
    } else {
      setProfessionals([]);
      warnings.push('profesionales');
    }

    if (warnings.length > 0) setWarning(`No se pudieron cargar: ${warnings.join(', ')}. Revisa que las rutas estén expuestas en el BFF.`);
    setIsLoading(false);
  }, [projectId]);

  useEffect(() => { loadData(); }, [loadData]);

  const reloadTasks = useCallback(async () => {
    if (!projectId) return;
    const result = await taskService.getByProject(projectId).catch(() => null);
    if (result) setTasks(result);
  }, [projectId]);

  const reloadPhases = useCallback(async () => {
    if (!projectId) return;
    const result = await phaseService.getByProject(projectId).catch(() => null);
    if (result) setPhases(result.sort((a, b) => (a.sequenceOrder ?? 0) - (b.sequenceOrder ?? 0)));
  }, [projectId]);

  const reloadAssignments = useCallback(async () => {
    if (!projectId) return;
    const result = await assignmentService.getByProject(projectId).catch(() => null);
    if (result) setAssignments(result);
  }, [projectId]);

  const updateProjectForm = (field: keyof typeof projectForm, value: string) => {
    setProjectForm((current) => ({ ...current, [field]: value }));
  };

  const handleStatusChange = async (newStatus: ProjectStatus) => {
    if (!projectId || !project) return;
    try {
      await apiClient.patch(`/api/projects/${projectId}/status`, { status: newStatus }, true);
    } catch {
      await projectService.update(projectId, { code: project.code, name: project.name, description: project.description, startDate: project.startDate, endDate: project.endDate, budget: project.budget, progressPct: project.progressPct, projectManagerId: project.projectManagerId, status: newStatus });
    }
    loadData();
  };

  const handleSaveProject = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!projectId || !project) return;
    setSavingProject(true);
    setError(null);
    try {
      await projectService.update(projectId, {
        code: projectForm.code,
        name: projectForm.name,
        description: projectForm.description || undefined,
        status: projectForm.status,
        startDate: projectForm.startDate || undefined,
        endDate: projectForm.endDate || undefined,
        budget: projectForm.budget ? Number(projectForm.budget) : undefined,
        progressPct: project.progressPct,
        projectManagerId: project.projectManagerId,
      });
      setIsEditingProject(false);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo actualizar el proyecto');
    } finally {
      setSavingProject(false);
    }
  };

  const handleDeleteProject = async () => {
    if (!projectId) return;
    try {
      await projectService.delete(projectId);
      blurActiveElement();
      history.push('/projects');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el proyecto');
      setConfirmDelete(false);
    }
  };

  if (isLoading) return <div className="ion-text-center ion-padding"><IonSpinner /><p className="muted">Cargando proyecto...</p></div>;

  if (!project || !projectId) {
    return (
      <IonCard className="app-card">
        <IonCardContent>
          <Link to="/projects">← Volver a proyectos</Link>
          <h2>Proyecto no encontrado</h2>
          {error && <IonText color="danger"><p>{error}</p></IonText>}
        </IonCardContent>
      </IonCard>
    );
  }

  const clientId = project.client ? getClientId(project.client as typeof project.client & Record<string, unknown>) : toNumericId(project.clientId);
  const clientName = project.client?.name ?? (clientId ? `Cliente #${clientId}` : 'Cliente no informado');
  const activePhase = phases.find((phase) => String(phase.phaseId) === activeTab);
  const phaseTasks = activePhase ? tasks.filter((task) => task.phaseId === activePhase.phaseId) : [];

  return (
    <div>
      <Link to={clientId ? `/clients/${clientId}` : '/projects'} className="back-link">
        <IonIcon icon={chevronDownOutline} className="back-link-icon" />
        {clientName}
      </Link>
      <div className="page-header ion-margin-top">
        <div>
          <h1 className="page-title">{project.name}</h1>
          <p className="page-subtitle">#{project.code} · {tasks.length} tareas · {assignments.length} asignaciones</p>
        </div>
        <div className="button-row">
          <IonBadge color={statusColor(project.status)}>{formatStatus(project.status)}</IonBadge>
          <IonButton type="button" fill="outline" onClick={() => setIsEditingProject((value) => !value)}>{isEditingProject ? 'Cancelar edición' : 'Editar proyecto'}</IonButton>
          <IonButton type="button" color="danger" fill="outline" onClick={() => setConfirmDelete(true)}>Eliminar</IonButton>
        </div>
      </div>

      {error && <IonText color="danger"><p>{error}</p></IonText>}
      {warning && <IonText color="warning"><p>{warning}</p></IonText>}

      {isEditingProject && (
        <IonCard className="app-card">
          <IonCardContent>
            <form onSubmit={handleSaveProject}>
              <IonList inset>
                <IonItem><IonInput label="Código" labelPlacement="stacked" value={projectForm.code} required onIonInput={(e) => updateProjectForm('code', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={projectForm.name} required onIonInput={(e) => updateProjectForm('name', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonTextarea label="Descripción" labelPlacement="stacked" value={projectForm.description} onIonInput={(e) => updateProjectForm('description', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem>
                  <IonSelect label="Estado" labelPlacement="stacked" value={projectForm.status} onIonChange={(e) => updateProjectForm('status', e.detail.value as ProjectStatus)}>
                    {projectStatuses.map((status) => <IonSelectOption key={status} value={status}>{formatStatus(status)}</IonSelectOption>)}
                  </IonSelect>
                </IonItem>
                <IonItem><IonInput label="Inicio" labelPlacement="stacked" type="date" value={projectForm.startDate} onIonInput={(e) => updateProjectForm('startDate', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Término" labelPlacement="stacked" type="date" value={projectForm.endDate} onIonInput={(e) => updateProjectForm('endDate', String(e.detail.value ?? ''))} /></IonItem>
                <IonItem><IonInput label="Presupuesto" labelPlacement="stacked" type="number" value={projectForm.budget} onIonInput={(e) => updateProjectForm('budget', String(e.detail.value ?? ''))} /></IonItem>
              </IonList>
              <IonButton expand="block" type="submit" disabled={savingProject}>{savingProject ? 'Guardando...' : 'Guardar cambios'}</IonButton>
            </form>
          </IonCardContent>
        </IonCard>
      )}

      {!isEditingProject && (
        <div className="project-info-panel">
          <button
            type="button"
            className="project-info-toggle"
            onClick={() => setInfoExpanded((v) => !v)}
          >
            <span className="project-info-toggle-left">
              <span className="project-info-toggle-icon">
                <IonIcon icon={informationCircleOutline} />
              </span>
              Información del proyecto
            </span>
            <IonIcon icon={infoExpanded ? chevronUpOutline : chevronDownOutline} className="project-info-chevron-icon" />
          </button>
          {infoExpanded && (
            <div className="project-info-body">
              <InfoTab project={project} />
              <div className="inline-row" style={{ marginTop: 10, marginBottom: 16 }}>
                <span className="muted" style={{ fontSize: '0.875rem' }}>Cambiar estado:</span>
                <IonSelect interface="popover" value={project.status} onIonChange={(e) => handleStatusChange(e.detail.value as ProjectStatus)}>
                  {projectStatuses.map((status) => <IonSelectOption key={status} value={status}>{formatStatus(status)}</IonSelectOption>)}
                </IonSelect>
              </div>
              <AssignmentsTab projectId={projectId} assignments={assignments} professionals={professionals} onReload={reloadAssignments} />
            </div>
          )}
        </div>
      )}

      <IonSegment value={activeTab} scrollable onIonChange={(e) => setActiveTab(String(e.detail.value ?? 'phases'))}>
        <IonSegmentButton value="phases">Fases ({phases.length})</IonSegmentButton>
        {phases.map((phase) => <IonSegmentButton key={phase.phaseId} value={String(phase.phaseId)}>{phase.name}</IonSegmentButton>)}
      </IonSegment>

      <div className="ion-margin-top">
        {activeTab === 'phases' && <PhasesTab phases={phases} tasks={tasks} projectId={projectId} onPhaseChange={reloadPhases} onTaskChange={reloadTasks} onTabChange={setActiveTab} />}
        {activePhase && <PhaseBoard phase={activePhase} tasks={phaseTasks} projectId={projectId} onTasksChange={reloadTasks} />}
      </div>

      {confirmDelete && <ConfirmModal title={`Eliminar proyecto "${project.name}"`} message="Esta acción eliminará el proyecto y sus datos asociados si el backend lo permite." confirmLabel="Eliminar" danger onConfirm={handleDeleteProject} onCancel={() => setConfirmDelete(false)} />}
    </div>
  );
}
