package org.example.libraffstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.GradeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "grade_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal bonusAmount;

    // Bonus hesablananda işçinin maaşı
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salaryAtTime;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_store_id")
    private GradeStore gradeStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_position_id")
    private GradePosition gradePosition;
}

