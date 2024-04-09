# Stock Broker Application


Link to design : https://www.figma.com/file/atrgPhH4OS6lmEwwsvNDmV/MARKET-DATA-SERVICE?type=design&node-id=0%3A1&mode=design&t=OBxyYBzm23eqzfjY-1



## Tools Used:

* TDD (TestContainers, JUNIT, Mockito)
* Aeron
* Lmax Disruptor
* Apache Kafka for streaming.
* Flyway for database migrations.
* Hazelcast embedded cache for Leader election.
* Spring Webflux to ensure scalability.
* WebSocket.
* Resilience4J as Circuit Breaker.
* Spring Cloud Reactive Gateway as API Gateway.
* Spring OAuth2 authorization server.
* Redis Cluster.
* ScyllaDB for Timeseries Data (Market Updates)
* R2DBC with PostgreSQL for non-blocking database querying.
* MongoDB Reactive
* JobRunr for distributed jobs.
* Zipkin for distributed logging.
* UDP Multicasting for Exchange Layer.
* Testcontainers.

------------------------------------------------------------------------------------------------------------------------------------------------------------------


## Authentication Service with Nimbus JWT and Spring Security

       **Approach**
       
      Authentication Flow:
       
       Users authenticate using credentials.
       JWTs are generated upon successful authentication.
       Refresh tokens allow for token renewal.


       Token Management:
       
       JWTs have a limited lifespan and may expire.
       Tokens are stored in HttpOnlyCookie mitigating XSS.
       Refresh tokens enable obtaining new JWTs without re-entering credentials.
       
       Sign-Up Logic:
       
       New users sign up with required information.
       Passwords are securely hashed before storage.



------------------------------------------------------------------------------------------------------------------------------------------------------------------


## Market Data Processing with ScyllaDB - MARKET DATA SERVICE (On Progress)

This script outlines a process for handling market data updates from exchanges (NSE/BSE) for a broker that's not co-located. The updates are received through UDP multicast. The user won't directly connect to this service but a seperate user layer. The MarketDataService will stream updates to the User layer and will also
snapshot the current OrderBook each minute of the day from 9:00am till 3:30pm. The snapshots are persisted on ScyllaDB.



**Assumptions:**

        * Approximately 2200 tradable tickers on NSE (more than BSE).
        * Exchange sends updates every second (may vary in reality).
        * Not all updates need permanent storage.

**Proposed Approach:**

1. **Role of ScyllaDB:**

        * Market Data Snapshot persistance/History Persistnace.

2. **Role of Hazelcast Embedded cache**

        * Low latency Leader election. All nodes will listen to the UDP feeds and will have their state upto date but only
          the leader would stream the tick updates to the UserService and take snapshots of the OrderBook.

4. **Cron Job for Aggregation:**

        * A cron job runs every 1 minute to aggregate data from Redis.
        * This aggregation could involve:
        * Creating new candlestick objects representing the last minute.
        * Calculating relevant market insights (e.g., symbol deviation within the last hour).

5. **Further Aggregation with TimeseriesDB:**

        * The aggregated data (e.g., candlesticks) are persisted in a Timeseries database for further analysis and historical tracking.

6. **UDP Multicasting**

        * The MarketDataService will recieve real time updates from the exchange through multicast groups.
        * UDP is made reliable by ensuring rate matching. (Virtual Threads and Multiple Instances listening to the same events)
        * Duplicate updations/persistance are avoided using distributed locking.

7. **LMAX Disruptor**

       * Lmax Disruptor was used to ensure low latency message(ticks) processing. It was used to avoid locks and multicast messages
         thus improving across the application thus enabling low latency and high through put.


------------------------------------------------------------------------------------------------------------------------------------------------------------------



## PENDING : OrderProcessingService, ValidationService, OrderManagementService, JournalService, PortfolioService
