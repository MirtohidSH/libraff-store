package org.example.libraffstore.service;

import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.RoleEntity;
import org.example.libraffstore.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String fin) throws UsernameNotFoundException {
        // 1. Search by FIN
        Employee employee = employeeRepository.findByFIN(fin)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with FIN: " + fin));

        // 2. Map it to Spring Security's User object.
        // We pass the FIN into the first parameter (which Spring calls 'username')
        return new org.springframework.security.core.userdetails.User(employee.getFIN(), employee.getPassword(),
                mapRolesToAuthorities(employee.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<RoleEntity> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
