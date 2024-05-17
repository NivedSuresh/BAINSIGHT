-- Create the keyspace
CREATE KEYSPACE IF NOT EXISTS candle_sticks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};


-- Switch to the keyspace
USE candle_sticks;


CREATE TABLE candle_sticks.candle_sticks (
    symbol text,
    timestamp timestamp,
    change double,
    close double,
    high double,
    low double,
    open double,
    volume bigint,
    PRIMARY KEY (symbol, timestamp)
) WITH CLUSTERING ORDER BY (timestamp ASC)
   AND bloom_filter_fp_chance = 0.01
   AND caching = {'keys': 'ALL', 'rows_per_partition': 'ALL'}
   AND comment = ''
   AND compaction = {'class': 'SizeTieredCompactionStrategy'}
   AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.LZ4Compressor'}
   AND crc_check_chance = 1.0
   AND dclocal_read_repair_chance = 0.0
   AND default_time_to_live = 0
   AND gc_grace_seconds = 864000
   AND max_index_interval = 2048
   AND memtable_flush_period_in_ms = 0
   AND min_index_interval = 128
   AND read_repair_chance = 0.0
   AND speculative_retry = '99.0PERCENTILE';

