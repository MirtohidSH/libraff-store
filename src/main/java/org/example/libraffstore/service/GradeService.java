package org.example.libraffstore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.GradeHistory;
import org.example.libraffstore.entity.GradeStructure;
import org.example.libraffstore.repository.GradeHistoryRepository;
import org.example.libraffstore.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final GradeHistoryRepository gradeHistoryRepository;

    public BigDecimal calculateTotalBonusForEmployee(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                                                 List<GradeStructure> gradeStructures) {
        LocalDate startDate;
        LocalDate endDate;

        if (gradeStructures == null || gradeStructures.isEmpty()) {  // ← bunu əlavə et
            return BigDecimal.ZERO;
        }

        switch (gradeStructures.getFirst().getPeriodType()) {

            case MONTHLY -> {
                startDate = periodEnd.with(TemporalAdjusters.firstDayOfMonth());
                endDate = periodEnd.with(TemporalAdjusters.lastDayOfMonth());
            }
            case ANNUAL -> {
                int month = periodEnd.getMonthValue(); // return month in integer value: e.g. April -> 4 etc.
                int seasonStartMonth = ((month - 1) / 3) * 3 + 1; // formula to find start month of the season
                startDate = LocalDate.of(periodEnd.getYear(), seasonStartMonth, 1); // year, month, day
                endDate = startDate.plusMonths(3).minusDays(1); // 10.03.2026 - > 31.03.2026

            }
            case YEARLY -> {
                startDate = periodEnd.with(TemporalAdjusters.firstDayOfYear());
                endDate = periodEnd.with(TemporalAdjusters.lastDayOfYear());
            }
            default -> {
                return BigDecimal.ZERO;
            }
        }
        ;

        return calculateBonus(employee.getId(), employee.getSalary(), true, startDate, endDate, gradeStructures);

    }


    public BigDecimal calculateTotalBonusForStore(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                                              List<GradeStructure> gradeStructures) {
        LocalDate startDate;
        LocalDate endDate;

        switch (gradeStructures.getFirst().getPeriodType()) {

            case MONTHLY -> {
                startDate = periodEnd.with(TemporalAdjusters.firstDayOfMonth());
                endDate = periodEnd.with(TemporalAdjusters.lastDayOfMonth());
            }
            case ANNUAL -> {
                int month = periodEnd.getMonthValue(); // return month in integer value: e.g. April -> 4 etc.
                int seasonStartMonth = ((month - 1) / 3) * 3 + 1; // formula to find start month of the season
                startDate = LocalDate.of(periodEnd.getYear(), seasonStartMonth, 1); // year, month, day
                endDate = startDate.plusMonths(3).minusDays(1); // 10.03.2026 - > 31.05.2026

            }
            case YEARLY -> {
                startDate = periodEnd.with(TemporalAdjusters.firstDayOfYear());
                endDate = periodEnd.with(TemporalAdjusters.lastDayOfYear());
            }
            default -> {
                return BigDecimal.ZERO;
            }
        }

        return calculateBonus(employee.getStore().getId(), employee.getSalary(), false, startDate, endDate,
                gradeStructures);
    }

    private BigDecimal calculateBonus(Long targetId, BigDecimal employeeSalary, boolean isEmployee, LocalDate startDate,
                                  LocalDate endDate, List<GradeStructure> gradeStructures) {

        if (gradeStructures == null || gradeStructures.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSales;

        if (isEmployee) {
            totalSales = transactionHistoryRepository.getTotalSalesByEmployeeAndDateRange(targetId, startDate, endDate);
        } else {
            totalSales = transactionHistoryRepository.getTotalSalesByStoreAndDateRange(targetId, startDate, endDate);
        }

        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        final BigDecimal finalSales = totalSales;
        // if the statament reached here, it means no bonus is calculated
        // for the employee
        return gradeStructures.stream()
                .filter(g -> finalSales.compareTo(g.getMinThreshold()) >= 0)
                .max(Comparator.comparing(GradeStructure::getMinThreshold))
                .map(g -> {
                    boolean hasFixed = g.getBonusAmount() != null && g.getBonusAmount().compareTo(BigDecimal.ZERO) > 0;
                    boolean hasPercent = g.getBonusPercentage() != null && g.getBonusPercentage().compareTo(BigDecimal.ZERO) > 0;

                    if (hasFixed && hasPercent) {
                        return g.getBonusAmount()
                                .add(employeeSalary.multiply(g.getBonusPercentage())
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    }
                    if (hasFixed) {
                        return g.getBonusAmount();
                    }
                    if (hasPercent) {
                        return employeeSalary.multiply(g.getBonusPercentage())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }

                    return BigDecimal.ZERO;
                })
                .orElse(BigDecimal.ZERO);
    }

    public void saveGradeHistory(BigDecimal totalSales, GradeStructure gradeStructure, Employee employee, LocalDate periodStart, LocalDate periodEnd) {

        GradeHistory gradeHistory = new GradeHistory();

        gradeHistory.setEmployee(employee);
        gradeHistory.setPosition(employee.getPosition());
        gradeHistory.setGradeStructure(gradeStructure);
        gradeHistory.setStore(employee.getStore());
        gradeHistory.setGradeDate(LocalDate.now());
        gradeHistory.setAchievedSales(totalSales);
        gradeHistory.setPeriodStart(periodStart);
        gradeHistory.setPeriodEnd(periodEnd);

        gradeHistoryRepository.save(gradeHistory);
    }
}
