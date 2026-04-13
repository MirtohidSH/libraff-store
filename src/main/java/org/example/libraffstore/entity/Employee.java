package org.example.libraffstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.libraffstore.exception.AlreadyExistsException;
import org.example.libraffstore.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"store", "position", "roles"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String FIN;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    private Boolean isActive = false;
    private String email;
    private String phone;
    private BigDecimal salary;
    private LocalDate dateEmployed;
    private LocalDate dateUnemployed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    public void terminate(LocalDate dateUnemployed) {
        if (Boolean.FALSE.equals(this.isActive)) {
            throw new BusinessException("İşçi onsuz da deaktivdir.");
        }
        this.isActive = false;
        this.dateUnemployed = dateUnemployed;
    }

    public void rehire(Store store, Position position, LocalDate dateEmployed) {
        if (Boolean.TRUE.equals(this.isActive)) {
            throw new AlreadyExistsException("Bu FIN ilə aktiv işçi artıq mövcuddur: " + this.FIN);
        }
        this.store = store;
        this.position = position;
        this.isActive = true;
        this.dateEmployed = dateEmployed;
        this.dateUnemployed = null;
    }
}