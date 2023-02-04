package com.personal.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFilter {
    private String role, dept, location;
    private Integer salary;

    private boolean employed;

    // Pagination
    private Integer count, page;
    
    //Searching
    private String searchTerm;

    // Sorting
    private List<String> sortValue;
    private List<Sort.Direction> sortOrder;
}
