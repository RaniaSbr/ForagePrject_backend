package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    // Vous pouvez ajouter des méthodes personnalisées si nécessaire
}