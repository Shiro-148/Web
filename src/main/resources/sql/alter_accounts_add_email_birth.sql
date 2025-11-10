-- Add email and birth_date columns to accounts if they do not exist
-- MySQL 8 supports ADD COLUMN IF NOT EXISTS
ALTER TABLE accounts
  ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE NULL,
  ADD COLUMN IF NOT EXISTS birth_date DATE NULL;

-- If your MySQL version does not support IF NOT EXISTS, run these commands instead:
-- ALTER TABLE accounts ADD COLUMN email VARCHAR(255) NULL;
-- ALTER TABLE accounts ADD COLUMN birth_date DATE NULL;
-- ALTER TABLE accounts ADD UNIQUE (email);
