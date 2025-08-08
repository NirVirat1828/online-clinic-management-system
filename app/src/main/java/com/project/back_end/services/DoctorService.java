package com.project.back_end.services;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.mappers.DoctorMapper;
import com.project.back_end.models.ClinicLocation;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.ClinicLocationRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Business logic for doctors (search, activation, assignment).
 */
@Service
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ClinicLocationRepository clinicLocationRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         ClinicLocationRepository clinicLocationRepository) {
        this.doctorRepository = doctorRepository;
        this.clinicLocationRepository = clinicLocationRepository;
    }

    public DoctorDTO getDoctor(Long id) {
        return doctorRepository.findById(id)
                .map(DoctorMapper::toDTO)
                .orElse(null);
    }

    public List<DoctorDTO> listAll() {
        return doctorRepository.findAll().stream()
                .map(DoctorMapper::toDTO)
                .collect(toList());
    }

    public List<DoctorDTO> search(String name, String specialty, Boolean activeOnly) {
        if (name == null && specialty == null && activeOnly == null) {
            return listAll();
        }
        List<Doctor> found = doctorRepository.flexibleSearch(
                name != null ? name.trim() : null,
                specialty != null ? specialty.trim() : null,
                activeOnly != null && activeOnly
        );
        return found.stream().map(DoctorMapper::toDTO).collect(toList());
    }

    @Transactional
    public DoctorDTO toggleActive(Long doctorId, boolean active) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        doctor.setActive(active);
        return DoctorMapper.toDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDTO assignClinic(Long doctorId, Long clinicLocationId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        ClinicLocation location = clinicLocationRepository.findById(clinicLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic location not found"));
        doctor.setClinicLocation(location);
        return DoctorMapper.toDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDTO createDoctor(Doctor doctor) {
        if (doctor.getEmail() != null && doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (doctor.getPhone() != null && doctorRepository.existsByPhone(doctor.getPhone())) {
            throw new IllegalArgumentException("Phone already in use");
        }
        doctor.setActive(true);
        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toDTO(saved);
    }
}