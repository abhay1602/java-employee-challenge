package com.reliaquest.api.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.exception.NotFoundException;
import com.reliaquest.api.exception.ServerErrorException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.ResponseWrapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@TestPropertySource(properties = "api.base-url=http://localhost:8112/api/v1/employee")
class ApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApiClient apiClient;

    @Value("${api.base-url}")
    private String baseUrl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiClient, "baseUrl", baseUrl);
    }

    @Test
    void testBaseUrl() {
        assertEquals("http://localhost:8112/api/v1/employee", baseUrl);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        EmployeeResponse employeeResponse =
                new EmployeeResponse("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        ResponseWrapper<List<EmployeeResponse>> responseWrapper =
                new ResponseWrapper<>(List.of(employeeResponse), "Success");

        String jsonResponse =
                "{\"data\":[{\"id\":\"1\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"john@company.com\"}]}";
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(jsonResponse));

        when(objectMapper.readValue(any(String.class), eq(ApiClient.LIST_EMPLOYEE_RESPONSE)))
                .thenReturn(responseWrapper);

        List<EmployeeResponse> result = apiClient.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());

        verify(objectMapper, times(1)).readValue(any(String.class), eq(ApiClient.LIST_EMPLOYEE_RESPONSE));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        EmployeeResponse employeeResponse =
                new EmployeeResponse("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        ResponseWrapper<EmployeeResponse> responseWrapper = new ResponseWrapper<>(employeeResponse, "Success");

        String jsonResponse =
                "{\"data\":{\"id\":\"1\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"john@company.com\"}}";
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(jsonResponse));

        when(objectMapper.readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE)))
                .thenReturn(responseWrapper);

        EmployeeResponse result = apiClient.getEmployeeById("1");

        assertNotNull(result);
        assertEquals("John Doe", result.getEmployeeName());

        verify(objectMapper, times(1)).readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE));
    }

    @Test
    void testCreateEmployee() throws Exception {
        CreateEmployeeInput input = new CreateEmployeeInput("John Doe", 50000, 30, "Developer");
        EmployeeResponse employeeResponse =
                new EmployeeResponse("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        ResponseWrapper<EmployeeResponse> responseWrapper = new ResponseWrapper<>(employeeResponse, "Success");

        String jsonResponse =
                "{\"data\":{\"id\":\"1\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"john@company.com\"}}";
        when(restTemplate.postForEntity(any(String.class), eq(input), eq(String.class)))
                .thenReturn(ResponseEntity.ok(jsonResponse));

        when(objectMapper.readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE)))
                .thenReturn(responseWrapper);

        EmployeeResponse result = apiClient.createEmployee(input);

        assertNotNull(result);
        assertEquals("John Doe", result.getEmployeeName());

        verify(objectMapper, times(1)).readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE));
    }

    @Test
    void testDeleteEmployeeById() throws JsonProcessingException {

        EmployeeResponse employeeResponse =
                new EmployeeResponse("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        ResponseWrapper<EmployeeResponse> responseWrapper = new ResponseWrapper<>(employeeResponse, "Success");

        String jsonResponse =
                "{\"data\":{\"id\":\"1\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"john@company.com\"}}";
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(jsonResponse));

        when(objectMapper.readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE)))
                .thenReturn(responseWrapper);

        doNothing().when(restTemplate).delete(any(String.class));

        String result = apiClient.deleteEmployeeById("1");

        assertEquals("Successfully deleted employee.", result);
    }

    @Test
    void testGetEmployeeByIdNotFoundException() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> apiClient.getEmployeeById("1"));
        assertEquals("Resource not found: 404 NOT_FOUND", exception.getMessage());
    }

    @Test
    void testCreateEmployeeValidationException() {
        CreateEmployeeInput input = new CreateEmployeeInput("Invalid Name", -5000, 30, "Developer");

        when(restTemplate.postForEntity(any(String.class), eq(input), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request"));

        ValidationException exception = assertThrows(ValidationException.class, () -> apiClient.createEmployee(input));
        assertEquals("Invalid request: 400 Invalid request", exception.getMessage());
    }

    @Test
    void testDeleteEmployeeByIdNotFound() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> apiClient.deleteEmployeeById("1"));
        assertEquals("Resource not found: 404 NOT_FOUND", exception.getMessage());
    }

    @Test
    void testDeleteEmployeeByIdServerError() throws JsonProcessingException {
        EmployeeResponse employeeResponse =
                new EmployeeResponse("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        ResponseWrapper<EmployeeResponse> responseWrapper = new ResponseWrapper<>(employeeResponse, "Success");

        String jsonResponse =
                "{\"data\":{\"id\":\"1\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"john@company.com\"}}";
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(jsonResponse));

        when(objectMapper.readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE)))
                .thenReturn(responseWrapper);

        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.DELETE), any(), eq(String.class));

        ServerErrorException exception =
                assertThrows(ServerErrorException.class, () -> apiClient.deleteEmployeeById("1"));
        assertEquals("Server error while deleting employee: 500 INTERNAL_SERVER_ERROR", exception.getMessage());
    }

    @Test
    void testGetDataFromApiThrowsException() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenThrow(new RuntimeException("API error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> apiClient.getAllEmployees());
        assertEquals("Failed to fetch data: API error", exception.getMessage());

        verify(restTemplate, times(1)).getForEntity(any(String.class), eq(String.class));
    }

    @Test
    void testParseResponseThrowsException() throws Exception {
        String invalidJson = "{\"data\":\"invalid\"}";
        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(ResponseEntity.ok(invalidJson));

        when(objectMapper.readValue(any(String.class), eq(ApiClient.EMPLOYEE_RESPONSE)))
                .thenThrow(new RuntimeException("Error parsing"));

        ApiException exception = assertThrows(ApiException.class, () -> apiClient.getEmployeeById("1"));
        assertEquals("Failed to fetch data: Error parsing API response: Error parsing", exception.getMessage());
    }
}
