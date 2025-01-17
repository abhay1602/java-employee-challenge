package com.reliaquest.api.service;

import com.reliaquest.api.model.*;
import java.util.List;

public interface IEmployeeService {
    List<EmployeeResponse> getAllEmployees();

    List<EmployeeResponse> getEmployeesByNameSearch(String searchString);

    EmployeeResponse getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    EmployeeResponse createEmployee(CreateEmployeeInput input);

    String deleteEmployeeById(String id);
}
