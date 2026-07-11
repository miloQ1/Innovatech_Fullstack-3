import type { Project } from '../../types/projects';
import { getProjectId } from '../../utils/ids';
import { ProjectCard } from './ProjectCard';

interface ProjectListProps {
  projects: Project[];
}

export function ProjectList({ projects }: ProjectListProps) {
  return <div className="section-bg card-grid">{projects.map((project) => <ProjectCard key={getProjectId(project as Project & Record<string, unknown>) ?? project.code} project={project} />)}</div>;
}
