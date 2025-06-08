package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

}