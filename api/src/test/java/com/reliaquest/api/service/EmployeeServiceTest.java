package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private ApiClient apiClient;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeResponse employee1;
    private EmployeeResponse employee2;
    private CreateEmployeeInput createEmployeeInput;

    @BeforeEach
    public void setup() {
        employee1 = new EmployeeResponse("1", "Abhay K", 50000, 30, "SE", "john@company.com");
        employee2 = new EmployeeResponse("2", "Jane Doe", 60000, 30, "SE", "jane@company.com");
        createEmployeeInput = new CreateEmployeeInput("New Employee", 70000, 30, "SSE");
    }

    @Test
    public void testGetAllEmployees() {
        List<EmployeeResponse> employees = Arrays.asList(employee1, employee2);
        when(apiClient.getAllEmployees()).thenReturn(employees);

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Abhay K");
        assertThat(result.get(1).getEmployeeName()).isEqualTo("Jane Doe");
    }

    @Test
    public void testGetEmployeesByNameSearch() {
        List<EmployeeResponse> employees = Arrays.asList(employee1, employee2);
        when(apiClient.getAllEmployees()).thenReturn(employees);

        List<EmployeeResponse> result = employeeService.getEmployeesByNameSearch("Abhay K");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Abhay K");
    }

    @Test
    public void testGetEmployeeById() {
        when(apiClient.getEmployeeById("1")).thenReturn(employee1);

        EmployeeResponse result = employeeService.getEmployeeById("1");

        assertThat(result.getEmployeeName()).isEqualTo("Abhay K");
        assertThat(result.getEmployeeSalary()).isEqualTo(50000);
    }

    @Test
    public void testGetHighestSalaryOfEmployees() {
        List<EmployeeResponse> employees = Arrays.asList(employee1, employee2);
        when(apiClient.getAllEmployees()).thenReturn(employees);

        Integer result = employeeService.getHighestSalaryOfEmployees();

        assertThat(result).isEqualTo(60000);
    }

    @Test
    public void testGetHighestSalaryOfEmployees_noEmployees() {
        when(apiClient.getAllEmployees()).thenReturn(Arrays.asList());

        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> employeeService.getHighestSalaryOfEmployees())
                .withMessage("No employees found.");
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() {
        List<EmployeeResponse> employees = Arrays.asList(employee1, employee2);
        when(apiClient.getAllEmployees()).thenReturn(employees);

        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo("Jane Doe");
        assertThat(result.get(1)).isEqualTo("Abhay K");
    }

    @Test
    public void testCreateEmployee() {
        EmployeeResponse createdEmployee =
                new EmployeeResponse("1", "New Employee", 70000, 30, "SE", "john@company.com");
        when(apiClient.createEmployee(createEmployeeInput)).thenReturn(createdEmployee);

        EmployeeResponse result = employeeService.createEmployee(createEmployeeInput);

        assertThat(result.getEmployeeName()).isEqualTo("New Employee");
        assertThat(result.getEmployeeSalary()).isEqualTo(70000);
    }

    @Test
    public void testDeleteEmployeeById() {
        when(apiClient.deleteEmployeeById("1")).thenReturn("Employee deleted successfully");
        String result = employeeService.deleteEmployeeById("1");

        assertThat(result).isEqualTo("Employee deleted successfully");
    }

    @Test
    public void testDeleteEmployeeById_notFound() {
        when(apiClient.deleteEmployeeById("999")).thenReturn("Employee not found");
        String result = employeeService.deleteEmployeeById("999");

        assertThat(result).isEqualTo("Employee not found");
    }
}
