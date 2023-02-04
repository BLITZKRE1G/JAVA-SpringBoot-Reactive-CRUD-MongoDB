package com.personal.project.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.personal.project.model.Employee;
import reactor.core.publisher.Flux;

@Repository
public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
    Flux<Employee> findByLocation(String location);

    Flux<Employee> findByRole(String role);

    Flux<Employee> findByDept(String department);

    Flux<Employee> findByEmployed(boolean employed);
}
