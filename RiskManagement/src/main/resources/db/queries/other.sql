INSERT INTO symbol_meta
values (1, 'AAPL');
INSERT INTO client
values ('b7096d4b-497a-4bdd-a082-81f605462d24', 'BAINSIGHT');
INSERT INTO client_symbol
values (1, 'b7096d4b-497a-4bdd-a082-81f605462d24', 'AAPL', 10000);



drop table symbol_meta, client, client_symbol, orders, flyway_schema_history;