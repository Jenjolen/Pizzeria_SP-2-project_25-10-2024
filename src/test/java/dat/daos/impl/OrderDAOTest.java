package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.Pizza;
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

class OrderDAOTest {
    private static EntityManagerFactory emf;
    private static OrderDAO orderDAO;
    private static EntityManager em;
    private static User testUser;
    private static Pizza testPizza;

    @BeforeAll
    static void setUpClass() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        orderDAO = OrderDAO.getInstance(emf);
        em = emf.createEntityManager();
    }

    @BeforeEach
    void setUp() {
        em.getTransaction().begin();

        // Create and persist Role
        Role userRole = new Role("USER");
        em.persist(userRole);

        // Create and persist User with all required fields
        testUser = new User("testUser", "password123");
        testUser.addRole(userRole);
        testUser.setAge(25); // Setting required age
        testUser.setEmail("test@test.com"); // Setting required email
        testUser.setGender("Male"); // Setting required gender
        em.persist(testUser);

        // Create and persist Pizza
        testPizza = new Pizza();
        testPizza.setName("Test Pizza");
        testPizza.setDescription("Test Description");
        testPizza.setPrice(100.0);
        testPizza.setToppings("Cheese, Tomato");
        testPizza.setPizzaType(Pizza.PizzaType.REGULAR);
        em.persist(testPizza);

        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM OrderLine").executeUpdate();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Pizza").executeUpdate();
        em.createQuery("DELETE FROM User_roles").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Role").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterAll
    static void tearDownClass() {
        em.close();
        emf.close();
    }

    @Test
    void testCreateOrder() {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();

        // Act
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Assert
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals(orderDTO.getOrderDate(), createdOrder.getOrderDate());
        assertEquals(orderDTO.getOrderPrice(), createdOrder.getOrderPrice());
        assertEquals(orderDTO.getUser().getUsername(), createdOrder.getUser().getUsername());
    }

    @Test
    void testReadOrder() {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Act
        OrderDTO readOrder = orderDAO.read(createdOrder.getId());

        // Assert
        assertNotNull(readOrder);
        assertEquals(createdOrder.getId(), readOrder.getId());
        assertEquals(createdOrder.getOrderDate(), readOrder.getOrderDate());
        assertEquals(createdOrder.getOrderPrice(), readOrder.getOrderPrice());
    }

    @Test
    void testReadAllOrders() {
        // Arrange
        OrderDTO orderDTO1 = createTestOrderDTO();
        OrderDTO orderDTO2 = createTestOrderDTO();
        orderDAO.create(orderDTO1);
        orderDAO.create(orderDTO2);

        // Act
        List<OrderDTO> orders = orderDAO.readAll();

        // Assert
        assertNotNull(orders);
        assertTrue(orders.size() >= 2);
    }

    @Test
    void testUpdateOrder() {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Modify order
        createdOrder.setOrderPrice(200.0);
        createdOrder.setOrderDate("2024-01-21");

        // Act
        OrderDTO updatedOrder = orderDAO.update(createdOrder.getId(), createdOrder);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(200.0, updatedOrder.getOrderPrice());
        assertEquals("2024-01-21", updatedOrder.getOrderDate());
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        OrderDTO orderDTO = createTestOrderDTO();
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Act
        orderDAO.delete(createdOrder.getId());

        // Assert
        OrderDTO deletedOrder = orderDAO.read(createdOrder.getId());
        assertNull(deletedOrder);
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