CREATE TABLE recurring_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_users(id),
    account_id UUID REFERENCES financial_accounts(id),
    category_id UUID REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    direction VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(40) NOT NULL,
    frequency VARCHAR(40) NOT NULL,
    day_of_month INT,
    start_date DATE NOT NULL,
    end_date DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_recurring_transactions_user_id ON recurring_transactions(user_id);
CREATE INDEX idx_recurring_transactions_user_active ON recurring_transactions(user_id, active);
CREATE INDEX idx_recurring_transactions_user_type ON recurring_transactions(user_id, transaction_type);
