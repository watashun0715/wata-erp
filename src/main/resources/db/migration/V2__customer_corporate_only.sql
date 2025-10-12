-- 1) 個人向けのカラムが万一残っていた場合は削除（存在すれば）
ALTER TABLE customer_master DROP COLUMN IF EXISTS first_name;
ALTER TABLE customer_master DROP COLUMN IF EXISTS last_name;

-- 2) 法人前提の制約を明確化
-- 会社名は必須
ALTER TABLE customer_master
  ALTER COLUMN company_name SET NOT NULL;

-- 法人コードのユニーク制約（既にあればスキップ）
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
      WHERE schemaname = 'public'
        AND indexname = 'ux_customer_code'
  ) THEN
    CREATE UNIQUE INDEX ux_customer_code ON customer_master(code);
  END IF;
END$$;

-- 3) 型・長さ・既定値の統一（すでに合っていれば no-op）
ALTER TABLE customer_master
  ALTER COLUMN code TYPE varchar(32),
  ALTER COLUMN company_name TYPE varchar(128),
  ALTER COLUMN billing_address TYPE varchar(256),
  ALTER COLUMN tax_code TYPE varchar(16);

-- 金額は「0」既定。NULL を入れさせない方針なら NOT NULL + DEFAULT
ALTER TABLE customer_master
  ALTER COLUMN credit_limit SET DEFAULT 0.00,
  ALTER COLUMN credit_limit SET NOT NULL;