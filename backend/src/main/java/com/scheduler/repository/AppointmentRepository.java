package com.scheduler.repository;

import com.scheduler.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDateOrderByStartTimeAsc(LocalDate date);

    List<Appointment> findAllByOrderByDateAscStartTimeAsc();
}
