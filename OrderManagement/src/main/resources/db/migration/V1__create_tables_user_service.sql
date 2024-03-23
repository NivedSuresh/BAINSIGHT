CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    ucc UUID NOT NULL,
    side VARCHAR(3) NOT NULL,
    order_type VARCHAR(10) NOT NULL,
    exchange_order_status VARCHAR(20) NOT NULL ,
    status VARCHAR(20) NOT NULL,
    symbol VARCHAR(10),
    matched_size BIGINT DEFAULT 0,
    executed_price DECIMAL(10, 2),
    total_price DECIMAL(10, 2),
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    validity VARCHAR(3),
    validity_ttl TIMESTAMP WITH TIME ZONE
);

create TABLE order_exchange(
    id BIGSERIAL PRIMARY KEY,
    exchange VARCHAR(10) NOT NULL,
    quantity BIGINT NOT NULL
);

CREATE TABLE match (
    match_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    exchange_match_id UUID,
    matched_order_id UUID,
    matched_quantity BIGINT NOT NULL,
    matched_price DECIMAL(10, 2) NOT NULL,
    matched_broker_id  VARCHAR(255),
    execution_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
