package com.prjt2cs.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prjt2cs.project.model.ERole;
import com.prjt2cs.project.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByName(ERole name); // Doit retourner une List
    boolean existsByName(ERole name);
}
