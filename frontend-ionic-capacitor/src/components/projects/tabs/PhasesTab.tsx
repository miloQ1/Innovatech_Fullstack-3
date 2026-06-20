import type * as React from 'react';
import { useState } from 'react';
import { IonBadge, IonButton, IonCard, IonCardContent, IonInput, IonItem, IonLabel, IonProgressBar } from '@ionic/react';
import type { Phase, Task } from '../../../types/projects';
import { phaseService, taskService } from '../../../api/projectService';
import { ConfirmModal } from '../../shared/ConfirmModal';
import { TaskModal } from '../TaskModal';
import { formatStatus } from '../../../utils/formatStatus';

interface PhasesTabProps {
  phases: Phase[];
  tasks: Task[];
  projectId: number;
  onReload: () => void;
  onTabChange: (tab: string) => void;
}

export function PhasesTab({ phases, tasks, projectId, onReload, onTabChange }: PhasesTabProps) {
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [editingPhaseId, setEditingPhaseId] = useState<number | null>(null);
  const [editPhaseName, setEditPhaseName] = useState('');
  const [editPhaseStart, setEditPhaseStart] = useState('');
  const [editPhaseEnd, setEditPhaseEnd] = useState('');
  const [addingTaskToPhase, setAddingTaskToPhase] = useState<number | null>(null);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [phaseToDelete, setPhaseToDelete] = useState<Phase | null>(null);
  const [taskToDelete, setTaskToDelete] = useState<Task | null>(null);

  const startEditPhase = (phase: Phase) => {
    setEditingPhaseId(phase.phaseId);
    setEditPhaseName(phase.name);
    setEditPhaseStart(phase.plannedStart ?? '');
    setEditPhaseEnd(phase.plannedEnd ?? '');
  };

  const handleEditPhase = async (event: React.FormEvent, phase: Phase) => {
    event.preventDefault();
    await phaseService.update(phase.phaseId, {
      name: editPhaseName,
      sequenceOrder: phase.sequenceOrder,
      plannedStart: editPhaseStart || undefined,
      plannedEnd: editPhaseEnd || undefined,
    });
    setEditingPhaseId(null);
    onReload();
  };

  const handleQuickAddTask = async (event: React.FormEvent, phaseId: number) => {
    event.preventDefault();
    if (!newTaskTitle.trim()) return;
    await taskService.create(projectId, { title: newTaskTitle.trim(), priority: null, status: 'TODO', phaseId } as any);
    setNewTaskTitle('');
    setAddingTaskToPhase(null);
    onReload();
  };

  const confirmDeletePhase = async () => {
    if (!phaseToDelete) return;
    await phaseService.delete(phaseToDelete.phaseId).catch(() => undefined);
    setPhaseToDelete(null);
    onReload();
  };

  const confirmDeleteTask = async () => {
    if (!taskToDelete) return;
    await taskService.deleteTask(taskToDelete.taskId).catch(() => undefined);
    setTaskToDelete(null);
    onReload();
  };

  if (phases.length === 0) {
    return <IonCard className="app-card"><IonCardContent><p className="muted">No hay fases todavía. Usa “Agregar fase” para comenzar.</p></IonCardContent></IonCard>;
  }

  return (
    <>
      {phases.map((phase) => {
        const phaseTasks = tasks.filter((task) => task.phaseId === phase.phaseId);
        const done = phaseTasks.filter((task) => task.status === 'DONE').length;
        const progress = phaseTasks.length ? done / phaseTasks.length : 0;
        const isEditing = editingPhaseId === phase.phaseId;

        return (
          <IonCard key={phase.phaseId} className="app-card">
            <IonCardContent>
              {isEditing ? (
                <form onSubmit={(event) => handleEditPhase(event, phase)} className="form-grid">
                  <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={editPhaseName} required onIonInput={(e) => setEditPhaseName(String(e.detail.value ?? ''))} /></IonItem>
                  <IonItem><IonInput label="Inicio" labelPlacement="stacked" type="date" value={editPhaseStart} onIonInput={(e) => setEditPhaseStart(String(e.detail.value ?? ''))} /></IonItem>
                  <IonItem><IonInput label="Término" labelPlacement="stacked" type="date" value={editPhaseEnd} onIonInput={(e) => setEditPhaseEnd(String(e.detail.value ?? ''))} /></IonItem>
                  <div className="button-row"><IonButton type="submit">Guardar</IonButton><IonButton color="medium" fill="outline" onClick={() => setEditingPhaseId(null)}>Cancelar</IonButton></div>
                </form>
              ) : (
                <>
                  <div className="page-header" style={{ marginBottom: 10 }}>
                    <div>
                      <h2 style={{ margin: 0 }}>{phase.name}</h2>
                      <p className="muted">{phase.plannedStart ?? 'Sin inicio'} {phase.plannedEnd ? `→ ${phase.plannedEnd}` : ''}</p>
                    </div>
                    <div className="button-row">
                      <IonBadge>{Math.round(progress * 100)}%</IonBadge>
                      <IonButton size="small" onClick={() => onTabChange(String(phase.phaseId))}>Ver board</IonButton>
                      <IonButton size="small" fill="outline" onClick={() => startEditPhase(phase)}>Editar</IonButton>
                      <IonButton size="small" color="danger" fill="outline" onClick={() => setPhaseToDelete(phase)}>Eliminar</IonButton>
                    </div>
                  </div>
                  <IonProgressBar value={progress} />

                  <div className="ion-margin-top">
                    {phaseTasks.length === 0 ? <p className="muted">Sin tareas.</p> : phaseTasks.map((task) => (
                      <IonItem key={task.taskId} button onClick={() => setSelectedTask(task)}>
                        <IonLabel>
                          <h2>{task.taskCode ? `${task.taskCode} · ` : ''}{task.title}</h2>
                          <p>{formatStatus(task.status)} {task.priority ? `· ${task.priority}` : ''}</p>
                        </IonLabel>
                        <IonButton slot="end" fill="clear" color="danger" onClick={(event) => { event.stopPropagation(); setTaskToDelete(task); }}>Eliminar</IonButton>
                      </IonItem>
                    ))}
                  </div>

                  {addingTaskToPhase === phase.phaseId ? (
                    <form onSubmit={(event) => handleQuickAddTask(event, phase.phaseId)} className="inline-row ion-margin-top">
                      <IonInput fill="outline" placeholder="Nueva tarea" value={newTaskTitle} onIonInput={(e) => setNewTaskTitle(String(e.detail.value ?? ''))} />
                      <IonButton type="submit">Crear</IonButton>
                      <IonButton fill="outline" color="medium" onClick={() => setAddingTaskToPhase(null)}>Cancelar</IonButton>
                    </form>
                  ) : (
                    <IonButton className="ion-margin-top" fill="clear" onClick={() => setAddingTaskToPhase(phase.phaseId)}>+ Agregar tarea</IonButton>
                  )}
                </>
              )}
            </IonCardContent>
          </IonCard>
        );
      })}

      {selectedTask && <TaskModal task={selectedTask} projectId={projectId} onClose={() => setSelectedTask(null)} onUpdate={() => { onReload(); setSelectedTask(null); }} onDelete={() => { onReload(); setSelectedTask(null); }} />}
      {phaseToDelete && <ConfirmModal title={`Eliminar fase "${phaseToDelete.name}"`} message="Esta acción eliminará la fase y sus tareas." confirmLabel="Eliminar" danger onConfirm={confirmDeletePhase} onCancel={() => setPhaseToDelete(null)} />}
      {taskToDelete && <ConfirmModal title={`Eliminar tarea "${taskToDelete.title}"`} message="Esta acción eliminará la tarea permanentemente." confirmLabel="Eliminar" danger onConfirm={confirmDeleteTask} onCancel={() => setTaskToDelete(null)} />}
    </>
  );
}
