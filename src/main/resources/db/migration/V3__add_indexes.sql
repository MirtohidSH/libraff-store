CREATE INDEX idx_books_genre ON books(genre_id);
CREATE INDEX idx_books_name ON books(name);

CREATE INDEX idx_stock_store_book ON store_book_stocks(store_id, book_id);

CREATE INDEX idx_employee_store ON employees(store_id);
CREATE INDEX idx_employee_position ON employees(position_id);

CREATE INDEX idx_tx_store_date ON transaction_histories(store_id, transaction_date);
CREATE INDEX idx_tx_book_date ON transaction_histories(book_id, transaction_date);

CREATE INDEX idx_transfer_status ON book_transfers(transfer_status);
CREATE INDEX idx_transfer_requested_at ON book_transfers(requested_at);