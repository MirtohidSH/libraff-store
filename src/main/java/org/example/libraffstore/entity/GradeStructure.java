package org.example.libraffstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.GradeTargetType;
import org.example.libraffstore.enums.GradeType;
import org.example.libraffstore.enums.PeriodType;

import java.math.BigDecimal;

@Entity
@Table(name = "grade_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 5, scale = 2)
    private BigDecimal bonusPercentage;

    @Column
    private BigDecimal bonusAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal minThreshold;

    @Enumerated(EnumType.STRING)
    private GradeTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeType gradeType;

    private String gradeName;
}
