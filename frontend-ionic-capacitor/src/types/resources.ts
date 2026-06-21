export type ProfessionalStatus = 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE';
export type Seniority = 'JUNIOR' | 'MID' | 'SENIOR' | 'LEAD' | 'PRINCIPAL';

export interface Professional {
  resourceId: number;
  professionalId?: number;
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  employeeCode?: string;
  roleName?: string;
  seniority?: Seniority | string;
  location?: string;
  timeZone?: string;
  weeklyCapacityHours?: number;
  status: ProfessionalStatus | string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProfessionalRequest {
  firstName: string;
  lastName: string;
  email: string;
  employeeCode?: string;
  roleName?: string;
  seniority?: Seniority | string;
  location?: string;
  timeZone?: string;
  weeklyCapacityHours?: number;
  status?: ProfessionalStatus | string;
}

export interface Skill {
  skillId: number;
  name: string;
  category?: string;
  description?: string;
  isActive?: boolean;
}

export interface SkillRequest {
  name: string;
  category?: string;
  description?: string;
  isActive?: boolean;
}

export interface Assignment {
  assignmentId: number;
  resourceId: number;
  projectId: number;
  projectRole?: string;
  allocationPct?: number;
  plannedHours?: number;
  startDate?: string;
  endDate?: string;
  assignmentStatus?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AssignmentRequest {
  resourceId: number;
  projectId: number;
  projectRole?: string;
  allocationPct?: number;
  plannedHours?: number;
  startDate?: string;
  endDate?: string;
  assignmentStatus?: string;
}

export interface Availability {
  availabilityId: number;
  resourceId: number;
  dayOfWeek?: string;
  availableHours?: number;
  startTime?: string;
  endTime?: string;
  isAvailable?: boolean;
}

export interface Absence {
  absenceId: number;
  resourceId: number;
  startDate: string;
  endDate: string;
  reason?: string;
  status?: string;
}

export interface ResourceSkill {
  resourceSkillId: number;
  resourceId: number;
  skillId: number;
  level?: string;
  yearsExperience?: number;
}
