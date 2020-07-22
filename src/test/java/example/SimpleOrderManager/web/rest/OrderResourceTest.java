package example.SimpleOrderManager.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.SimpleOrderManager.domain.Order;
import example.SimpleOrderManager.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class OrderResourceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Order first, second, additional;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        first = new Order("Water","Jan","Kowalski", Date.valueOf(LocalDate.now().minusMonths(3)));
        second = new Order("Pizza","Piotr","Nowak", Date.valueOf(LocalDate.now().minusDays(32)));
        additional = new Order("Bread","Jan","Kowalski", Date.valueOf(LocalDate.now().minusDays(12)));

        orderRepository.save(first);
        orderRepository.save(second);
    }

    @Test
    void testGetAllOrders() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasSize(2)))
                .andExpect(jsonPath("$.[*].order_name").value(hasItems(first.getOrder_name(), second.getOrder_name())))
                .andExpect(jsonPath("$.[*].buyer_name").value(hasItems(first.getBuyer_name(), second.getBuyer_name())))
                .andExpect(jsonPath("$.[*].buyer_surname").value(hasItems(first.getBuyer_surname(), second.getBuyer_surname())))
                .andExpect(jsonPath("$.[*].date").value(hasItems(first.getDate().toString(), second.getDate().toString())));
    }

    @Test
    void testCreateOrderWithoutId() throws Exception{
        mvc.perform(MockMvcRequestBuilders
                .post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(additional)))
                .andExpect(status().isCreated());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList.size()).isEqualTo(3);
        Order testOrder = orderList.get(orderList.size() -1);
        assertThat(testOrder.getBuyer_name()).isEqualTo(additional.getBuyer_name());
        assertThat(testOrder.getBuyer_surname()).isEqualTo(additional.getBuyer_surname());
        assertThat(testOrder.getOrder_name()).isEqualTo(additional.getOrder_name());
        assertThat(testOrder.getDate()).isEqualTo(additional.getDate());
    }

    @Test
    void testCreateOrderWithAlreadyUsedId() throws Exception{
        additional.setId(first.getId());
        mvc.perform(MockMvcRequestBuilders
                .post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(additional)))
                .andExpect(status().isCreated());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList.size()).isEqualTo(3);
        Order testOrder = orderList.get(2);
        assertThat(testOrder.getBuyer_name()).isEqualTo(additional.getBuyer_name());
        assertThat(testOrder.getBuyer_surname()).isEqualTo(additional.getBuyer_surname());
        assertThat(testOrder.getOrder_name()).isEqualTo(additional.getOrder_name());
        assertThat(testOrder.getDate()).isEqualTo(additional.getDate());
        // id should be ignored
        assertThat(testOrder.getId()).isNotEqualTo(additional.getId());
    }

    @Test
    void testUpdateOrderWithCorrectId() throws Exception{
        // updating 'first' order
        additional.setId(first.getId());

        mvc.perform(MockMvcRequestBuilders
                .put("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(additional)))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                .get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasSize(2)))
                .andExpect(jsonPath("$.[*].order_name").value(hasItems(additional.getOrder_name(), second.getOrder_name())))
                .andExpect(jsonPath("$.[*].buyer_name").value(hasItems(additional.getBuyer_name(), second.getBuyer_name())))
                .andExpect(jsonPath("$.[*].buyer_surname").value(hasItems(additional.getBuyer_surname(), second.getBuyer_surname())))
                .andExpect(jsonPath("$.[*].date").value(hasItems(additional.getDate().toString(), second.getDate().toString())));

    }

    @Test
    void testUpdateOrderWithNotExistingId() throws Exception{
        mvc.perform(MockMvcRequestBuilders
                .put("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(additional)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOrderWithCorrectId() throws Exception{
        mvc.perform(MockMvcRequestBuilders
                .get("/api/order/{id}", first.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(first.getId()))
                .andExpect(jsonPath("$.order_name").value(first.getOrder_name()))
                .andExpect(jsonPath("$.buyer_name").value(first.getBuyer_name()))
                .andExpect(jsonPath("$.buyer_surname").value(first.getBuyer_surname()))
                .andExpect(jsonPath("$.date").value(first.getDate().toString()));
    }

    @Test
    void testGetOrderWithNotExistingId() throws Exception{
        mvc.perform(MockMvcRequestBuilders
                .get("/api/order/{id}", additional.getId() )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOrderWithCorrectId() throws Exception{
        mvc.perform(MockMvcRequestBuilders
                .delete("/api/order/{id}", second.getId()))
                .andExpect(status().isOk());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList.size()).isEqualTo(1);
        Order testOrder = orderList.get(0);
        assertThat(testOrder.getId()).isEqualTo(first.getId());
        assertThat(testOrder.getOrder_name()).isEqualTo(first.getOrder_name());
    }

    @Test
    void testDeleteOrderWithNotExistingId() throws Exception{
        mvc.perform(MockMvcRequestBuilders
                .delete("/api/order/{id}", additional.getId() )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}