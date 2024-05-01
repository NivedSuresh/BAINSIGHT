package org.bainsight.order.Data.OrderPersistance;

import jakarta.persistence.LockModeType;
import org.bainsight.order.Model.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
interface OrderRepo extends PagingAndSortingRepository<Order, UUID>, JpaRepository<Order, UUID> {


    Optional<List<Order>> findByUcc(UUID ucc, PageRequest pageRequest);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Order> findWithLockingByOrderId(UUID orderId);


    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Order findWithLockingByUccAndOrderIdAndOrderStatusIn(UUID ucc, UUID orderId, List<String> statuses);


    @Modifying
    @Query("update Order o set o.orderStatus = :newOrderStatus where o.ucc = :ucc and o.orderId = :orderId and o.orderStatus in :orderStatuses")
    @Transactional
    Integer updateByUccAndOrderIdAndOrderStatusIn(
            @Param("ucc") UUID ucc,
            @Param("orderId") UUID orderId,
            @Param("orderStatuses") Collection<String> orderStatuses,
            @Param("newOrderStatus") String newOrderStatus
    );




    List<Order> findAllByOrderTypeAndOrderStatus(org.exchange.library.Enums.OrderType orderType, String orderStatus);



    @Modifying
    @Query("update Order o set o.orderStatus = 'PARTIALLY_FILLED' where o.orderStatus = 'OPEN'")
    void partiallyFillAllOpen();

    @Query("SELECT o from Order as o where o.ucc = :ucc order by o.orderPlacedAt desc")
    Optional<Page<Order>> findByUccWithPage(UUID ucc, PageRequest pageRequest);
}
