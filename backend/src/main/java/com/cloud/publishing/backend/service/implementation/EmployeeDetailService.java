package com.cloud.publishing.backend.service.implementation;

import static com.cloud.publishing.common.constants.employee.EmployeeMessage.EMPLOYEE_NOT_FOUND_MSG;

import com.cloud.publishing.backend.repository.EmployeeRepository;
import com.cloud.publishing.backend.security.EmployeeDetails;
import com.cloud.publishing.model.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailService implements UserDetailsService {
    private final EmployeeRepository repository;

    @Autowired
    public EmployeeDetailService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(EMPLOYEE_NOT_FOUND_MSG));
        return new EmployeeDetails(
                employee.id(),
                employee.email(),
                employee.password(),
                employee.chiefEditor(),
                employee.type()
        );
    }
}