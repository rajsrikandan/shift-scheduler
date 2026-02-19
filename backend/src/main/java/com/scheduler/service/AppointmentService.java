package com.scheduler.service;

import com.scheduler.dto.AppointmentRequest;
import com.scheduler.dto.AppointmentResponse;
import com.scheduler.entity.Appointment;
import com.scheduler.exception.ResourceNotFoundException;
import com.scheduler.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {

    private static final Set<Integer> VALID_DURATIONS = Set.of(15, 30, 45, 60, 90, 120);

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public AppointmentResponse create(AppointmentRequest request) {
        validateDuration(request.getDuration());
        Appointment entity = toEntity(request);
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll(LocalDate date) {
        List<Appointment> results = (date != null)
                ? repository.findByDateOrderByStartTimeAsc(date)
                : repository.findAllByOrderByDateAscStartTimeAsc();
        return results.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(UUID id) {
        return toResponse(getOrThrow(id));
    }

    public AppointmentResponse update(UUID id, AppointmentRequest request) {
        validateDuration(request.getDuration());
        Appointment entity = getOrThrow(id);
        entity.setTitle(request.getTitle());
        entity.setDate(request.getDate());
        entity.setStartTime(request.getStartTime());
        entity.setDuration(request.getDuration());
        entity.setCategory(request.getCategory());
        entity.setNotes(request.getNotes() != null ? request.getNotes() : "");
        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private Appointment getOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private void validateDuration(Integer duration) {
        if (duration == null || !VALID_DURATIONS.contains(duration)) {
            throw new IllegalArgumentException(
                "Duration must be one of: 15, 30, 45, 60, 90, 120 minutes");
        }
    }

    private Appointment toEntity(AppointmentRequest r) {
        Appointment a = new Appointment();
        a.setTitle(r.getTitle());
        a.setDate(r.getDate());
        a.setStartTime(r.getStartTime());
        a.setDuration(r.getDuration());
        a.setCategory(r.getCategory());
        a.setNotes(r.getNotes() != null ? r.getNotes() : "");
        return a;
    }

    private AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse res = new AppointmentResponse();
        res.setId(a.getId());
        res.setCreatedAt(a.getCreatedAt());
        res.setUpdatedAt(a.getUpdatedAt());
        res.setTitle(a.getTitle());
        res.setDate(a.getDate());
        res.setStartTime(a.getStartTime());
        res.setDuration(a.getDuration());
        res.setCategory(a.getCategory().name());
        res.setNotes(a.getNotes());
        return res;
    }
}
