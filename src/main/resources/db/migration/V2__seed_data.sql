-- Roles
INSERT INTO roles (name) VALUES
                             ('ADMIN'),
                             ('MANAGER'),
                             ('CASHIER'),
                             ('SALES_REPRESENTATIVE');

-- Positions
INSERT INTO positions (position_type, max_salary, min_salary) VALUES
                                                                  ('MANAGER', 5000, 3000),
                                                                  ('CASHIER', 2500, 1500),
                                                                  ('SALES_REPRESENTATIVE', 3000, 1800),
                                                                  ('HEAD_SALES_REPRESENTATIVE', 4000, 2500);

-- Genres
INSERT INTO genres (name) VALUES
                              ('Fiction'),
                              ('Non-fiction'),
                              ('Science'),
                              ('History'),
                              ('Business');

-- Company + Store
INSERT INTO companies (name, address, phone)
VALUES ('Libraff Group', 'Baku, Azerbaijan', '+994501112233');

INSERT INTO stores (name, address, phone, company_id)
VALUES ('Libraff 28Mall', 'Baku 28Mall', '+994501112234', 1);