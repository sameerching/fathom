CREATE TABLE budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_users(id),
    category_id UUID REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    month VARCHAR(7) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month);
CREATE INDEX idx_budgets_user_active ON budgets(user_id, active);
