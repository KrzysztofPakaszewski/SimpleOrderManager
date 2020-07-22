package example.SimpleOrderManager.web.rest;

import example.SimpleOrderManager.domain.Order;
import example.SimpleOrderManager.service.OrderService;
import example.SimpleOrderManager.service.errors.EntityNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private final OrderService orderService;

    public OrderResource(OrderService orderService){
        this.orderService = orderService;
    }

    /**
     * {@code GET /order} : Get all orders
     *
     * @return {@link ResponseEntity}
     *      *          with status {@code 200 (ok)} and with body containing list of all orders
     */
    @GetMapping("/order")
    public ResponseEntity<List<Order>> getAllOrders(){
        log.debug("GET request for all orders");
        return ResponseEntity.ok().body(orderService.getAll());
    }

    /**
     * {@code POST /order} : create new order
     *
     * @param order the order to create
     * @return {@link ResponseEntity}
     *          with status {@code 201 (created)} and with body containing created order
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order)throws URISyntaxException {
        log.debug("POST request to create order: {}", order);
        Order result = orderService.createOrder(order);
        return ResponseEntity.created(new URI("/api/order/" + result.getId())).body(result);
    }

    /**
     * {@code PUT /order} : update existing order
     *
     * @param order the order to update
     * @return {@link ResponseEntity}
     *          with status {@code 200 (ok)} and with body containing created order or
     *          with status {@code 400 (bad_request)} if there is no order with given id
     */
    @PutMapping("/order")
    public ResponseEntity updateOrder(@RequestBody Order order){
        log.debug("PUT request to update order: {}", order);

        try {
            Order result = orderService.updateOrder(order);
            return ResponseEntity.ok().body(result);
        }catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * {@code GET /order/:id} : get specified order
     *
     * @param id the id of the order to find
     * @return {@link ResponseEntity}
     *      *          with status {@code 200 (ok)} and with body containing found order or
     *      *          with status {@code 400 (bad_request)} if there is no order with given id
     */
    @GetMapping("/order/{id}")
    public ResponseEntity getOrder(@PathVariable Long id){
        log.debug("GET request to get order with id: {}", id);
        try {
            Order result = orderService.getOrder(id);
            return ResponseEntity.ok().body(result);
        }catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * {@code DELETE /order/:id} : delete specified order
     *
     * @param id the id of the order to delete
     * @return {@link ResponseEntity}
     *              with status {@code 200 (ok)} if order was deleted
     *              with status {@code 400 (bad_request)} if order does not exists
     */
    @DeleteMapping("/order/{id}")
    public ResponseEntity deleteOrder(@PathVariable Long id){
        log.debug("DELETE request to delete order with id: {}", id);
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().body("Order deleted");
        }catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
