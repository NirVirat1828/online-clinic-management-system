package com.project.back_end.mappers.batch;

import com.project.back_end.DTO.*;
import com.project.back_end.mappers.*;
import com.project.back_end.models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility batch mapping helpers for lists.
 */
public final class DTOCollectors {

    private DTOCollectors() {}

    public static List<DoctorDTO> doctors(List<Doctor> doctors) {
        return doctors == null ? List.of() :
                doctors.stream().map(DoctorMapper::toDTO).collect(Collectors.toList());
    }

    public static List<PatientDTO> patients(List<Patient> patients) {
        return patients == null ? List.of() :
                patients.stream().map(PatientMapper::toDTO).collect(Collectors.toList());
    }

    public static List<AppointmentDTO> appointments(List<Appointment> appointments) {
        return appointments == null ? List.of() :
                appointments.stream().map(AppointmentMapper::toDTO).collect(Collectors.toList());
    }

    public static List<ClinicLocationDTO> clinicLocations(List<ClinicLocation> list) {
        return list == null ? List.of() :
                list.stream().map(ClinicLocationMapper::toDTO).collect(Collectors.toList());
    }
}