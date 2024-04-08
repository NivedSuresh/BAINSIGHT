-- Create the keyspace
CREATE KEYSPACE IF NOT EXISTS candlesticks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- Switch to the keyspace
USE candlesticks;
