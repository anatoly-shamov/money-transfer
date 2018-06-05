DROP TABLE IF EXISTS account;

CREATE TABLE account (
id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
holderName VARCHAR(200),
currencyCode CHAR(3),
balance DECIMAL(19,4)
);

INSERT INTO account (id, holderName, currencyCode, balance) VALUES
  (1, 'John Dow', 'EUR', 1000.0000),
  (2, 'John Dow', 'USD', 2000.0000),
  (3, 'Mary Jane', 'EUR', 3000.0000),
  (4, 'Mary Jane', 'USD', 4000.0000);