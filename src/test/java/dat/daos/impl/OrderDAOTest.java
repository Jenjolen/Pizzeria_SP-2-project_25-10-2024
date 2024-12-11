package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.Pizza;
import dat.exceptions.ApiException;
import dat.security.entities.Role;
import dat.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderDAOTest {
    private static EntityManagerFactory emf;
    private static OrderDAO orderDAO;
    private static EntityManager em;
    private static User testUser;
    private static Pizza testPizza;
    private static Order testOrder;
    private static OrderLineDTO testOrderLineDTO;

    @BeforeAll
    static void setUpClass() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        orderDAO = OrderDAO.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Opretter og gemmer en Role
            Role userRole = new Role("user");
            em.persist(userRole);

            // Opretter og gemmer en User med alle påkrævede felter
            testUser = new User();
            testUser.setUsername("testUser");
            testUser.setPassword("test123");
            testUser.setAge(25);  // Tilføjet age
            testUser.setEmail("test@test.com");  // Tilføjet email
            testUser.setGender("M");  // Tilføjet gender
            testUser.addRole(userRole);
            em.persist(testUser);

            // Opretter og gemmer en Pizza
            testPizza = new Pizza();
            testPizza.setName("Test Pizza");
            testPizza.setDescription("Test Description");
            testPizza.setPrice(100.0);
            testPizza.setToppings("Test Toppings");
            testPizza.setPizzaType(Pizza.PizzaType.REGULAR);
            em.persist(testPizza);



            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    void tearDown() {
        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM OrderLine").executeUpdate();
            em.createQuery("DELETE FROM Order").executeUpdate();
            em.createQuery("DELETE FROM Pizza").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    static void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    @DisplayName("Create order test")
    void createOrder() {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();

        // Act
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Assert
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getOrderId());
        assertEquals(orderDTO.getOrderDate(), createdOrder.getOrderDate());
        assertEquals(orderDTO.getOrderPrice(), createdOrder.getOrderPrice());
        assertEquals(orderDTO.getUser().getUsername(), createdOrder.getUser().getUsername());
        assertFalse(createdOrder.getOrderLines().isEmpty());
    }

    @Test
    @DisplayName("Read order test")
    void readOrder() throws ApiException {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Act
        OrderDTO readOrder = orderDAO.read(createdOrder.getOrderId());

        // Assert
        assertNotNull(readOrder);
        assertEquals(createdOrder.getOrderId(), readOrder.getOrderId());
        assertEquals(createdOrder.getOrderDate(), readOrder.getOrderDate());
        assertEquals(createdOrder.getOrderPrice(), readOrder.getOrderPrice());
        assertEquals(createdOrder.getUser().getUsername(), readOrder.getUser().getUsername());
    }

    @Test
    @DisplayName("Update order test")
    void updateOrder() {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Act
        createdOrder.setOrderPrice(200.0);
        OrderDTO updatedOrder = orderDAO.update(createdOrder.getOrderId(), createdOrder);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(createdOrder.getOrderId(), updatedOrder.getOrderId());
        assertEquals(200.0, updatedOrder.getOrderPrice());
    }

    @Test
    @DisplayName("Delete order test")
    void deleteOrder() throws ApiException {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Act
        orderDAO.delete(createdOrder.getOrderId());

        // Assert
        OrderDTO deletedOrder = orderDAO.read(createdOrder.getOrderId());
        assertNull(deletedOrder);
    }

    @Test
    @DisplayName("Read all orders test")
    void readAllOrders() {
        // Arrange
        OrderDTO orderDTO1 = createTestOrderDTO();
        OrderDTO orderDTO2 = createTestOrderDTO();
        orderDAO.create(orderDTO1);
        orderDAO.create(orderDTO2);

        // Act
        List<OrderDTO> orders = orderDAO.readAll();

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    private OrderDTO createTestOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate("2024-01-20");
        orderDTO.setOrderPrice(100.0);

        UserDTO userDTO = new UserDTO(testUser.getUsername(),
                testUser.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()));
        orderDTO.setUser(userDTO);

        Set<OrderLineDTO> orderLines = new HashSet<>();
        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.setPizza(testPizza);
        orderLineDTO.setQuantity(1);
        orderLineDTO.setPrice(100.0);
        orderLines.add(orderLineDTO);

        orderDTO.setOrderLines(orderLines);

        return orderDTO;
    }
}