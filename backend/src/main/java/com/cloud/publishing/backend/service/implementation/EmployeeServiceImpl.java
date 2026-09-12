package com.cloud.publishing.backend.service.implementation;

import static com.cloud.publishing.common.constants.employee.EmployeeMessage.*;

import com.cloud.publishing.backend.service.EmployeeService;
import com.cloud.publishing.common.dto.request.EmployeeRequest;
import com.cloud.publishing.common.dto.request.EmployeeUpdateRequest;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.backend.exception.InvalidArgumentException;
import com.cloud.publishing.backend.exception.ObjectNotFoundException;
import com.cloud.publishing.backend.repository.EducationRepository;
import com.cloud.publishing.model.employee.Education;
import com.cloud.publishing.model.employee.Employee;
import java.util.List;
import com.cloud.publishing.backend.mapper.EmployeeMapper;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cloud.publishing.backend.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EducationRepository educationRepository;
    private final EmployeeMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            EducationRepository educationRepository,
            EmployeeMapper mapper,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.educationRepository = educationRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Employee add(EmployeeRequest request) {
        if (request.chiefEditor()) {
            employeeRepository.resetChiefEditor();
        }
        Education education = educationRepository.get(request.educationId());
        Employee employee = mapper.toEntity(request, education);
        String password = passwordEncoder.encode(request.password());
        employee = Employee.withPassword(employee, password);
        return employeeRepository.add(employee);
    }

    @Override
    @Transactional
    public Employee update(int id, EmployeeUpdateRequest request) {
        Employee current = employeeRepository.get(id);
        if (request.chiefEditor() && !current.chiefEditor()) {
            employeeRepository.resetChiefEditor();
        }
        boolean hasPassword = request.password() != null && !request.password().isEmpty();
        if (hasPassword) {
            if (request.passwordConfirm() == null || !request.password()
                    .equals(request.passwordConfirm())) {
                throw new InvalidArgumentException(PASSWORD_MISMATCH_OR_CONFIRMATION_MISSING_MSG);
            }
        }
        Education education = educationRepository.get(request.educationId());
        Employee employee = mapper.toEntity(id, request, education);
        if (hasPassword) {
            String password = passwordEncoder.encode(request.password());
            employee = Employee.withPassword(employee, password);
        }
        employeeRepository.update(employee);
        return employee;
    }

    @Override
    public Employee get(int id) {
        return employeeRepository.get(id);
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.getAll();
    }

    @Override
    public void delete(int id) {
        if (employeeRepository.exists(id)) {
            employeeRepository.delete(id);
        } else {
            throw new ObjectNotFoundException(EMPLOYEE_NOT_FOUND_MSG);
        }
    }

    @Override
    public List<EmployeeShort> getByIds(Set<Integer> ids) {
        return employeeRepository.findByIds(ids);
    }

    @Override
    public List<EmployeeShort> getJournalists(Set<Integer> journalistIds) {
        return employeeRepository.findCoAuthors(journalistIds);
    }
}