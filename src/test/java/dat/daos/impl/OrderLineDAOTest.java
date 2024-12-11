package dat.daos.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.dtos.PizzaDTO;
import dat.entities.Order;
import dat.entities.OrderLine;
import dat.entities.Pizza;
import dat.security.entities.Role;
import dat.security.entities.User;
import dat.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Set;

import static dat.entities.Pizza.PizzaType.FAMILY;
import static org.junit.jupiter.api.Assertions.*;

public class OrderLineDAOTest {

    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static OrderLineDAO orderLineDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static EntityManager entityManager = emf.createEntityManager();

    @BeforeAll
    public static void setUpClass() {
        orderLineDAO = OrderLineDAO.getInstance(emf);
    }

    @BeforeAll
    static void setUp() {
        entityManager.getTransaction().begin();
        // Step 1: Create and persist User entity
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setAge(25);
        Role role = new Role("USER");
        user.setRoles(Set.of(role));
        user.setGender("Undefined");

        // Persist the user
        entityManager.persist(user);

        // Step 2: Create and persist Pizza entity
        Pizza pizza = new Pizza();
        pizza.setName("Pepperoni");
        pizza.setDescription("Classic pepperoni pizza");
        pizza.setPrice(10.99);
        pizza.setToppings("Pepperoni, Tomato sauce, Mozzarella cheese");
        pizza.setPizzaType(Pizza.PizzaType.REGULAR);
        entityManager.persist(pizza);

        // Step 3: Create Order and set the User
        Order order = new Order();
        order.setOrderDate("2024-10-25");
        order.setOrderPrice(10.99);
        order.setUser(user);  // Assign the user here
        entityManager.persist(order);

        // Step 4: Create OrderLine and set Order and Pizza
        OrderLine orderLine = new OrderLine();
        orderLine.setOrder(order);  // Set the order
        orderLine.setPizza(pizza);  // Set the pizza
        orderLine.setQuantity(1);
        orderLine.setPrice(10.99);
        entityManager.persist(orderLine);
        order.setOrderLines(Set.of(orderLine));
        OrderDAO orderDAO = OrderDAO.getInstance(emf);
        for (OrderDTO o : OrderDAO.getInstance(emf).readAll()) {
            if (o.equals(new OrderDTO(order))) {
                orderDAO.update(o.getOrderId(), new OrderDTO(order));
            }

        }


        // Flush to ensure all entities are saved
        entityManager.getTransaction().commit();

    }

    @AfterAll
    public static void tearDown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM OrderLine").executeUpdate();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Pizza").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @AfterAll
    public static void tearDownClass() {
        emf.close();
    }

    @Test
    public void testCreateOrderLine() throws JsonProcessingException {
        EntityManager em = emf.createEntityManager();
        Pizza pizza = em.find(Pizza.class, 1);
        Order order = em.find(Order.class, 1);

        OrderLineDTO orderLineDTO = new OrderLineDTO(new OrderDTO(order), new PizzaDTO(pizza), 2, 150.0);
        OrderLineDTO createdOrderLine = orderLineDAO.create(orderLineDTO);
        OrderLine orderLine = new OrderLine(createdOrderLine);


        OrderLine dbOrderLine = em.find(OrderLine.class, createdOrderLine.getOrderLineId());
        assertEquals(dbOrderLine, orderLine);
        Order dbOrder = em.find(Order.class, orderLine.getOrder().getId());
        for (OrderLine o : dbOrder.getOrderLines()) {
            if (o.getId().equals(orderLine.getId())) {
                assertEquals(o, orderLine);
            }
    }

        em.close();
    }

    @Test
    public void testReadOrderLine() throws JsonProcessingException {

        OrderLineDTO ol = orderLineDAO.read(1);
        assertEquals(1, ol.getOrderLineId());
        assertEquals(1, ol.getOrder().getOrderId());
        assertEquals("Pepperoni", ol.getPizza().getName());
        assertEquals(1, ol.getQuantity());
        assertEquals(10.99, ol.getPrice());


    }

    @Test
    public void testReadAllOrderLines() {
        assertEquals(2, orderLineDAO.readAll().size());
        assertEquals("Pepperoni", orderLineDAO.readAll().get(0).getPizza().getName());
    }

    @Test
    public void testReadAllOrderLinesByOrder() {
        assertEquals(2, orderLineDAO.readAllOrderLinesByOrder(1).size());
        assertEquals("Pepperoni", orderLineDAO.readAllOrderLinesByOrder(1).get(0).getPizza().getName());
    }

    @Test
    public void testUpdateOrderLine()  {
        EntityManager em = emf.createEntityManager();
        Pizza pizza = em.find(Pizza.class, 1);
        Order order = em.find(Order.class, 1);

        // Create and update
        OrderLineDTO orderLineDTO = new OrderLineDTO(new OrderDTO(order), new PizzaDTO (pizza), 2, 150.0);
        OrderLineDTO createdOrderLine = orderLineDAO.create(orderLineDTO);
        createdOrderLine.setQuantity(3);
        createdOrderLine.setPrice(180.0);
        OrderLineDTO updatedOrderLine = orderLineDAO.update(createdOrderLine.getOrderLineId(), createdOrderLine);

        assertEquals(3, updatedOrderLine.getQuantity());
        assertEquals(180.0, updatedOrderLine.getPrice());
        assertEquals(orderLineDTO.getPizza().getName(), updatedOrderLine.getPizza().getName());


    }

    @Test
    public void testDeleteOrderLine() {
        OrderLineDTO orderLineDTO = orderLineDAO.read(1);
        assertNotNull(orderLineDTO);
        orderLineDAO.delete(orderLineDTO.getOrderLineId());
        assertNull(orderLineDAO.read(orderLineDTO.getOrderLineId()));
    }
}
