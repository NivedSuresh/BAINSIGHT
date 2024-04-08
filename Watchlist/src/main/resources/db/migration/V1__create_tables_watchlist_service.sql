create table watchlist (
    is_pinned boolean,
    watchlist_id bigserial not null,
    ucc uuid, watchlist_name varchar(255),
    primary key (watchlist_id),
    constraint ucc_watchlist unique (ucc, watchlist_name)
);

create table watchlist_symbols (watchlist_id bigint not null, symbols varchar(255))