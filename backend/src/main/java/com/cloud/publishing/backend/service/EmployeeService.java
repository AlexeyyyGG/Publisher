package com.cloud.publishing.backend.service;

import com.cloud.publishing.common.dto.request.EmployeeRequest;
import com.cloud.publishing.common.dto.request.EmployeeUpdateRequest;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.model.employee.Employee;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing employees. Provides operations for creating, updating, retrieving
 * and deleting employees.
 */
public interface EmployeeService {
    /**
     * Creates a new employee.
     *
     * @param request {@link EmployeeRequest} containing employee data
     * @return created {@link Employee}
     */
    Employee add(EmployeeRequest request);

    /**
     * Updates an existing employee.
     *
     * @param id      identifier of the employee to update
     * @param request {@link EmployeeUpdateRequest} containing updated employee data
     * @return updated {@link Employee}
     */
    Employee update(int id, EmployeeUpdateRequest request);

    /**
     * Returns employee by id.
     *
     * @param id identifier of the employee
     * @return {@link Employee}
     */
    Employee get(int id);

    /**
     * Returns list of all employees.
     *
     * @return list of {@link Employee}
     */
    List<Employee> getAll();

    /**
     * Returns employees by IDs.
     *
     * @param ids employee IDs
     * @return list of {@link EmployeeShort}
     */
    List<EmployeeShort> getByIds(Set<Integer> ids);

    /**
     * Deletes employee by id.
     *
     * @param id identifier of the employee to delete
     */
    void delete(int id);
}