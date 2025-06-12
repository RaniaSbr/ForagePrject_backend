package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Puit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PuitRepository extends JpaRepository<Puit, String> {
    @Query("SELECT r FROM Rapport r WHERE r.puit.puitId = :puitId ORDER BY r.dateCreation DESC LIMIT 1")
    Optional<Rapport> findLatestRapportByPuitId(@Param("puitId") String puitId);
    // Additional custom methods if needed
}