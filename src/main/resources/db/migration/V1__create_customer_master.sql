CREATE TABLE customer_master (
  id uuid PRIMARY KEY,
  code varchar(32) UNIQUE NOT NULL,
  last_name varchar(64),     -- 姓
  first_name varchar(64),    -- 名
  company_name varchar(128), -- 法人顧客用（個人ならNULL）
  billing_address text,
  tax_code varchar(16),
  credit_limit numeric(14,2) DEFAULT 0,
  active boolean DEFAULT true
);