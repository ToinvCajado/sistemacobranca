CREATE DATABASE sistema_cobranca;
USE sistema_cobranca;

CREATE TABLE cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nomeCliente VARCHAR(100) NOT NULL,
    endereco VARCHAR(150),
    uf CHAR(2),
    telefone VARCHAR(20),
    documento VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE divida (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    idCredor INT NOT NULL,
    idDevedor INT NOT NULL,
    dataAtualizacao DATE NOT NULL,
    valorDivida DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (idCredor) REFERENCES cliente(idCliente) ON DELETE RESTRICT,
    FOREIGN KEY (idDevedor) REFERENCES cliente(idCliente) ON DELETE RESTRICT,
    CONSTRAINT chk_credor_devedor_diferente CHECK (idCredor <> idDevedor)
);

CREATE TABLE pagamento (
    idpag INT AUTO_INCREMENT PRIMARY KEY,
    idDivida INT NOT NULL,
    dataPagamento DATE NOT NULL,
    valorPago DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (idDivida) REFERENCES divida(codigo) ON DELETE RESTRICT
);

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE VIEW dividas_em_aberto AS
SELECT d.codigo, d.idCredor, d.idDevedor, d.dataAtualizacao, d.valorDivida,
       COALESCE(SUM(p.valorPago), 0) AS totalPago,
       (d.valorDivida - COALESCE(SUM(p.valorPago), 0)) AS saldoRestante
FROM divida d
LEFT JOIN pagamento p ON p.idDivida = d.codigo
GROUP BY d.codigo
HAVING saldoRestante > 0;

DELIMITER $$
CREATE TRIGGER trg_bloquear_pagamento_quitado
BEFORE INSERT ON pagamento
FOR EACH ROW
BEGIN
    DECLARE totalPago DECIMAL(10,2);
    DECLARE valorDivida DECIMAL(10,2);

    SELECT COALESCE(SUM(valorPago), 0)
    INTO totalPago
    FROM pagamento
    WHERE idDivida = NEW.idDivida;

    SELECT valorDivida
    INTO valorDivida
    FROM divida
    WHERE codigo = NEW.idDivida;

    IF totalPago >= valorDivida THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Esta dívida já está quitada. Pagamento não permitido.';
    END IF;
END$$
DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_pagamento_inferior_bloqueado
BEFORE INSERT ON pagamento
FOR EACH ROW
BEGIN
    DECLARE totalPagamentos INT;
    DECLARE valorDivida DECIMAL(10,2);

    SELECT COUNT(*) INTO totalPagamentos
    FROM pagamento
    WHERE idDivida = NEW.idDivida;

    IF totalPagamentos = 0 THEN
        SELECT valorDivida INTO valorDivida
        FROM divida
        WHERE codigo = NEW.idDivida;

        IF NEW.valorPago < valorDivida THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'O valor do primeiro pagamento não pode ser inferior à dívida.';
        END IF;
    END IF;
END$$

DELIMITER ;
