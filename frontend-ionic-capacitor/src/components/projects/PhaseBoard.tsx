import type * as React from 'react';
import { useCallback, useEffect, useState } from 'react';
import {
  IonBadge,
  IonButton,
  IonCard,
  IonCardContent,
  IonInput,
  IonItem,
  IonSelect,
  IonSelectOption,
  IonText,
} from '@ionic/react';
import type { BoardColumn, Phase, Task, TaskStatus } from '../../types/projects';
import { columnService, taskService } from '../../api/projectService';
import { ConfirmModal } from '../shared/ConfirmModal';
import { TaskModal } from './TaskModal';
import { formatStatus } from '../../utils/formatStatus';

interface PhaseBoardProps {
  phase: Phase;
  tasks: Task[];
  projectId: number;
  onTasksChange: () => void;
}

const prioritiesColor: Record<string, string> = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger', CRITICAL: 'danger' };
const taskStatuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE', 'CANCELLED'];

export function PhaseBoard({ phase, tasks, projectId, onTasksChange }: PhaseBoardProps) {
  const [columns, setColumns] = useState<BoardColumn[]>([]);
  const [draggingId, setDraggingId] = useState<number | null>(null);
  const [dragOverCol, setDragOverCol] = useState<number | null>(null);
  const [addingToColId, setAddingToColId] = useState<number | null>(null);
  const [quickTitle, setQuickTitle] = useState('');
  const [showColForm, setShowColForm] = useState(false);
  const [colName, setColName] = useState('');
  const [colColor, setColColor] = useState('#6366f1');
  const [colStatus, setColStatus] = useState('');
  const [editingColId, setEditingColId] = useState<number | null>(null);
  const [editName, setEditName] = useState('');
  const [editColor, setEditColor] = useState('');
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [columnToDelete, setColumnToDelete] = useState<BoardColumn | null>(null);
  const [dropError, setDropError] = useState<string | null>(null);

  const loadColumns = useCallback(async () => {
    const data = await columnService.getByPhase(phase.phaseId).catch(() => [] as BoardColumn[]);
    setColumns(data);
  }, [phase.phaseId]);

  useEffect(() => { loadColumns(); }, [loadColumns]);

  const getColTasks = (col: BoardColumn) => col.mappedStatus ? tasks.filter((task) => task.status === col.mappedStatus) : [];

  const handleDrop = async (event: React.DragEvent, col: BoardColumn) => {
    event.preventDefault();
    setDragOverCol(null);
    const id = draggingId ?? Number(event.dataTransfer.getData('text/plain'));
    setDraggingId(null);
    if (!id) return;
    if (!col.mappedStatus) {
      setDropError(`La columna "${col.name}" no tiene un estado asociado. Edítala para asignarle uno.`);
      return;
    }
    try {
      await taskService.updateStatus(id, col.mappedStatus as TaskStatus);
      onTasksChange();
    } catch (err) {
      setDropError(err instanceof Error ? err.message : 'No se pudo mover la tarea.');
    }
  };

  const handleQuickAdd = async (event: React.FormEvent, col: BoardColumn) => {
    event.preventDefault();
    if (!quickTitle.trim()) return;
    await taskService.create(projectId, { title: quickTitle.trim(), priority: null, status: (col.mappedStatus as TaskStatus) ?? 'TODO', phaseId: phase.phaseId } as any);
    setQuickTitle('');
    setAddingToColId(null);
    onTasksChange();
  };

  const handleAddColumn = async (event: React.FormEvent) => {
    event.preventDefault();
    await columnService.create(phase.phaseId, { name: colName, color: colColor, mappedStatus: colStatus || undefined });
    setColName('');
    setColStatus('');
    setColColor('#6366f1');
    setShowColForm(false);
    loadColumns();
  };

  const startEdit = (col: BoardColumn) => {
    setEditingColId(col.columnId);
    setEditName(col.name);
    setEditColor(col.color);
  };

  const handleEditColumn = async (event: React.FormEvent, col: BoardColumn) => {
    event.preventDefault();
    await columnService.update(phase.phaseId, col.columnId, { name: editName, color: editColor });
    setEditingColId(null);
    loadColumns();
  };

  const deleteColumn = async () => {
    if (!columnToDelete) return;
    await columnService.delete(phase.phaseId, columnToDelete.columnId).catch(() => undefined);
    setColumnToDelete(null);
    loadColumns();
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h2 className="page-title">{phase.name}</h2>
          <p className="page-subtitle">{tasks.length} tareas · {tasks.filter((task) => task.status === 'DONE').length} terminadas</p>
        </div>
        <IonButton onClick={() => setShowColForm((value) => !value)}>{showColForm ? 'Cancelar' : 'Agregar columna'}</IonButton>
      </div>

      {showColForm && (
        <IonCard className="app-card">
          <IonCardContent>
            <form onSubmit={handleAddColumn} className="form-grid">
              <IonItem><IonInput label="Nombre" labelPlacement="stacked" value={colName} required onIonInput={(e) => setColName(String(e.detail.value ?? ''))} /></IonItem>
              <IonItem><IonInput label="Color" labelPlacement="stacked" type="text" value={colColor} onIonInput={(e) => setColColor(String(e.detail.value ?? '#6366f1'))} /></IonItem>
              <IonItem>
                <IonSelect label="Estado asociado" labelPlacement="stacked" value={colStatus} onIonChange={(e) => setColStatus(String(e.detail.value ?? ''))}>
                  <IonSelectOption value="">Sin asociación</IonSelectOption>
                  {taskStatuses.map((status) => <IonSelectOption key={status} value={status}>{formatStatus(status)}</IonSelectOption>)}
                </IonSelect>
              </IonItem>
              <IonButton type="submit">Guardar columna</IonButton>
            </form>
          </IonCardContent>
        </IonCard>
      )}

      {dropError && (
        <IonText color="danger">
          <p className="ion-padding-horizontal">{dropError}</p>
        </IonText>
      )}

      <div className="kanban-board">
        {columns.map((col) => {
          const colTasks = getColTasks(col);
          return (
            <div
              key={col.columnId}
              className={`kanban-column ${dragOverCol === col.columnId ? 'over' : ''}`}
              onDragOver={(event) => { event.preventDefault(); setDragOverCol(col.columnId); }}
              onDragLeave={(event) => { if (!event.currentTarget.contains(event.relatedTarget as Node)) setDragOverCol(null); }}
              onDrop={(event) => handleDrop(event, col)}
            >
              {editingColId === col.columnId ? (
                <form onSubmit={(event) => handleEditColumn(event, col)} className="inline-row">
                  <IonInput fill="outline" value={editName} onIonInput={(e) => setEditName(String(e.detail.value ?? ''))} />
                  <IonInput fill="outline" type="text" value={editColor} onIonInput={(e) => setEditColor(String(e.detail.value ?? col.color))} />
                  <IonButton type="submit" size="small">OK</IonButton>
                </form>
              ) : (
                <div className="kanban-col-header">
                  <strong className="kanban-col-name"><span className="kanban-col-dot" style={{ color: col.color }}>●</span> {col.name}</strong>
                  <div className="inline-row">
                    <IonBadge>{colTasks.length}</IonBadge>
                    <IonButton fill="clear" size="small" onClick={() => startEdit(col)}>Editar</IonButton>
                    <IonButton fill="clear" size="small" color="danger" onClick={() => setColumnToDelete(col)}>Eliminar</IonButton>
                  </div>
                </div>
              )}

              {colTasks.map((task) => (
                <div
                  key={task.taskId}
                  className={`kanban-card ${draggingId === task.taskId ? 'dragging' : ''}`}
                  draggable
                  onDragStart={(e) => { e.dataTransfer.setData('text/plain', String(task.taskId)); setDraggingId(task.taskId); }}
                  onDragEnd={() => { setDraggingId(null); setDragOverCol(null); }}
                  onDragOver={(e) => e.preventDefault()}
                  onClick={() => setSelectedTask(task)}
                  role="button"
                  tabIndex={0}
                  onKeyDown={(e) => e.key === 'Enter' && setSelectedTask(task)}
                >
                  {task.taskCode && <p className="kanban-card-code">{task.taskCode}</p>}
                  <p className="kanban-card-title">{task.title}</p>
                  {task.description && <p className="kanban-card-desc">{task.description}</p>}
                  {task.priority && (
                    <div className="kanban-card-footer">
                      <IonBadge color={prioritiesColor[task.priority] ?? 'medium'}>{formatStatus(task.priority)}</IonBadge>
                    </div>
                  )}
                </div>
              ))}

              {addingToColId === col.columnId ? (
                <form onSubmit={(event) => handleQuickAdd(event, col)} className="inline-row">
                  <IonInput fill="outline" placeholder="Nueva tarea" value={quickTitle} onIonInput={(e) => setQuickTitle(String(e.detail.value ?? ''))} />
                  <IonButton type="submit" size="small">Crear</IonButton>
                </form>
              ) : (
                <IonButton expand="block" fill="clear" onClick={() => setAddingToColId(col.columnId)}>+ Crear tarea</IonButton>
              )}
            </div>
          );
        })}

        {columns.length === 0 && (
          <IonCard className="app-card" style={{ minWidth: 280 }}>
            <IonCardContent>
              <p className="muted">No hay columnas configuradas para esta fase.</p>
              <IonButton onClick={() => setShowColForm(true)}>Agregar primera columna</IonButton>
            </IonCardContent>
          </IonCard>
        )}
      </div>

      {selectedTask && <TaskModal task={selectedTask} projectId={projectId} onClose={() => setSelectedTask(null)} onUpdate={() => { onTasksChange(); setSelectedTask(null); }} onDelete={() => { onTasksChange(); setSelectedTask(null); }} />}
      {columnToDelete && <ConfirmModal title={`Eliminar columna "${columnToDelete.name}"`} message="Las tareas no se eliminarán, solo se quitará la columna." confirmLabel="Eliminar" danger onConfirm={deleteColumn} onCancel={() => setColumnToDelete(null)} />}
    </div>
  );
}
