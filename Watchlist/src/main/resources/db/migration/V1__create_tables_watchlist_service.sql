CREATE TABLE watchlist
(
    watchlist_id    BIGSERIAL PRIMARY KEY,
    watchlist_name  VARCHAR(50),
    ucc             UUID NOT NULL
);

CREATE TABLE watchlist_symbols
(
    id              BIGSERIAL PRIMARY KEY,
    watchlist_id    BIGSERIAL,
    symbol          VARCHAR(10)
)