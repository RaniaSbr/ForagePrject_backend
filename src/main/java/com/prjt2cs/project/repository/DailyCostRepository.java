package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.DailyCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyCostRepository extends JpaRepository<DailyCost, Long> {
    // Méthode pour trouver le DailyCost associé à un rapport spécifique
    DailyCost findByReportId(Long reportId);
}