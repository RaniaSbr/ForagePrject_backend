package com.prjt2cs.project.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.ReportRepository;

@RestController
@RequestMapping("/reports") // donc ton URL finale est : /reports/{id}/download-excel
public class ExcelController {

    private final ReportRepository reportRepository;

    public ExcelController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/{id}/download-excel")
    public ResponseEntity<byte[]> downloadExcelFile(@PathVariable Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        byte[] fileData = report.getExcelFile();
        if (fileData == null || fileData.length == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report_" + id + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }
}
