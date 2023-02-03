package com.personal.project.controller;

import com.personal.project.model.Employee;
import com.personal.project.model.EmployeeFilter;
import com.personal.project.model.PaginatedEmployee;
import com.personal.project.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    @Autowired
    EmployeeService service;

    @GetMapping(value = "/fetch/{_id}")
    public Mono<Employee> findEmployee(@PathVariable String _id) {
        return service.getEmployeeById(_id);
    }

    @PostMapping(value = "/save")
    public Mono<Employee> saveEmployee(@RequestBody Employee employee) {
        return service.saveEmployeeToDatabase(employee);
    }

    @PutMapping(value = "/update")
    public Mono<Employee> updateEmployee(@RequestBody Employee employee) {
        return service.updateEmployeeDetails(employee);
    }

    @DeleteMapping(value = "/delete/{_id}")
    public Mono<String> deleteEmployee(@PathVariable String _id) {
        return service.deleteEmployeeFromDatabase(_id);
    }

    @GetMapping(value = "/fetch/{role}")
    public Flux<Employee> findByRole(@PathVariable String role) {
        return service.getEmployeesByRole(role);
    }

    @GetMapping(value = "fetch/{location}")
    public Flux<Employee> findByLocation(@PathVariable String location) {
        return service.getEmployeesByLocation(location);
    }

    @GetMapping(value = "/fetch/{status}")
    public Flux<Employee> getByEmploymentStatus(@PathVariable String status){
        return service.getByEmploymentStatus(status);
    }

    @GetMapping(value = "/filter")
    public Mono<PaginatedEmployee> getEmployeesPaginated(@RequestBody String _id, EmployeeFilter employeeFilter){
        return service.getEmployeesByPagination(employeeFilter, _id);
    }
}
