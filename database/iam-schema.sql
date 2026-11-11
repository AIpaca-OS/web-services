-- Apply to PostgreSQL database "rumbo" before enabling IAM (production migration).
CREATE TABLE IF NOT EXISTS iam_accounts (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('DRIVER','TUTOR','ASSISTANT')),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    token_version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_iam_accounts_email ON iam_accounts (LOWER(email));
CREATE TABLE IF NOT EXISTS iam_email_actions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES iam_accounts(id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    purpose VARCHAR(25) NOT NULL CHECK (purpose IN ('VERIFY_EMAIL','RESET_PASSWORD')),
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS ix_iam_email_actions_account ON iam_email_actions(account_id);
