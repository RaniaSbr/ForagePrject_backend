package com.prjt2cs.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prjt2cs.project.service.DatabaseService;

import java.util.List;
import java.util.Map;

@RestController
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/tables")
    public List<Map<String, Object>> getTables() {
        return databaseService.getTables();
    }
}
