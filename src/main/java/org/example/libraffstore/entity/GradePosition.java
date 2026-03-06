package org.example.libraffstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "GradePositions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal salesRangeMin;

    @Column(precision = 10, scale = 2)
    private BigDecimal salesRangeMax;

    @Column(precision = 5, scale = 2)
    private BigDecimal bonusPercent;

    @Column(precision = 10, scale = 2)
    private BigDecimal bonusFixedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_structure_id", nullable = false)
    private GradeStructure gradeStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}
