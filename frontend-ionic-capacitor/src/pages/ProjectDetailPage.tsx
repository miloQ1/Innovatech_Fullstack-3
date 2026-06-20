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
import { apiClient } from '../api/apiClient';
import { memberService, phaseService, projectService, taskService } from '../api/projectService';
import { MembersTab } from '../components/projects/tabs/MembersTab';
import { OverviewTab } from '../components/projects/tabs/OverviewTab';
import { PhasesTab } from '../components/projects/tabs/PhasesTab';
import { PhaseBoard } from '../components/projects/PhaseBoard';
import { ConfirmModal } from '../components/shared/ConfirmModal';
import type { Phase, Project, ProjectMember, ProjectStatus, Task } from '../types/projects';
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
  const [members, setMembers] = useState<ProjectMember[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [warning, setWarning] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState('overview');
  const [showPhaseForm, setShowPhaseForm] = useState(false);
  const [phaseName, setPhaseName] = useState('');
  const [phaseStart, setPhaseStart] = useState('');
  const [phaseEnd, setPhaseEnd] = useState('');
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

    const [projectResult, phaseResult, taskResult, memberResult] = await Promise.allSettled([
      projectService.getById(projectId),
      phaseService.getByProject(projectId),
      taskService.getByProject(projectId),
      memberService.getByProject(projectId),
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

    if (memberResult.status === 'fulfilled') {
      setMembers(memberResult.value);
    } else {
      setMembers([]);
      warnings.push('miembros');
    }

    if (warnings.length > 0) setWarning(`No se pudieron cargar: ${warnings.join(', ')}. Revisa que las rutas estén expuestas en el BFF.`);
    setIsLoading(false);
  }, [projectId]);

  useEffect(() => { loadData(); }, [loadData]);

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

  const handleAddPhase = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!projectId) return;
    try {
      const newPhase = await phaseService.create(projectId, {
        name: phaseName,
        sequenceOrder: phases.length + 1,
        plannedStart: phaseStart || undefined,
        plannedEnd: phaseEnd || undefined,
      });
      setPhaseName('');
      setPhaseStart('');
      setPhaseEnd('');
      setShowPhaseForm(false);
      await loadData();
      setActiveTab(String(newPhase.phaseId));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo crear la fase');
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
      <Link to={clientId ? `/clients/${clientId}` : '/projects'}>← {clientName}</Link>
      <div className="page-header ion-margin-top">
        <div>
          <h1 className="page-title">{project.name}</h1>
          <p className="page-subtitle">#{project.code} · {tasks.length} tareas · {members.length} miembros</p>
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
        <div className="inline-row ion-margin-bottom">
          <span className="muted">Cambiar estado:</span>
          <IonSelect interface="popover" value={project.status} onIonChange={(e) => handleStatusChange(e.detail.value as ProjectStatus)}>
            {projectStatuses.map((status) => <IonSelectOption key={status} value={status}>{formatStatus(status)}</IonSelectOption>)}
          </IonSelect>
        </div>
      )}

      <IonSegment value={activeTab} scrollable onIonChange={(e) => setActiveTab(String(e.detail.value ?? 'overview'))}>
        <IonSegmentButton value="overview">Resumen</IonSegmentButton>
        <IonSegmentButton value="phases">Fases ({phases.length})</IonSegmentButton>
        {phases.map((phase) => <IonSegmentButton key={phase.phaseId} value={String(phase.phaseId)}>{phase.name}</IonSegmentButton>)}
        <IonSegmentButton value="members">Miembros ({members.length})</IonSegmentButton>
      </IonSegment>

      <div className="button-row ion-margin-top">
        <IonButton type="button" fill="outline" onClick={() => setShowPhaseForm((value) => !value)}>{showPhaseForm ? 'Cancelar fase' : '+ Agregar fase'}</IonButton>
      </div>

      {showPhaseForm && (
        <IonCard className="app-card">
          <IonCardContent>
            <form onSubmit={handleAddPhase} className="form-grid">
              <IonItem><IonInput label="Nombre de fase" labelPlacement="stacked" value={phaseName} required onIonInput={(e) => setPhaseName(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Inicio" labelPlacement="stacked" type="date" value={phaseStart} onIonInput={(e) => setPhaseStart(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Término" labelPlacement="stacked" type="date" value={phaseEnd} onIonInput={(e) => setPhaseEnd(String(e.detail.value ?? ''))} /></IonItem>
              <IonButton type="submit">Guardar fase</IonButton>
            </form>
          </IonCardContent>
        </IonCard>
      )}

      <div className="ion-margin-top">
        {activeTab === 'overview' && <OverviewTab project={project} phases={phases} tasks={tasks} onTabChange={setActiveTab} />}
        {activeTab === 'phases' && <PhasesTab phases={phases} tasks={tasks} projectId={projectId} onReload={loadData} onTabChange={setActiveTab} />}
        {activeTab === 'members' && <MembersTab members={members} projectId={projectId} onReload={loadData} />}
        {activePhase && <PhaseBoard phase={activePhase} tasks={phaseTasks} projectId={projectId} onTasksChange={loadData} />}
      </div>

      {confirmDelete && <ConfirmModal title={`Eliminar proyecto "${project.name}"`} message="Esta acción eliminará el proyecto y sus datos asociados si el backend lo permite." confirmLabel="Eliminar" danger onConfirm={handleDeleteProject} onCancel={() => setConfirmDelete(false)} />}
    </div>
  );
}
