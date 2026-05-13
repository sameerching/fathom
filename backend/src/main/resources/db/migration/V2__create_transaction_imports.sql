CREATE TABLE transaction_imports (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_users(id),
  account_id UUID NOT NULL REFERENCES financial_accounts(id),
  source VARCHAR(30) NOT NULL,
  original_filename VARCHAR(255) NOT NULL,
  status VARCHAR(30) NOT NULL,
  total_rows INT NOT NULL DEFAULT 0,
  created_count INT NOT NULL DEFAULT 0,
  skipped_duplicate_count INT NOT NULL DEFAULT 0,
  failed_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE transaction_import_errors (
  id UUID PRIMARY KEY,
  import_id UUID NOT NULL REFERENCES transaction_imports(id),
  row_number INT NOT NULL,
  message TEXT NOT NULL,
  raw_row TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
