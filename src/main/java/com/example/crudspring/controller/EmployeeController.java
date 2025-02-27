package com.example.crudspring.controller;

import com.example.crudspring.exception.ResourceNotFoundException;
import com.example.crudspring.models.Employee;
import com.example.crudspring.repository.EmployeeRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

  @Autowired private EmployeeRepository employeeRepository;

  @GetMapping("/all")
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  @PostMapping("/create")
  public Employee createEmployee(@RequestBody Employee employee) {
    return employeeRepository.save(employee);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
    return ResponseEntity.ok(employee);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Employee> updateEmployee(
      @PathVariable Long id, @RequestBody Employee employeeDetails) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));

    employee.setFirstName(employeeDetails.getFirstName());
    employee.setLastName(employeeDetails.getLastName());
    employee.setEmailId(employeeDetails.getEmailId());

    Employee updatedEmployee = employeeRepository.save(employee);
    return ResponseEntity.ok(updatedEmployee);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));

    employeeRepository.delete(employee);
    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return ResponseEntity.ok(response);
  }
}
