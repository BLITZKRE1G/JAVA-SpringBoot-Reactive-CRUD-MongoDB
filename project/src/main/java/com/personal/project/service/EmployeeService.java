package com.personal.project.service;

import com.personal.project.model.EmployeeFilter;
import com.personal.project.model.PaginatedEmployee;
import com.personal.project.repository.EmployeeFilterRepository;
import com.personal.project.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;

import com.personal.project.model.Employee;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    EmployeeRepository repository;

    @Autowired
    EmployeeFilterRepository employeeFilterRepository;

    public Mono<Employee> getEmployeeById(String _id) {
        return repository.findById(_id);
    }

    public Flux<Employee> getAllEmployees() {
        return repository.findAll().flatMap(employee -> {
            log.info("Found: " + employee);
            return Flux.just(employee);
        });
    }

    public Mono<Employee> saveEmployeeToDatabase(Employee employee) {
        return repository.save(employee);
    }

    public Mono<Employee> updateEmployeeDetails(Employee employee) {
        return repository.findById(employee.get_id())
                .switchIfEmpty(Mono.defer(() -> {
                    throw new RuntimeException("Employee Doesn't Exist in the Database. Create a New Entry");
                })).flatMap(employeeDetails -> {
                    log.info("Updated: " + employeeDetails + " to " + employee);
                    return repository.save(employee);
                });
    }

    public Mono<String> deleteEmployeeFromDatabase(String _id) {
        return repository.findById(_id).switchIfEmpty(Mono.defer(() -> {
            log.info("Employee Not in Database!");
            throw new RuntimeException("Employee Not in Database!");
        })).flatMap(employee -> {
            repository.delete(employee);
            return Mono.just("Deleted: " + employee);
        });
    }

    public Flux<Employee> getEmployeesByLocation(String location) {
        return repository.findByLocation(location).switchIfEmpty(Mono.defer(() -> {
            log.info("Employees DON'T exist from this Location.");
            throw new RuntimeException("Employees DON'T exist from this Location.");
        }));
    }

    public Flux<Employee> getEmployeesByRole(String role) {
        return repository.findByRole(role).switchIfEmpty(Mono.defer(() -> {
            log.info("Employees DON'T exist for this Role.");
            throw new RuntimeException("Employees DON'T exist for this Role.");
        }));
    }

    public Flux<Employee> getEmployeesByDepartment(String department){
        return repository.findByDept(department)
                .switchIfEmpty(Mono.defer(()->{
                    log.info("Employees DON'T Exist for this Role!");
                    throw new RuntimeException("Employees DON'T Exist for this Role!");
                }));
    }

    public Flux<Employee> getByEmploymentStatus(boolean employed) {
        return repository.findByEmployed(employed);
    }

    public Mono<PaginatedEmployee> findEmployeesByPagination(EmployeeFilter employeeFilter, String _id) {
        if (_id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Should NOT be NULL!");
        }
        if (employeeFilter.getPage() == null || employeeFilter.getPage() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should NOT be NULL or LESS than 1!");
        }
        if (employeeFilter.getCount() == null || employeeFilter.getCount() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Count Should be GREATER than 10!");
        }

        Pageable page = PageRequest.of(employeeFilter.getPage() - 1, employeeFilter.getCount(), Sort.by("salary"));

        if (employeeFilter.getSortValue() != null && employeeFilter.getSortOrder() != null) {
            page = PageRequest.of(employeeFilter.getPage() - 1, employeeFilter.getCount(),
                    Sort.by(getSortingOrders(employeeFilter.getSortValue(), employeeFilter.getSortOrder())));
        }

        return employeeFilterRepository.filterEmployee(employeeFilter, _id, page);
    }

    public List<Order> getSortingOrders(List<String> values, List<Direction> orders) {
        List<Order> sorts = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            sorts.add(new Order(orders.get(i), values.get(i)));
        }

        return sorts;
    }
}
