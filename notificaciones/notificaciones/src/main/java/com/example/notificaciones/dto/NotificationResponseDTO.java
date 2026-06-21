package com.example.notificaciones.dto;

import java.util.ArrayList;
import java.util.List;

public class NotificationResponseDTO {

    private String message;
    private Integer totalDispatches;
    private List<DispatchResultDTO> dispatches = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotalDispatches() {
        return totalDispatches;
    }

    public void setTotalDispatches(Integer totalDispatches) {
        this.totalDispatches = totalDispatches;
    }

    public List<DispatchResultDTO> getDispatches() {
        return dispatches;
    }

    public void setDispatches(List<DispatchResultDTO> dispatches) {
        this.dispatches = dispatches;
    }
}
