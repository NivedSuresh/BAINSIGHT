CREATE TABLE watchlist
(
    watchlist_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ucc            UUID,
    watchlist_name VARCHAR(255),
    is_pinned      BOOLEAN,
    CONSTRAINT pk_watchlist PRIMARY KEY (watchlist_id)
);

CREATE TABLE watchlist_symbols
(
    watchlist_id BIGINT NOT NULL,
    symbol       VARCHAR(255)
);

ALTER TABLE watchlist
    ADD CONSTRAINT ucc_watchlist UNIQUE (ucc, watchlist_name);

ALTER TABLE watchlist_symbols
    ADD CONSTRAINT fk_watchlist_symbols_on_watchlist FOREIGN KEY (watchlist_id) REFERENCES watchlist (watchlist_id);