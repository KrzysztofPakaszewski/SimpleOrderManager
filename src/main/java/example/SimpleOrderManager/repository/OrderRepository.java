package example.SimpleOrderManager.repository;

import example.SimpleOrderManager.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOneById(Long id);
}
