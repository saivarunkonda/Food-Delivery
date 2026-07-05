package com.fooddelivery.repository;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    List<Order> findByRestaurantId(UUID restaurantId);
    
    List<Order> findByCustomerId(UUID customerId);
    
    List<Order> findByDriverId(UUID driverId);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.status IN :statuses")
    List<Order> findByRestaurantIdAndStatusIn(@Param("restaurantId") UUID restaurantId, 
                                               @Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate")
    Long countOrdersSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<Order> findPendingOrdersByStatus(@Param("status") OrderStatus status);
}
