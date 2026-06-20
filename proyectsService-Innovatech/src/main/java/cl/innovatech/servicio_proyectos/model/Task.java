package cl.innovatech.servicio_proyectos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.innovatech.servicio_proyectos.model.enums.TaskPriority;
import cl.innovatech.servicio_proyectos.model.enums.TaskStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id")
    private Phase phase;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @JsonIgnore
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> subTasks = new ArrayList<>();

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Enumerated(EnumType.STRING)
    @Column( length = 20)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "assigned_resource_id")
    private Long assignedResourceId;

    @Column(name = "estimated_hours", precision = 7, scale = 2)
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours", precision = 7, scale = 2)
    private BigDecimal actualHours;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "task_code", length = 30)
    private String taskCode;

    

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<TaskStatusHistory> statusHistory = new ArrayList<>();


    
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Phase getPhase() { return phase; }
    public void setPhase(Phase phase) { this.phase = phase; }

    public Task getParentTask() { return parentTask; }
    public void setParentTask(Task parentTask) { this.parentTask = parentTask; }

    public List<Task> getSubTasks() { return subTasks; }
    public void setSubTasks(List<Task> subTasks) { this.subTasks = subTasks; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Long getAssignedResourceId() { return assignedResourceId; }
    public void setAssignedResourceId(Long assignedResourceId) { this.assignedResourceId = assignedResourceId; }

    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }

    public BigDecimal getActualHours() { return actualHours; }
    public void setActualHours(BigDecimal actualHours) { this.actualHours = actualHours; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getTaskCode() {
        return taskCode;
    }
    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    @JsonProperty("phaseId")
    public Long getPhaseIdForJson() {
    return phase != null ? phase.getPhaseId() : null;
    }

    @Transient
    @JsonProperty("phaseId")
    private Long inputPhaseId;

    public Long getInputPhaseId() { return inputPhaseId; }
    public void setInputPhaseId(Long inputPhaseId) { this.inputPhaseId = inputPhaseId; }

    public List<TaskStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<TaskStatusHistory> statusHistory) { this.statusHistory = statusHistory; }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
