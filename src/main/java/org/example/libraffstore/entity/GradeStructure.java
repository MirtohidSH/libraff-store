package org.example.libraffstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.GradeType;
import org.example.libraffstore.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "grade_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minSales;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxSales;

    @Column(precision = 5, scale = 2)
    private BigDecimal bonusPercent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeType gradeType;
}
