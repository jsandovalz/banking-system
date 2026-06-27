CREATE DATABASE IF NOT EXISTS banking_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE   utf8mb4_unicode_ci;

USE banking_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS persons;
SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- Persona  (clase base, herencia JOINED en JPA)
-- -----------------------------------------------------------------------------
CREATE TABLE persons (
    person_id       BIGINT        NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)  NOT NULL,
    gender          CHAR(1)       NOT NULL,
    age             INT           NOT NULL,
    identification  VARCHAR(20)   NOT NULL,
    address         VARCHAR(200)  NOT NULL,
    phone           VARCHAR(20)   NOT NULL,
    PRIMARY KEY (person_id),
    UNIQUE KEY uk_persons_identification (identification),
    CONSTRAINT chk_persons_gender CHECK (gender IN ('M','F','O')),
    CONSTRAINT chk_persons_age    CHECK (age BETWEEN 0 AND 150)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Cliente  (extiende Person -> comparte person_id como PK y FK)
-- -----------------------------------------------------------------------------
CREATE TABLE clients (
    person_id            BIGINT        NOT NULL,
    client_id_external   VARCHAR(30)   NOT NULL,
    password             VARCHAR(100)  NOT NULL,
    status               BIT(1)        NOT NULL DEFAULT b'1',
    PRIMARY KEY (person_id),
    UNIQUE KEY uk_clients_client_id_external (client_id_external),
    CONSTRAINT fk_clients_persons FOREIGN KEY (person_id)
        REFERENCES persons (person_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Cuenta
-- -----------------------------------------------------------------------------
CREATE TABLE accounts (
    account_id          BIGINT         NOT NULL AUTO_INCREMENT,
    account_number      VARCHAR(20)    NOT NULL,
    account_type        VARCHAR(20)    NOT NULL,
    initial_balance     DECIMAL(19,2)  NOT NULL,
    available_balance   DECIMAL(19,2)  NOT NULL,
    status              BIT(1)         NOT NULL DEFAULT b'1',
    client_id           BIGINT         NOT NULL,
    PRIMARY KEY (account_id),
    UNIQUE KEY uk_accounts_account_number (account_number),
    KEY idx_accounts_client_id (client_id),
    CONSTRAINT fk_accounts_clients FOREIGN KEY (client_id)
        REFERENCES clients (person_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_accounts_type
        CHECK (account_type IN ('AHORRO','CORRIENTE')),
    CONSTRAINT chk_accounts_initial_balance
        CHECK (initial_balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Movimiento
--   Convencion del proyecto:
--     DEPOSITO -> amount > 0
--     RETIRO   -> amount < 0
--     balance  -> saldo disponible despues de aplicar el movimiento.
-- -----------------------------------------------------------------------------
CREATE TABLE transactions (
    transaction_id      BIGINT         NOT NULL AUTO_INCREMENT,
    date                DATETIME       NOT NULL,
    transaction_types   VARCHAR(20)    NOT NULL,
    amount              DECIMAL(19,2)  NOT NULL,
    balance             DECIMAL(19,2)  NOT NULL,
    account_id          BIGINT         NOT NULL,
    PRIMARY KEY (transaction_id),
    KEY idx_transactions_account_id      (account_id),
    KEY idx_transactions_date            (date),
    KEY idx_transactions_account_date    (account_id, date),
    KEY idx_transactions_account_type    (account_id, transaction_types, date),
    CONSTRAINT fk_transactions_accounts FOREIGN KEY (account_id)
        REFERENCES accounts (account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_transactions_types
        CHECK (transaction_types IN ('DEPOSITO','RETIRO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- DATOS DE EJEMPLO (Casos de uso del enunciado)
-- =============================================================================

-- ---- Personas ----------------------------------------------------------------
INSERT INTO persons (person_id, name, gender, age, identification, address, phone) VALUES
    (1, 'Jose Lema',          'M', 35, '0102030405', 'Otavalo sn y principal', '098254785'),
    (2, 'Marianela Montalvo', 'F', 30, '0203040506', 'Amazonas y NNUU',        '097548965'),
    (3, 'Juan Osorio',        'M', 28, '0304050607', '13 junio y Equinoccial', '098874587');

-- ---- Clientes ----------------------------------------------------------------
-- NOTA: en produccion el password debe almacenarse hasheado (BCrypt).
INSERT INTO clients (person_id, client_id_external, password, status) VALUES
    (1, 'joselema',   '1234', b'1'),
    (2, 'marianela',  '5678', b'1'),
    (3, 'juanosorio', '1245', b'1');

-- ---- Cuentas -----------------------------------------------------------------
INSERT INTO accounts (account_id, account_number, account_type, initial_balance, available_balance, status, client_id) VALUES
    (1, '478758', 'AHORRO',     2000.00, 2000.00, b'1', 1),
    (2, '225487', 'CORRIENTE',   100.00,  100.00, b'1', 2),
    (3, '495878', 'AHORRO',        0.00,    0.00, b'1', 3),
    (4, '496825', 'AHORRO',      540.00,  540.00, b'1', 2),
    (5, '585545', 'CORRIENTE',  1000.00, 1000.00, b'1', 1);

-- ---- Movimientos -------------------------------------------------------------
-- Reflejan los escenarios de prueba (deposito, retiro, retiro insuficiente).
INSERT INTO transactions (date, transaction_types, amount, balance, account_id) VALUES
    ('2026-05-03 14:15:00', 'RETIRO',   -575.00, 1425.00, 1),
    ('2026-05-04 09:00:00', 'DEPOSITO',  600.00, 700.00, 2),
    ('2026-05-04 19:00:00', 'DEPOSITO',  150.00, 150.00, 3),
    ('2026-05-05 16:45:00', 'RETIRO',   -540.00,  0.00, 4);

-- Mantener el saldo disponible coherente con el ultimo balance registrado
UPDATE accounts SET available_balance = 1425.00 WHERE account_id = 1;
UPDATE accounts SET available_balance =  700.00 WHERE account_id = 2;
UPDATE accounts SET available_balance =  150.00 WHERE account_id = 3;
UPDATE accounts SET available_balance =  0.00 WHERE account_id = 4;
