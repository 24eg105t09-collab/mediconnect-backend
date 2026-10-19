package com.mediconnect.hospital.repository;

import com.mediconnect.hospital.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctorIdAndAvailableTrue(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekIgnoreCaseAndAvailableTrue(Long doctorId, String dayOfWeek);

    List<DoctorSchedule> findByDoctorIdAndSpecificDateAndAvailableTrue(Long doctorId, LocalDate specificDate);

    List<DoctorSchedule> findByDayOfWeekIgnoreCaseAndAvailableTrue(String dayOfWeek);
}
