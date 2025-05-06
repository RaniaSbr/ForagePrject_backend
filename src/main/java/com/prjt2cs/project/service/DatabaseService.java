package com.prjt2cs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getTables() {
        // Query to get tables from the user schema
        String sql = "SELECT table_name FROM user_tables";
        return jdbcTemplate.queryForList(sql);
    }
}
