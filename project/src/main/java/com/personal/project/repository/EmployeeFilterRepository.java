package com.personal.project.repository;

import com.personal.project.model.Employee;
import com.personal.project.model.EmployeeFilter;
import com.personal.project.model.PaginatedEmployee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class EmployeeFilterRepository {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<PaginatedEmployee> filterEmployee(EmployeeFilter employeeFilter, String _id, Pageable page) {
        Query query = new Query();
        log.info("Template: " + reactiveMongoTemplate.getMongoDatabase());

        Criteria employeeCriteria = Criteria.where("id").in(_id);
        query.addCriteria(employeeCriteria);

        if (employeeFilter.getDept() != null && !employeeFilter.getDept().isEmpty()) {
            Criteria deptCriteria = Criteria.where("dept").in(employeeFilter.getDept());
            query.addCriteria(deptCriteria);
        }

        if (employeeFilter.getRole() != null && !employeeFilter.getRole().isEmpty()) {
            Criteria roleCriteria = Criteria.where("role").in(employeeFilter.getRole());
            query.addCriteria(roleCriteria);
        }

        if (employeeFilter.isEmployed()) {
            Criteria employementStatusCriteria = Criteria.where("employmentStatus")
                    .in(employeeFilter.isEmployed());
            query.addCriteria(employementStatusCriteria);
        }

        if (employeeFilter.getLocation() != null && !employeeFilter.getLocation().isEmpty()) {
            Criteria locationCriteria = Criteria.where("location").in(employeeFilter.getLocation());
            query.addCriteria(locationCriteria);
        }

        if (employeeFilter.getSalary() != null && !employeeFilter.getSalary().toString().isEmpty()) {
            Criteria salaryCriteria = Criteria.where("salary").in(employeeFilter.getSalary());
            query.addCriteria(salaryCriteria);
        }

        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary().excludeCase()));

        log.info(query.toString());

        return reactiveMongoTemplate.find(query, Employee.class).count().flatMap(count -> {
            PaginatedEmployee paginatedEmployee = new PaginatedEmployee();
            paginatedEmployee.setTotalCount(count);

            if (page != null) {
                return reactiveMongoTemplate.find(query.with(page), Employee.class).collectList().flatMap(employees -> {
                    paginatedEmployee.setEmployees(employees);
                    return Mono.just(paginatedEmployee);
                });
            }
            return reactiveMongoTemplate.find(query, Employee.class).collectList().flatMap(employees -> {
                paginatedEmployee.setEmployees(employees);
                return Mono.just(paginatedEmployee);
            });
        });
    }
}
