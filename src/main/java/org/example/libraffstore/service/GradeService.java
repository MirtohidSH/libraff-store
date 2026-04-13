package org.example.libraffstore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

import org.example.libraffstore.dto.BonusResult;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.GradeHistory;
import org.example.libraffstore.entity.GradeStructure;
import org.example.libraffstore.enums.PeriodType;
import org.example.libraffstore.repository.GradeHistoryRepository;
import org.example.libraffstore.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final GradeHistoryRepository gradeHistoryRepository;

    public BonusResult calculateTotalBonusForEmployee(Employee employee, LocalDate periodEnd, List<GradeStructure> gradeStructures) {
        if (gradeStructures == null || gradeStructures.isEmpty()) {
            return BonusResult.zero(BigDecimal.ZERO);
        }

        LocalDate[] range = resolveDateRange(gradeStructures.getFirst().getPeriodType(), periodEnd);
        return calculateBonus(employee.getId(), employee.getSalary(), true,
                range[0], range[1], gradeStructures);
    }

    public BonusResult calculateTotalBonusForStore(Employee employee, LocalDate periodEnd, List<GradeStructure> gradeStructures) {
        if (gradeStructures == null || gradeStructures.isEmpty()) {
            return BonusResult.zero(BigDecimal.ZERO);
        }

        LocalDate[] range = resolveDateRange(gradeStructures.getFirst().getPeriodType(), periodEnd);
        return calculateBonus(employee.getStore().getId(), employee.getSalary(), false,
                range[0], range[1], gradeStructures);
    }

    private LocalDate[] resolveDateRange(PeriodType periodType, LocalDate periodEnd) {
        return switch (periodType) {
            case MONTHLY -> new LocalDate[]{
                    periodEnd.with(TemporalAdjusters.firstDayOfMonth()),
                    periodEnd.with(TemporalAdjusters.lastDayOfMonth())
            };
            case ANNUAL -> {
                int seasonStartMonth = ((periodEnd.getMonthValue() - 1) / 3) * 3 + 1;
                LocalDate start = LocalDate.of(periodEnd.getYear(), seasonStartMonth, 1);
                yield new LocalDate[]{start, start.plusMonths(3).minusDays(1)};
            }
            case YEARLY -> new LocalDate[]{
                    periodEnd.with(TemporalAdjusters.firstDayOfYear()),
                    periodEnd.with(TemporalAdjusters.lastDayOfYear())
            };
        };
    }

    private BonusResult calculateBonus(Long targetId, BigDecimal employeeSalary, boolean isEmployee, LocalDate startDate,
                                       LocalDate endDate, List<GradeStructure> gradeStructures) {

        BigDecimal totalSales = isEmployee
                ? transactionHistoryRepository.getTotalSalesByEmployeeAndDateRange(targetId, startDate, endDate)
                : transactionHistoryRepository.getTotalSalesByStoreAndDateRange(targetId, startDate, endDate);

        if (totalSales == null) totalSales = BigDecimal.ZERO;

        final BigDecimal finalSales = totalSales;

        BigDecimal bonus = gradeStructures.stream()
                .filter(g -> finalSales.compareTo(g.getMinThreshold()) >= 0)
                .max(Comparator.comparing(GradeStructure::getMinThreshold))
                .map(g -> computeBonusAmount(g, employeeSalary))
                .orElse(BigDecimal.ZERO);

        return new BonusResult(finalSales, bonus);
    }

    private BigDecimal computeBonusAmount(GradeStructure g, BigDecimal salary) {
        boolean hasFixed   = g.getBonusAmount() != null
                && g.getBonusAmount().compareTo(BigDecimal.ZERO) > 0;
        boolean hasPercent = g.getBonusPercentage() != null
                && g.getBonusPercentage().compareTo(BigDecimal.ZERO) > 0;

        if (hasFixed && hasPercent) {
            return g.getBonusAmount()
                    .add(salary.multiply(g.getBonusPercentage())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        }
        if (hasFixed)   return g.getBonusAmount();
        if (hasPercent) return salary.multiply(g.getBonusPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return BigDecimal.ZERO;
    }

    public void saveGradeHistory(BigDecimal achievedSales, BigDecimal calculatedBonus, GradeStructure gradeStructure, Employee employee,
                                 LocalDate periodStart, LocalDate periodEnd) {

        GradeHistory gradeHistory = new GradeHistory();
        gradeHistory.setEmployee(employee);
        gradeHistory.setPosition(employee.getPosition());
        gradeHistory.setGradeStructure(gradeStructure);
        gradeHistory.setStore(employee.getStore());
        gradeHistory.setGradeDate(LocalDate.now());
        gradeHistory.setAchievedSales(achievedSales);
        gradeHistory.setCalculatedGradeAmount(calculatedBonus);
        gradeHistory.setPeriodStart(periodStart);
        gradeHistory.setPeriodEnd(periodEnd);

        gradeHistoryRepository.save(gradeHistory);
    }
}