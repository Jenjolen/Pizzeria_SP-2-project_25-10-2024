package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.OrderLine;
import dat.entities.Pizza;
import dat.exceptions.ApiException;
import dat.security.entities.Role;
import dat.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderDAOTest {
    private EntityManagerFactory emf;
    private OrderDAO orderDAO;
    private User testUser;
    private Pizza testPizza;

    @BeforeAll
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        orderDAO = OrderDAO.getInstance(emf);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Ryd eksisterende data
            em.createQuery("DELETE FROM OrderLine").executeUpdate();
            em.createQuery("DELETE FROM Order").executeUpdate();
            em.createQuery("DELETE FROM Pizza").executeUpdate();
            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();

            // Opret basis test data
            Role userRole = new Role("USER");
            testUser = new User("testUser", "test123");
            testUser.addRole(userRole);

            testPizza = new Pizza();
            testPizza.setName("Test Pizza");
            testPizza.setPrice(100.0);
            testPizza.setDescription("Test Description");
            testPizza.setToppings("Test Toppings");
            testPizza.setPizzaType(Pizza.PizzaType.REGULAR);

            em.persist(userRole);
            em.persist(testUser);
            em.persist(testPizza);

            em.getTransaction().commit();
        }
    }

    private OrderDTO createTestOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderPrice(100.0);

        // Opret UserDTO
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        UserDTO userDTO = new UserDTO(testUser.getUsername(), roles);
        orderDTO.setUser(userDTO);

        // Opret OrderLines
        Set<OrderLineDTO> orderLines = new HashSet<>();
        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.setPizza(testPizza);
        orderLineDTO.setQuantity(1);
        orderLineDTO.setPrice(100.0);
        orderLines.add(orderLineDTO);

        orderDTO.setOrderLines(orderLines);

        return orderDTO;
    }

    @Test
    void testCreateOrder() throws ApiException {
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getOrderId());
        assertEquals(orderDTO.getOrderPrice(), createdOrder.getOrderPrice());
        assertEquals(orderDTO.getUser().getUsername(), createdOrder.getUser().getUsername());
        assertFalse(createdOrder.getOrderLines().isEmpty());
    }

    @Test
    void testReadOrder() throws ApiException {
        // Opret først en ordre
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Læs ordren
        OrderDTO readOrder = orderDAO.read(createdOrder.getOrderId());

        assertNotNull(readOrder);
        assertEquals(createdOrder.getOrderId(), readOrder.getOrderId());
        assertEquals(createdOrder.getOrderPrice(), readOrder.getOrderPrice());
        assertEquals(createdOrder.getUser().getUsername(), readOrder.getUser().getUsername());
    }

    @Test
    void testReadAllOrders() throws ApiException {
        // Opret to ordrer
        OrderDTO orderDTO1 = createTestOrderDTO();
        OrderDTO orderDTO2 = createTestOrderDTO();
        orderDAO.create(orderDTO1);
        orderDAO.create(orderDTO2);

        List<OrderDTO> orders = orderDAO.readAll();

        assertNotNull(orders);
        assertTrue(orders.size() >= 2);
    }

    @Test
    void testUpdateOrder() throws ApiException {
        // Opret en ordre
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Opdater ordren
        double newPrice = 200.0;
        createdOrder.setOrderPrice(newPrice);

        OrderDTO updatedOrder = orderDAO.update(createdOrder.getOrderId(), createdOrder);

        assertNotNull(updatedOrder);
        assertEquals(newPrice, updatedOrder.getOrderPrice());
    }

    @Test
    void testDeleteOrder() throws ApiException {
        // Opret en ordre
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Slet ordren
        orderDAO.delete(createdOrder.getOrderId());

        // Verificer at ordren er slettet
        assertThrows(ApiException.class, () -> orderDAO.read(createdOrder.getOrderId()));
    }

    @Test
    void testValidatePrimaryKey() throws ApiException {
        // Opret en ordre
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Test at primary key validering virker
        assertTrue(orderDAO.validatePrimaryKey(createdOrder.getOrderId()));
        assertFalse(orderDAO.validatePrimaryKey(-1));
    }

    @AfterAll
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM OrderLine").executeUpdate();
            em.createQuery("DELETE FROM Order").executeUpdate();
            em.createQuery("DELETE FROM Pizza").executeUpdate();
            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
        if (emf != null) {
            emf.close();
        }
    }
}