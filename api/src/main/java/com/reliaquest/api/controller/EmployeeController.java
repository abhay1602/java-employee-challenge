package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.IEmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<EmployeeResponse, CreateEmployeeInput> {

    private final IEmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Override
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        logger.info("Fetching all employees.");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByNameSearch(String searchString) {
        logger.info("Searching for employees with name containing '{}'.", searchString);
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<EmployeeResponse> getEmployeeById(String id) {
        logger.info("Fetching employee by ID: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Fetching highest salary of employees.");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching top ten highest earning employee names.");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<EmployeeResponse> createEmployee(CreateEmployeeInput employeeInput) {
        logger.info("Creating new employee: {}", employeeInput.getName());
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        logger.info("Deleting employee with ID: {}", id);
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }
}
