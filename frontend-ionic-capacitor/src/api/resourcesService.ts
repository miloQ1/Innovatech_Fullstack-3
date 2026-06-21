import { BACKEND_ROUTES } from '../config/backend';
import type {
  Absence,
  Assignment,
  AssignmentRequest,
  Availability,
  Professional,
  ProfessionalRequest,
  ResourceSkill,
  Skill,
  SkillRequest,
} from '../types/resources';
import { apiClient } from './apiClient';

export const professionalService = {
  getAll(): Promise<Professional[]> {
    return apiClient.get<Professional[]>(BACKEND_ROUTES.professionals, true);
  },
  getById(id: number): Promise<Professional> {
    return apiClient.get<Professional>(`${BACKEND_ROUTES.professionals}/${id}`, true);
  },
  create(data: ProfessionalRequest): Promise<Professional> {
    return apiClient.post<Professional>(BACKEND_ROUTES.professionals, data, true);
  },
  update(id: number, data: Partial<ProfessionalRequest>): Promise<Professional> {
    return apiClient.put<Professional>(`${BACKEND_ROUTES.professionals}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.professionals}/${id}`, true);
  },
  getByStatus(status: string): Promise<Professional[]> {
    return apiClient.get<Professional[]>(`${BACKEND_ROUTES.professionals}/status/${status}`, true);
  },
};

export const skillService = {
  getAll(): Promise<Skill[]> {
    return apiClient.get<Skill[]>(BACKEND_ROUTES.skills, true);
  },
  getById(id: number): Promise<Skill> {
    return apiClient.get<Skill>(`${BACKEND_ROUTES.skills}/${id}`, true);
  },
  create(data: SkillRequest): Promise<Skill> {
    return apiClient.post<Skill>(BACKEND_ROUTES.skills, data, true);
  },
  update(id: number, data: Partial<SkillRequest>): Promise<Skill> {
    return apiClient.put<Skill>(`${BACKEND_ROUTES.skills}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.skills}/${id}`, true);
  },
};

export const assignmentService = {
  getAll(): Promise<Assignment[]> {
    return apiClient.get<Assignment[]>(BACKEND_ROUTES.assignments, true);
  },
  getById(id: number): Promise<Assignment> {
    return apiClient.get<Assignment>(`${BACKEND_ROUTES.assignments}/${id}`, true);
  },
  getByProject(projectId: number): Promise<Assignment[]> {
    return apiClient.get<Assignment[]>(`${BACKEND_ROUTES.assignments}/project/${projectId}`, true);
  },
  getByResource(resourceId: number): Promise<Assignment[]> {
    return apiClient.get<Assignment[]>(`${BACKEND_ROUTES.assignments}/resource/${resourceId}`, true);
  },
  create(data: AssignmentRequest): Promise<Assignment> {
    return apiClient.post<Assignment>(BACKEND_ROUTES.assignments, data, true);
  },
  update(id: number, data: Partial<AssignmentRequest>): Promise<Assignment> {
    return apiClient.put<Assignment>(`${BACKEND_ROUTES.assignments}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.assignments}/${id}`, true);
  },
};

export const availabilityService = {
  getAll(): Promise<Availability[]> {
    return apiClient.get<Availability[]>(BACKEND_ROUTES.availability, true);
  },
  getByResource(resourceId: number): Promise<Availability[]> {
    return apiClient.get<Availability[]>(`${BACKEND_ROUTES.availability}/resource/${resourceId}`, true);
  },
  create(data: Partial<Availability>): Promise<Availability> {
    return apiClient.post<Availability>(BACKEND_ROUTES.availability, data, true);
  },
  update(id: number, data: Partial<Availability>): Promise<Availability> {
    return apiClient.put<Availability>(`${BACKEND_ROUTES.availability}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.availability}/${id}`, true);
  },
};

export const absenceService = {
  getAll(): Promise<Absence[]> {
    return apiClient.get<Absence[]>(BACKEND_ROUTES.absences, true);
  },
  getByResource(resourceId: number): Promise<Absence[]> {
    return apiClient.get<Absence[]>(`${BACKEND_ROUTES.absences}/resource/${resourceId}`, true);
  },
  create(data: Partial<Absence>): Promise<Absence> {
    return apiClient.post<Absence>(BACKEND_ROUTES.absences, data, true);
  },
  update(id: number, data: Partial<Absence>): Promise<Absence> {
    return apiClient.put<Absence>(`${BACKEND_ROUTES.absences}/${id}`, data, true);
  },
  delete(id: number): Promise<void> {
    return apiClient.delete<void>(`${BACKEND_ROUTES.absences}/${id}`, true);
  },
};

export const resourceSkillService = {
  getAll(): Promise<ResourceSkill[]> {
    return apiClient.get<ResourceSkill[]>(BACKEND_ROUTES.resourceSkills, true);
  },
  getByResource(resourceId: number): Promise<ResourceSkill[]> {
    return apiClient.get<ResourceSkill[]>(`${BACKEND_ROUTES.resourceSkills}/resource/${resourceId}`, true);
  },
};
