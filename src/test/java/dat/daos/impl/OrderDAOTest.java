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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderDAOTest {
    private static EntityManagerFactory emf;
    private static OrderDAO orderDAO;
    private static EntityManager em;

    @BeforeAll
    static void setUpClass() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        orderDAO = OrderDAO.getInstance(emf);
        em = emf.createEntityManager();
    }

    @BeforeEach
    void setUp() {
        em.getTransaction().begin();
        // Create test user
        User user = new User("testUser", "password123");
        Role role = new Role("USER");
        user.setRoles(Set.of(role));

        // Create test pizza
        Pizza pizza = new Pizza();
        pizza.setName("Test Pizza");
        pizza.setDescription("Test Description");
        pizza.setPrice(100.0);
        pizza.setPizzaType(Pizza.PizzaType.REGULAR);

        // Persist entities
        em.persist(role);
        em.persist(user);
        em.persist(pizza);

        // Create test order
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate("2024-01-20");
        orderDTO.setOrderPrice(100.0);
        orderDTO.setUser(new UserDTO(user.getUsername(), user.getRoles().stream()
                .map(Role::getRoleName).collect(java.util.stream.Collectors.toSet())));

        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM OrderLine").executeUpdate();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Pizza").executeUpdate();
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
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate("2024-01-20");
        orderDTO.setOrderPrice(100.0);
        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", "testUser")
                .getSingleResult();
        orderDTO.setUser(new UserDTO(user.getUsername(), user.getRoles().stream()
                .map(Role::getRoleName).collect(java.util.stream.Collectors.toSet())));

        // Act
        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(orderDTO.getOrderDate(), createdOrder.getOrderDate());
        assertEquals(orderDTO.getOrderPrice(), createdOrder.getOrderPrice());
        assertEquals(orderDTO.getUser().getUsername(), createdOrder.getUser().getUsername());
    }

    @Test
    void testReadOrder() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate("2024-01-20");
        orderDTO.setOrderPrice(100.0);
        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", "testUser")
                .getSingleResult();
        orderDTO.setUser(new UserDTO(user.getUsername(), user.getRoles().stream()
                .map(Role::getRoleName).collect(java.util.stream.Collectors.toSet())));

        OrderDTO createdOrder = orderDAO.create(orderDTO);

        // Act
        OrderDTO readOrder = orderDAO.read(createdOrder.getId());

        // Assert
        assertNotNull(readOrder);
        assertEquals(createdOrder.getId(), readOrder.getId());
        assertEquals(createdOrder.getOrderDate(), readOrder.getOrderDate());
        assertEquals(createdOrder.getOrderPrice(), readOrder.getOrderPrice());
    }
}