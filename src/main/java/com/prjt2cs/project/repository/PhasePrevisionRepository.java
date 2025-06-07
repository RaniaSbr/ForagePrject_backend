package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.PhasePrevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhasePrevisionRepository extends JpaRepository<PhasePrevision, String> {
}