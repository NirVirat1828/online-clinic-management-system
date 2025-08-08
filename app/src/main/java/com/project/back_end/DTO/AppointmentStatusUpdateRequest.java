package com.project.back_end.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request to change the status of an appointment.
 */
public class AppointmentStatusUpdateRequest {

    @NotNull
    private Long appointmentId;

    @NotNull
    @Min(0)
    private Integer status;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public AppointmentStatusUpdateRequest setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public AppointmentStatusUpdateRequest setStatus(Integer status) {
        this.status = status;
        return this;
    }
}
