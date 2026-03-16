# Libraff Store — Book Store Management System

A Spring Boot REST API for managing a multi-branch book store chain. The system handles employee lifecycle, inventory, sales, book and employee transfers, discounts, grade-based bonuses, and automated payroll processing.

---

## Tech Stack

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **ModelMapper**
- **Gradle**

---

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Gradle

### Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE "libraff-db";
```

### Configuration

Update `src/main/resources/application.yml` with your credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/libraff-db
    username: postgres
    password: postgres
```

### Run

```bash
./gradlew bootRun
```

---

## API Endpoints

### Employees — `/employees`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/employees` | Get all employees |
| GET | `/employees/{id}` | Get employee by ID |
| GET | `/employees/active` | Get active employees |
| POST | `/employees` | Add new employee or rehire by FIN |
| PUT | `/employees/{id}` | Update employee personal info |
| DELETE | `/employees/{id}` | Deactivate employee (soft delete) |

### Employee Transfers — `/emloyee-transfer`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/emloyee-transfer` | Transfer employee to another branch |

### Book Transfers — `/book-transfers`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/book-transfers/available/{bookId}` | Check stock across all branches |
| POST | `/book-transfers` | Create a transfer request |
| GET | `/book-transfers/pending` | View pending requests (manager) |
| PATCH | `/book-transfers/{id}/approve?managerId=` | Approve transfer (manager only) |
| PATCH | `/book-transfers/{id}/reject?managerId=` | Reject transfer (manager only) |

### Sales — `/transaction-histories`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/transaction-histories/sell` | Sell books with automatic discount applied |

### Discounts — `/discounts`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/discounts` | Create a discount |
| GET | `/discounts/active` | Get all active discounts |

### Payroll — `/payroll`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/payroll` | Manually trigger payroll processing |

### Grade History — `/grade-history`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/grade-history` | Get all grade bonus history |

### Books — `/books`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/books` | Get all books |

---

## Key Business Logic

### Employee Lifecycle
- When adding an employee, the system checks by **FIN**:
  - If no record exists → creates new employee, saves `HIRED` work history
  - If exists but inactive → rehires them, updates info, saves `REHIRED` work history
  - If exists and active → throws conflict error
- On deactivation → saves `RESIGNED` work history
- On transfer → saves `TRANSFERRED` work history, closes previous history record

### Work History Types

| Type | Description |
|------|-------------|
| `HIRED` | Employee joined the company |
| `TRANSFERRED` | Moved to another branch |
| `RESIGNED` | Left the company |
| `REHIRED` | Previously resigned, rejoined |

### Book Transfers
- Employee requests transfer by specifying book, source branch, and destination branch
- System validates stock availability before creating the request (`PENDING`)
- Only a `MANAGER` position employee can approve or reject
- On approval: stock is deducted from source, added to destination (`COMPLETED`)
- If destination has no stock record for the book, one is created automatically

### Discount System
- Discounts can be applied to: **book**, **author**, **genre**, or **store**
- Only **one target** per discount (cannot combine)
- Discount percentage must be between **5% and 40%**
- Date-range based: `startDate` to `endDate`
- At point of sale, the system finds the highest applicable active discount and applies it automatically
- Original `salesPrice` in the database never changes — discounted price is calculated on the fly

### Payroll (Automated)
- Runs automatically on the **1st of every month** via `@Scheduled`
- Calculates **pro-rated salary** for employees hired mid-month
- Skips employees already paid for the current period (duplicate prevention)
- Calculates **grade bonuses** from two sources:
  - **Position-based bonus**: based on individual employee sales
  - **Store-based bonus**: based on total store sales
- Saves full salary history including bonus breakdown per employee

### Grade Bonus System
- `GradeStructure` defines sales thresholds (`minThreshold`) and bonus rules (`bonusPercentage` / `bonusAmount`)
- `GradePosition` links grade structures to positions
- `GradeStore` links grade structures to stores
- Supports **monthly**, **seasonal (ANNUAL)**, and **yearly** periods
- The highest matching threshold is applied

### Salary Validation
- Each `Position` has `minSalary` and `maxSalary`
- Salary is validated on both `addEmployee` and `transferEmployee`

---

## Project Structure

```
src/main/java/org/example/libraffstore/
├── config/         # App config (ModelMapper, Scheduling)
├── controller/     # REST controllers
├── dto/
│   ├── request/    # Incoming request DTOs
│   └── response/   # Outgoing response DTOs
├── entity/         # JPA entities
├── enums/          # Enumerations
├── exception/      # Custom exceptions & GlobalExceptionHandler
├── mapper/         # Entity → DTO mappers
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic
└── validator/      # Input validators
```

---

## Position Types

| Position | Description |
|----------|-------------|
| `SALES_REPRESENTATIVE` | Regular sales staff |
| `HEAD_SALES_REPRESENTATIVE` | Senior sales staff |
| `CASHIER` | Cashier |
| `MANAGER` | Branch manager — can approve transfers |
