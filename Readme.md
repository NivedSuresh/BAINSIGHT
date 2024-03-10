# Stock Broker Application

## Tools Used:

* WebSocket.
* Apache Kafka for streaming.
* Flyway for database migrations.
* Project Reactor to ensure scalability.
* Redis Cluster.
* Hazelcast embedded cache.
* Resilience4J as Circuit Breaker.
* Spring Cloud Reactive Gateway as API Gateway.
* Spring OAuth2 authorization server.
* R2DBC with PostgreSQL for non-blocking database querying.
* MongoDB Reactive
* JobRunr for distributed jobs.
* Zipkin for distributed logging.
* UDP Multicasting for Exchange Layer.
* Testcontainers.
* TDD.

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
       
       Considerations
       
       Implementation details may vary.
       Ensure robust security measures are in place.



------------------------------------------------------------------------------------------------------------------------------------------------------------------


## Market Data Processing with Redis Cluster and NoSQL Aggregation - MARKET DATA SERVICE (On Progress)

This script outlines a process for handling market data updates from exchanges (NSE/BSE) for a broker that's not co-located.

Link to design : https://www.figma.com/file/atrgPhH4OS6lmEwwsvNDmV/MARKET-DATA-SERVICE?type=design&node-id=0%3A1&mode=design&t=OBxyYBzm23eqzfjY-1

**Assumptions:**

        * Approximately 2200 tradable tickers on NSE (more than BSE).
        * Exchange sends updates every second (may vary in reality).
        * Not all updates need permanent storage.

**Proposed Approach:**

1. **Role of Redis Cluster:**

        * Each market update is persisted/rewritten in the cluster.
        * This data is the current state of the Market and can also be used for SOR/GTT/Stop Loss Orders by other services.
        * Estimated size per update: 80 bytes (object + key).

2. **Role of Hazelcast Embedded cache**

        * Efficent and Secure distribued locking using FencedLock.
        * Trending/Gainers/Losers (Tickers) are persisted and can be used by the UI during Homepage Initialization.

3. **Cron Job for Aggregation:**

        * A cron job runs every 10 minutes to aggregate data from Redis.
        * This aggregation could involve:
        * Creating new candlestick objects representing the last 10 minutes.
        * Calculating relevant market insights (e.g., symbol deviation within the last hour).

4. **Further Aggregation with NoSQL:**

        * The aggregated data (e.g., candlesticks) is persisted in a NoSQL database for further analysis and historical tracking.

5. **UDP Multicasting**

        * The MarketDataService will recieve real time updates from the exchange through multicast groups.
        * UDP is made reliable by ensuring rate matching. (Virtual Threads and Multiple Instances listening to the same events)
        * Duplicate updations/persistance are avoided using distributed locking.


6. **Benefits:**

        * Efficient storage and updates with in Redis.
        * Flexible aggregation and insight generation using distributed jobs.
        * Scalable storage of historical data in a NoSQL database.

7. **Note:**

        * This is a high-level overview. Actual implementation details and data sizes may vary.

        **Further Considerations:**

        * Data filtering strategies for selecting valuable updates in Redis.
        * Error handling and data consistency across different storage layers. 



------------------------------------------------------------------------------------------------------------------------------------------------------------------



## PENDING : OrderProcessingService, ValidationService, OrderManagementService, JournalService, PortfolioService