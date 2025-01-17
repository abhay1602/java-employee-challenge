package com.reliaquest.api.service;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final ApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        logger.info("Fetching all employees from API.");
        return apiClient.getAllEmployees();
    }

    @Override
    public List<EmployeeResponse> getEmployeesByNameSearch(String searchString) {
        logger.info("Filtering employees by name containing '{}'.", searchString);
        return apiClient.getAllEmployees().stream()
                .filter(e -> e.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse getEmployeeById(String id) {
        logger.info("Fetching employee by ID: {}", id);
        return apiClient.getEmployeeById(id);
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        logger.info("Fetching highest salary of employees.");
        return apiClient.getAllEmployees().stream()
                .mapToInt(EmployeeResponse::getEmployeeSalary)
                .max()
                .orElseThrow(() -> new ApiException("No employees found."));
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching top 10 highest earning employees.");
        return apiClient.getAllEmployees().stream()
                .sorted(Comparator.comparingInt(EmployeeResponse::getEmployeeSalary)
                        .reversed())
                .limit(10)
                .map(EmployeeResponse::getEmployeeName)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeInput input) {
        logger.info("Creating a new employee with name '{}'.", input.getName());
        return apiClient.createEmployee(input);
    }

    @Override
    public String deleteEmployeeById(String id) {
        logger.info("Deleting employee with ID: {}", id);
        return apiClient.deleteEmployeeById(id);
    }
}
