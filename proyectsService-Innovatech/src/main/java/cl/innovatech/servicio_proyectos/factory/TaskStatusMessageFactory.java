package cl.innovatech.servicio_proyectos.factory;


import org.springframework.stereotype.Component;

@Component
public class TaskStatusMessageFactory {

    public TaskStatusMessage createMessage(String status, String taskTitle) {
        return switch (status) {
            case "TODO"        -> new TaskStatusMessage("Pendiente",   taskTitle + " está pendiente de inicio");
            case "IN_PROGRESS" -> new TaskStatusMessage("En progreso", taskTitle + " está siendo trabajada");
            case "IN_REVIEW"   -> new TaskStatusMessage("En revisión", taskTitle + " está esperando revisión");
            case "DONE"        -> new TaskStatusMessage("Completada",  taskTitle + " fue completada exitosamente");
            case "CANCELLED"   -> new TaskStatusMessage("Cancelada",   taskTitle + " fue cancelada");
            default -> throw new IllegalArgumentException("Status no soportado: " + status);
        };
    }
}