package example.SimpleOrderManager.service;

import example.SimpleOrderManager.domain.Order;
import example.SimpleOrderManager.repository.OrderRepository;
import example.SimpleOrderManager.service.errors.EntityNotFound;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }


    public List<Order> getAll(){
        return orderRepository.findAll();
    }


    // creating copy of order, ignoring id
    public Order createOrder(Order order){
        Order createdOrder = new Order(order.getOrder_name(), order.getBuyer_name(),
                order.getBuyer_surname(), order.getDate());
        return orderRepository.save(createdOrder);
    }

    // updates row with the same id as order
    // if there is no such row, throws Entity not found
    public Order updateOrder(Order order){
        if(orderRepository.findOneById(order.getId()).isPresent()){
            return orderRepository.save(order);
        }else{
            throw new EntityNotFound("Order", order.getId());
        }
    }

    public Order getOrder(Long id){
        return orderRepository.findOneById(id).orElseThrow(
                () -> new EntityNotFound("Order", id)
        );
    }

    public void deleteOrder(Long id){
        if(orderRepository.findOneById(id).isPresent()) {
            orderRepository.deleteById(id);
        }else{
            throw new EntityNotFound("Order", id);
        }
    }
}
