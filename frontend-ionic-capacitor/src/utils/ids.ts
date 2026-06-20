import type { Client, Project } from '../types/projects';
import type { Professional } from '../types/resources';

type EntityLike = Record<string, unknown>;

export function toNumericId(value: unknown): number | null {
  if (value === null || value === undefined || value === '') return null;
  const id = Number(value);
  return Number.isInteger(id) && id > 0 ? id : null;
}

export function getClientId(client: Partial<Client> & EntityLike): number | null {
  return toNumericId(client.clientId ?? client.id);
}

export function getProjectId(project: Partial<Project> & EntityLike): number | null {
  return toNumericId(project.projectId ?? project.id);
}

export function getProfessionalId(professional: Partial<Professional> & EntityLike): number | null {
  return toNumericId(professional.resourceId ?? professional.professionalId ?? professional.id);
}

export function blurActiveElement() {
  const active = document.activeElement;
  if (active instanceof HTMLElement) active.blur();
}
