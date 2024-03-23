CREATE TABLE symbol_meta
(
    id SERIAL PRIMARY KEY,
    symbol_meta VARCHAR(20) NOT NULL,
    exchange VARCHAR(5) NOT NULL,
    constraint combo_unq UNIQUE (symbol_meta, exchange)
);


CREATE TABLE client_meta
(
    ucc UUID PRIMARY KEY,
    balance DOUBLE PRECISION DEFAULT 0.0
);


CREATE TABLE client_security
(
    id SERIAL PRIMARY KEY,
    ucc UUID REFERENCES client_meta (ucc),
    symbol_id BIGINT REFERENCES symbol_meta(id),
    size BIGINT NOT NULL CHECK (size >= 0)
);

