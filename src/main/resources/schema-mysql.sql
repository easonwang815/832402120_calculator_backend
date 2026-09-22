CREATE TABLE IF NOT EXISTS calculation_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    expression VARCHAR(255) NOT NULL,
    result VARCHAR(1024) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
