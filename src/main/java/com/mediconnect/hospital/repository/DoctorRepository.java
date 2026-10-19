package com.mediconnect.hospital.repository;

import com.mediconnect.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByActiveTrue();
    
    Optional<Doctor> findByUserId(Long userId);

    @Query("SELECT d FROM Doctor d WHERE d.active = true AND " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.department.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Doctor> searchDoctors(@Param("query") String query);

    List<Doctor> findByDepartmentIdAndActiveTrue(Long departmentId);
    
    List<Doctor> findBySpecializationContainingIgnoreCaseAndActiveTrue(String specialization);
}
