
package cl.innovatech.servicio_proyectos.factory;

public class TaskStatusMessage {

    private final String label;
    private final String description;

    public TaskStatusMessage(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel()       { return label; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return label + " — " + description;
    }
}
