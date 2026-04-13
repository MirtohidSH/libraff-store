package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.repository.EmployeeRepository;
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