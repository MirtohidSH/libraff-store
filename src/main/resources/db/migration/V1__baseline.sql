-- =========================
-- BASELINE SCHEMA (PostgreSQL)
-- =========================

-- 1) COMPANIES
CREATE TABLE companies (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           phone VARCHAR(15) NOT NULL UNIQUE
);

-- 2) STORES
CREATE TABLE stores (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        address VARCHAR(255) NOT NULL,
                        phone VARCHAR(15) NOT NULL UNIQUE,
                        company_id BIGINT NOT NULL REFERENCES companies(id)
);

-- 3) GENRES
CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE
);

-- 4) AUTHORS
CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL
);

-- 5) BOOKS
CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       date_published DATE,
                       purchase_price NUMERIC(10,2) NOT NULL,
                       sales_price NUMERIC(10,2) NOT NULL,
                       purchase_date DATE,
                       sales_date DATE,
                       publication_amount INT,
                       genre_id BIGINT NOT NULL REFERENCES genres(id)
);

-- 6) BOOK_AUTHOR (MANY-TO-MANY)
CREATE TABLE book_author (
                             book_id BIGINT NOT NULL REFERENCES books(id),
                             author_id BIGINT NOT NULL REFERENCES authors(id),
                             PRIMARY KEY (book_id, author_id)
);

-- 7) STORE BOOK STOCK
CREATE TABLE store_book_stocks (
                                   id BIGSERIAL PRIMARY KEY,
                                   quantity INT NOT NULL,
                                   store_id BIGINT NOT NULL REFERENCES stores(id),
                                   book_id BIGINT NOT NULL REFERENCES books(id),
                                   CONSTRAINT uq_store_book UNIQUE (store_id, book_id)
);

-- 8) DISCOUNTS
CREATE TABLE discounts (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           discount_percent NUMERIC(5,2) NOT NULL,
                           start_date DATE NOT NULL,
                           end_date DATE NOT NULL,
                           is_active BOOLEAN NOT NULL,
                           book_id BIGINT REFERENCES books(id),
                           author_id BIGINT REFERENCES authors(id),
                           genre_id BIGINT REFERENCES genres(id),
                           store_id BIGINT REFERENCES stores(id)
);

-- 9) ROLES
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- 10) POSITIONS
CREATE TABLE positions (
                           id BIGSERIAL PRIMARY KEY,
                           position_type VARCHAR(50) NOT NULL,
                           max_salary INT,
                           min_salary INT,
                           CONSTRAINT chk_position_type CHECK (position_type IN (
                                                                                 'SALES_REPRESENTATIVE','HEAD_SALES_REPRESENTATIVE','CASHIER','MANAGER'
                               ))
);

-- 11) EMPLOYEES
CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,
                           fin VARCHAR(50) NOT NULL UNIQUE,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           password VARCHAR(255) NOT NULL,
                           is_active BOOLEAN DEFAULT FALSE,
                           email VARCHAR(150),
                           phone VARCHAR(30),
                           salary NUMERIC(10,2),
                           date_employed DATE,
                           date_unemployed DATE,
                           store_id BIGINT NOT NULL REFERENCES stores(id),
                           position_id BIGINT NOT NULL REFERENCES positions(id)
);

-- 12) EMPLOYEE_ROLES
CREATE TABLE employee_roles (
                                employee_id BIGINT NOT NULL REFERENCES employees(id),
                                role_id BIGINT NOT NULL REFERENCES roles(id),
                                PRIMARY KEY (employee_id, role_id)
);

-- 13) EMPLOYEE TRANSFERS
CREATE TABLE employees_transfers (
                                     id BIGSERIAL PRIMARY KEY,
                                     transfer_date DATE NOT NULL,
                                     new_salary NUMERIC(10,2) NOT NULL,
                                     employee_id BIGINT NOT NULL REFERENCES employees(id),
                                     from_store_id BIGINT NOT NULL REFERENCES stores(id),
                                     to_store_id BIGINT NOT NULL REFERENCES stores(id)
);

-- 14) EMPLOYEE WORK HISTORIES
CREATE TABLE employees_work_histories (
                                          id BIGSERIAL PRIMARY KEY,
                                          is_active BOOLEAN NOT NULL,
                                          salary NUMERIC(10,2) NOT NULL,
                                          start_date DATE NOT NULL,
                                          end_date DATE,
                                          history_type VARCHAR(30) NOT NULL,
                                          employee_id BIGINT NOT NULL REFERENCES employees(id),
                                          store_id BIGINT NOT NULL REFERENCES stores(id),
                                          position_id BIGINT NOT NULL REFERENCES positions(id),
                                          CONSTRAINT chk_history_type CHECK (history_type IN (
                                                                                              'HIRED','TRANSFERRED','RESIGNED','REHIRED'
                                              ))
);

-- 15) GRADE STRUCTURES
CREATE TABLE grade_structures (
                                  id BIGSERIAL PRIMARY KEY,
                                  bonus_percentage NUMERIC(5,2),
                                  bonus_amount NUMERIC(10,2),
                                  min_threshold NUMERIC(10,2),
                                  target_type VARCHAR(20),
                                  period_type VARCHAR(20) NOT NULL,
                                  grade_type VARCHAR(20) NOT NULL,
                                  grade_name VARCHAR(100),
                                  CONSTRAINT chk_grade_target_type CHECK (target_type IN ('EMPLOYEE','STORE')),
                                  CONSTRAINT chk_period_type CHECK (period_type IN ('MONTHLY','ANNUAL','YEARLY')),
                                  CONSTRAINT chk_grade_type CHECK (grade_type IN ('GRAD_A','GRAD_B','GRAD_C'))
);

-- 16) GRADE POSITIONS
CREATE TABLE grade_positions (
                                 id BIGSERIAL PRIMARY KEY,
                                 grade_structure_id BIGINT NOT NULL REFERENCES grade_structures(id),
                                 position_id BIGINT NOT NULL REFERENCES positions(id)
);

-- 17) GRADE STORES
CREATE TABLE grade_stores (
                              id BIGSERIAL PRIMARY KEY,
                              store_id BIGINT NOT NULL REFERENCES stores(id),
                              grade_structure_id BIGINT NOT NULL REFERENCES grade_structures(id)
);

-- 18) GRADE HISTORIES
CREATE TABLE grade_histories (
                                 id BIGSERIAL PRIMARY KEY,
                                 employee_id BIGINT NOT NULL REFERENCES employees(id),
                                 grade_id BIGINT REFERENCES grade_structures(id),
                                 position_id BIGINT REFERENCES positions(id),
                                 store_id BIGINT REFERENCES stores(id),
                                 grade_date DATE NOT NULL,
                                 achieved_sales NUMERIC(10,2) NOT NULL,
                                 calculated_grade_amount NUMERIC(10,2),
                                 period_start DATE NOT NULL,
                                 period_end DATE NOT NULL
);

-- 19) TRANSACTIONS
CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              total_purchase NUMERIC(10,2) NOT NULL,
                              total_sales NUMERIC(10,2) NOT NULL,
                              profit NUMERIC(10,2) NOT NULL,
                              period_start DATE NOT NULL,
                              period_end DATE NOT NULL,
                              period_type VARCHAR(20) NOT NULL,
                              store_id BIGINT NOT NULL REFERENCES stores(id),
                              CONSTRAINT chk_transaction_period_type CHECK (period_type IN ('MONTHLY','ANNUAL','YEARLY'))
);

-- 20) TRANSACTION HISTORIES
CREATE TABLE transaction_histories (
                                       id BIGSERIAL PRIMARY KEY,
                                       quantity INT NOT NULL,
                                       purchase_price NUMERIC(10,2) NOT NULL,
                                       sales_price NUMERIC(10,2) NOT NULL,
                                       transaction_date DATE NOT NULL,
                                       store_id BIGINT NOT NULL REFERENCES stores(id),
                                       book_id BIGINT NOT NULL REFERENCES books(id),
                                       employee_id BIGINT NOT NULL REFERENCES employees(id)
);

-- 21) SALARY HISTORIES
CREATE TABLE salary_histories (
                                  id BIGSERIAL PRIMARY KEY,
                                  employee_id BIGINT NOT NULL REFERENCES employees(id),
                                  salary_amount NUMERIC(10,2) NOT NULL,
                                  bonus_amount NUMERIC(10,2) NOT NULL,
                                  total_amount NUMERIC(10,2) NOT NULL,
                                  pay_period VARCHAR(50) NOT NULL,
                                  store_id BIGINT REFERENCES stores(id),
                                  salary_given_date DATE NOT NULL
);

-- 22) BOOK TRANSFERS
CREATE TABLE book_transfers (
                                id BIGSERIAL PRIMARY KEY,
                                book_id BIGINT NOT NULL REFERENCES books(id),
                                from_store_id BIGINT NOT NULL REFERENCES stores(id),
                                to_store_id BIGINT NOT NULL REFERENCES stores(id),
                                requested_employee_id BIGINT NOT NULL REFERENCES employees(id),
                                approved_employee_id BIGINT REFERENCES employees(id),
                                quantity INT NOT NULL,
                                requested_at TIMESTAMP NOT NULL,
                                approved_at TIMESTAMP,
                                completed_at TIMESTAMP,
                                transfer_status VARCHAR(20) NOT NULL,
                                CONSTRAINT chk_transfer_status CHECK (transfer_status IN ('REJECTED','PENDING','COMPLETED'))
);

-- =========================
-- INDEXES (senior performance)
-- =========================
CREATE INDEX idx_books_genre ON books(genre_id);
CREATE INDEX idx_books_name ON books(name);

CREATE INDEX idx_stock_store_book ON store_book_stocks(store_id, book_id);

CREATE INDEX idx_employee_store ON employees(store_id);
CREATE INDEX idx_employee_position ON employees(position_id);

CREATE INDEX idx_tx_store_date ON transaction_histories(store_id, transaction_date);
CREATE INDEX idx_tx_book_date ON transaction_histories(book_id, transaction_date);

CREATE INDEX idx_transfer_status ON book_transfers(transfer_status);
CREATE INDEX idx_transfer_requested_at ON book_transfers(requested_at);