# Bainsight (Stock Broker Application)


Link to design : https://www.figma.com/file/atrgPhH4OS6lmEwwsvNDmV/MARKET-DATA-SERVICE?type=design&node-id=0%3A1&mode=design&t=OBxyYBzm23eqzfjY-1



## Tools Used:

* Docker, Kubernetes, Azure AKS (Deployment)
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


## OrderProcessing, Portfolio/WalletManagement, OrderManagement and RiskManagement!

Order Processing will validate user's Portfolio/Wallet and then do additional risk checks as reccommended by SEBI.

      i.  Price Check: This means that when brokers use algorithms to place orders, they
          must ensure that these orders are within the acceptable marketPrice ranges set by the
          stock exchange for each security. They can't place orders at prices that are too
          high or too low compared to the current market prices.
      
      ii. Quantity Check: Brokers need to make sure that the quantity of shares they're
          ordering through algorithms doesn't exceed the limits set by the stock exchange.
          This prevents brokers from placing excessively large orders that could disrupt the market.
      
      iii.Order Value Check: Similar to the quantity check, brokers must also ensure
          that the total value of the orders placed through algorithms doesn't exceed
          certain limits defined by the stock exchange. This helps control the size of orders
          and reduces the risks of large market moves due to algorithmic trading.
      
      iv. Cumulative Open Order Value Check: Brokers may set limits on the total value
          of unexecuted orders (orders that have been placed but not yet filled) for each client.
          This helps manage the risks exposure of individual clients and prevents them from
          accumulating too many open orders.
      
      v.  Automated Execution Check: Algorithms must keep track of all the orders they've placed,
          whether they've been executed (filled), are still pending (unexecuted), or haven't been
          confirmed yet. Additionally, algorithms must have safeguards in place to automatically stop
          trading if they encounter any unexpected situations, such as placing orders in a loop or
          excessively rapid trading.
      
      vi. Order Tagging: Each order placed through algorithms must be marked with a unique identifier
          provided by the stock exchange. This helps regulators track and audit algorithmic trading
          activity to ensure compliance with regulations and detect any irregularities or abuses.

For technology choices:

1. **gRPC: Utilized for fast Order Processing and swift cancellations.**
2. **Kafka: Employed for Distributed transactions and Rollbacks.**
3. **PostgreSQL: Selected for Data Integrity and robust Pessimistic/Optimistic locking mechanisms.**
4. **Redis: Used as a Distributed cache for low-latency Risk Management.**
5. **JobRunr for distributed jobs such as resetting portfolio/wallet/orders once market is closed!**


## PENDING : JournalService
