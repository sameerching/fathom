CREATE TABLE category_rules (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_users(id),
  name VARCHAR(255) NOT NULL,
  priority INT NOT NULL DEFAULT 100,
  rule_field VARCHAR(40) NOT NULL,
  match_operator VARCHAR(40) NOT NULL,
  match_value VARCHAR(255) NOT NULL,
  category_id UUID NOT NULL REFERENCES categories(id),
  transaction_type VARCHAR(40),
  direction VARCHAR(20),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_category_rules_user_id ON category_rules(user_id);
CREATE INDEX idx_category_rules_user_active ON category_rules(user_id, active);
CREATE INDEX idx_category_rules_user_priority ON category_rules(user_id, priority);
