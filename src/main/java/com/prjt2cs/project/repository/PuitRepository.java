package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Puit;
import com.prjt2cs.project.model.Report;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PuitRepository extends JpaRepository<Puit, String> {

    // Correct query using the 'date' field from your Report entity
    @Query("SELECT r FROM Report r WHERE r.puit.puitId = :puitId ORDER BY r.date DESC LIMIT 1")
    Optional<Report> findLatestReportByPuitId(@Param("puitId") String puitId);

    // Alternative: Using method name query (no @Query annotation needed)
    // Optional<Report> findFirstByPuitPuitIdOrderByDateDesc(String puitId);

}