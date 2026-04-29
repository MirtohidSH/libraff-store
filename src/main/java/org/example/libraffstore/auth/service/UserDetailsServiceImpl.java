package org.example.libraffstore.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.employee.entity.Employee;
import org.example.libraffstore.employee.repository.EmployeeRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String fin) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByFIN(fin)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Employee not found with FIN: " + fin));

        if (Boolean.FALSE.equals(employee.getIsActive())) {
            throw new DisabledException("Employee is inactive");
        }

        return new User(
                employee.getFIN(),
                employee.getPassword(),
                mapRolesToAuthorities(employee));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Employee employee) {
        return employee.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}