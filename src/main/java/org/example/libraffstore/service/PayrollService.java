package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libraffstore.dto.BonusResult;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.GradeStructure;
import org.example.libraffstore.entity.SalaryHistory;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.GradePositionRepository;
import org.example.libraffstore.repository.GradeStoreRepository;
import org.example.libraffstore.repository.SalaryHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollService {

    private final SalaryHistoryRepository salaryHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final GradePositionRepository gradePositionRepository;
    private final GradeStoreRepository gradeStoreRepository;
    private final GradeService gradeService;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void payMonthlySalary() {
        log.info("Starting automated payroll processing...");
        String currentPeriod = YearMonth.now().minusMonths(1).toString();

        employeeRepository.findAllByIsActiveTrue().forEach(employee -> {
            try {
                processEmployeeSalary(employee, currentPeriod);
            } catch (Exception e) {
                log.error("Failed to pay Employee {}: {}", employee.getFirstName(), e.getMessage());
            }
        });

        log.info("Payroll processing completed for period: {}", currentPeriod);
    }

    private void processEmployeeSalary(Employee employee, String currentPeriod) {
        if (salaryHistoryRepository.existsByEmployeeAndPayPeriod(employee, currentPeriod)) {
            log.warn("Skipping Employee ID {}: Already paid for {}", employee.getId(), currentPeriod);
            return;
        }

        YearMonth yearMonth = YearMonth.parse(currentPeriod);
        LocalDate periodStart = yearMonth.atDay(1);
        LocalDate periodEnd   = yearMonth.atEndOfMonth();

        if (employee.getDateEmployed().isAfter(periodEnd)) {
            log.warn("Skipping Employee ID {}: Hired after period {}", employee.getId(), currentPeriod);
            return;
        }

        BigDecimal salaryAmount = calculateSalary(employee, periodStart, periodEnd);

        List<GradeStructure> employeeGrades = gradePositionRepository
                .findAllGradesByPositionId(employee.getPosition().getId());

        List<GradeStructure> storeGrades = gradeStoreRepository
                .findAllGradesByStoreId(employee.getStore().getId());

        BonusResult employeeBonus = gradeService.calculateTotalBonusForEmployee(
                employee, periodEnd, employeeGrades);

        BonusResult storeBonus = gradeService.calculateTotalBonusForStore(
                employee, periodEnd, storeGrades);

        BigDecimal totalBonus = employeeBonus.bonusAmount().add(storeBonus.bonusAmount());

        if (!employeeGrades.isEmpty() && totalBonus.compareTo(BigDecimal.ZERO) > 0) {
            employeeGrades.stream()
                    .filter(g -> g.getMinThreshold() != null)
                    .max(Comparator.comparing(GradeStructure::getMinThreshold))
                    .ifPresent(bestGrade -> gradeService.saveGradeHistory(
                            employeeBonus.achievedSales(),
                            totalBonus,
                            bestGrade, employee, periodStart, periodEnd));
        }

        saveSalaryHistory(employee, salaryAmount, totalBonus, currentPeriod);
        log.info("Successfully processed salary for Employee: {}", employee.getFirstName());
    }

    private BigDecimal calculateSalary(Employee employee, LocalDate periodStart, LocalDate periodEnd) {
        LocalDate hireDate = employee.getDateEmployed();

        if (!hireDate.isAfter(periodStart)) {
            return employee.getSalary();
        }

        long daysWorked = ChronoUnit.DAYS.between(hireDate, periodEnd) + 1;
        int totalDaysInMonth = periodStart.lengthOfMonth();

        return employee.getSalary()
                .divide(BigDecimal.valueOf(totalDaysInMonth), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(daysWorked))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void saveSalaryHistory(Employee employee, BigDecimal salaryAmount, BigDecimal totalBonus, String currentPeriod) {
        SalaryHistory salaryHistory = new SalaryHistory();
        salaryHistory.setEmployee(employee);
        salaryHistory.setSalaryAmount(salaryAmount);
        salaryHistory.setBonusAmount(totalBonus);
        salaryHistory.setTotalAmount(salaryAmount.add(totalBonus));
        salaryHistory.setStore(employee.getStore());
        salaryHistory.setPayPeriod(currentPeriod);
        salaryHistory.setSalaryGivenDate(LocalDate.now());

        salaryHistoryRepository.save(salaryHistory);
    }
}