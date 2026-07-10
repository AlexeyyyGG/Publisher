package com.cloud.publishing.backend.repository;

import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.model.employee.Employee;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends IRepository<Employee, Integer> {
    List<Employee> getAll();

    List<EmployeeShort> findByIds(Set<Integer> ids);

    Optional<Employee> findByEmail(String email);

    void resetChiefEditor();
}