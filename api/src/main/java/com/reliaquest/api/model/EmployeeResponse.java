package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeResponse {
    private String id;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private int employeeSalary;

    @JsonProperty("employee_age")
    private int employeeAge;

    @JsonProperty("employee_title")
    private String employeeTitle;

    @JsonProperty("employee_email")
    private String employeeEmail;
}
