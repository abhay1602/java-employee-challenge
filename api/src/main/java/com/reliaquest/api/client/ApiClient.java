package com.reliaquest.api.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.exception.NotFoundException;
import com.reliaquest.api.exception.ServerErrorException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.ResponseWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String baseUrl;

    public static final TypeReference<ResponseWrapper<List<EmployeeResponse>>> LIST_EMPLOYEE_RESPONSE =
            new TypeReference<ResponseWrapper<List<EmployeeResponse>>>() {};

    public static final TypeReference<ResponseWrapper<EmployeeResponse>> EMPLOYEE_RESPONSE =
            new TypeReference<ResponseWrapper<EmployeeResponse>>() {};

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    public List<EmployeeResponse> getAllEmployees() {
        logger.info("Fetching all employees from API.");
        return getDataFromApi(baseUrl, LIST_EMPLOYEE_RESPONSE);
    }

    public EmployeeResponse getEmployeeById(String id) {
        logger.info("Fetching employee with ID: {}", id);
        String url = baseUrl + "/" + id;
        return getDataFromApi(url, EMPLOYEE_RESPONSE);
    }

    public EmployeeResponse createEmployee(CreateEmployeeInput input) {
        logger.info("Creating new employee: {}", input.getName());
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, input, String.class);
            return parseResponse(response, EMPLOYEE_RESPONSE);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ValidationException("Invalid request: " + e.getMessage());
            } else {
                throw new ApiException("Client error during creation: " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            throw new ServerErrorException("Server error during creation: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException("Failed to create employee: " + e.getMessage());
        }
    }

    public String deleteEmployeeById(String id) {
        logger.info("Deleting employee with ID: {}", id);
        try {
            EmployeeResponse response = getEmployeeById(id);
            if (response == null) {
                throw new NotFoundException("Employee with ID " + id + " not found.");
            }
            DeleteEmployeeInput deleteInput = new DeleteEmployeeInput();
            deleteInput.setName(response.getEmployeeName());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<DeleteEmployeeInput> requestEntity = new HttpEntity<>(deleteInput, headers);

            restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, String.class);
            return "Successfully deleted employee.";
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Employee not found: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServerErrorException("Server error while deleting employee: " + e.getMessage());
        }
    }

    private <T> T getDataFromApi(String url, TypeReference<ResponseWrapper<T>> typeRef) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseResponse(response, typeRef);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Resource not found: " + e.getStatusCode());
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ValidationException("Invalid request: " + e.getStatusCode());
            } else {
                throw new ApiException("Client error: " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            throw new ServerErrorException("Server error: " + e.getStatusCode());
        } catch (Exception e) {
            throw new ApiException("Failed to fetch data: " + e.getMessage());
        }
    }

    private <T> T parseResponse(ResponseEntity<String> response, TypeReference<ResponseWrapper<T>> typeRef) {
        try {
            ResponseWrapper<T> wrapper = objectMapper.readValue(response.getBody(), typeRef);
            return wrapper.getData(); // Extract the data from the ResponseWrapper
        } catch (Exception e) {
            throw new ApiException("Error parsing API response: " + e.getMessage());
        }
    }
}
