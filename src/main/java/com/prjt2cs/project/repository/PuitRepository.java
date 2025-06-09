package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Puit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuitRepository extends JpaRepository<Puit, String> {
    // Additional custom methods if needed
}