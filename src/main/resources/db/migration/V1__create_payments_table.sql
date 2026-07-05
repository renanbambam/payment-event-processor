CREATE TABLE payments (
    id          VARCHAR(64)     PRIMARY KEY,
    amount      NUMERIC(19, 2)  NOT NULL,
    currency    CHAR(3)         NOT NULL,
    sender_id   VARCHAR(64)     NOT NULL,
    receiver_id VARCHAR(64)     NOT NULL,
    status      VARCHAR(16)     NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL,
    updated_at  TIMESTAMPTZ     NOT NULL
);

CREATE INDEX idx_payments_status ON payments (status);
CREATE INDEX idx_payments_sender ON payments (sender_id);
