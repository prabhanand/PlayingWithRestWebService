create database allpago;

use allpago;

DROP TABLE IF EXISTS office;
CREATE TABLE  office (
id BIGINT AUTO_INCREMENT NOT NULL,
name VARCHAR(255) NOT NULL,
address VARCHAR(500),
opentm TIME,
closetm TIME,
utcdiff INT,
constraint pk_office PRIMARY KEY (id)
);