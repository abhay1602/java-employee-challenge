package com.reliaquest.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.IEmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private IEmployeeService employeeService;

    @Test
    public void testGetAllEmployees() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeResponse employee1 = new EmployeeResponse("1", "Abhay K", 50000, 30, "Developer", "john@company.com");
        EmployeeResponse employee2 = new EmployeeResponse("2", "Jane Doe", 50000, 30, "Developer", "john@company.com");
        List<EmployeeResponse> employees = Arrays.asList(employee1, employee2);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ResponseEntity<List<EmployeeResponse>> responseEntity = employeeController.getAllEmployees();

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).hasSize(2);
        assertThat(responseEntity.getBody().get(0).getEmployeeName()).isEqualTo("Abhay K");
        assertThat(responseEntity.getBody().get(1).getEmployeeName()).isEqualTo("Jane Doe");
    }

    @Test
    public void testGetEmployeeById() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeResponse employee = new EmployeeResponse("1", "Abhay K", 50000, 30, "Developer", "john@company.com");
        when(employeeService.getEmployeeById("1")).thenReturn(employee);

        ResponseEntity<EmployeeResponse> responseEntity = employeeController.getEmployeeById("1");

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().getEmployeeName()).isEqualTo("Abhay K");
    }

    @Test
    public void testCreateEmployee() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CreateEmployeeInput createEmployeeInput = new CreateEmployeeInput("Abhay K", 50000, 30, "SE");

        EmployeeResponse createdEmployee =
                new EmployeeResponse("1", "Abhay K", 50000, 30, "Developer", "john@company.com");
        when(employeeService.createEmployee(createEmployeeInput)).thenReturn(createdEmployee);

        ResponseEntity<EmployeeResponse> responseEntity = employeeController.createEmployee(createEmployeeInput);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().getEmployeeName()).isEqualTo("Abhay K");
    }

    @Test
    public void testDeleteEmployeeById() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(employeeService.deleteEmployeeById("1")).thenReturn("Employee deleted successfully");

        ResponseEntity<String> responseEntity = employeeController.deleteEmployeeById("1");

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo("Employee deleted successfully");
    }

    @Test
    public void testGetHighestSalaryOfEmployees() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(60000);

        ResponseEntity<Integer> responseEntity = employeeController.getHighestSalaryOfEmployees();

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo(60000);
    }
}
