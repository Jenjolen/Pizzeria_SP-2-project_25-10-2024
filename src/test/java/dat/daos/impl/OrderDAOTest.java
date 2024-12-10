package dat.daos.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.Pizza;
import dat.security.entities.Role;
import dat.security.entities.User;
import dat.security.entities.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class OrderDAOTest {

    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    static OrderDAO orderDAO = OrderDAO.getInstance(emf);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static EntityManager entityManager = emf.createEntityManager();

    @BeforeEach
    void setUp() {
        Populate populate = new Populate();
        populate.populate();

    }

    @AfterEach
    void tearDown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM OrderLine").executeUpdate();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Pizza").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    void create() {
        User user = new User();
        user.setUsername("testUser");
        user.setGender("Undefined");
        user.setEmail("test@123");
        user.setPassword("password123");
        user.setAge(25);
        user.setRoles(Set.of(new Role("USER")));
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUser(new UserDTO(user));
        orderDTO.setOrderPrice(10.99);
        orderDTO.setOrderDate("2021-12-12");
        orderDTO.setOrderLines(Set.of(new OrderLineDTO(new Order(orderDTO), new Pizza("Olive", "Lots of green yummy olives", "Tomato, green olives, parmesan cheese", 3.49, Pizza.PizzaType.REGULAR), 1, 10.99)));
        OrderDTO orderDTO1 = orderDAO.create(orderDTO);

        assertEquals(orderDTO1.getOrderPrice(), orderDTO.getOrderPrice());
        assertEquals(orderDTO1.getOrderDate(), orderDTO.getOrderDate());
        assertEquals(orderDTO1.getUser().getUsername(), orderDTO.getUser().getUsername());
        assertEquals(orderDTO1.getOrderLines().size(), orderDTO.getOrderLines().size());


    }

    @Test
    void read() {
    }

    @Test
    void readAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}