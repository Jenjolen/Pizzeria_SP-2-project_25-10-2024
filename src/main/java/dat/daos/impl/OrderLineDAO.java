package dat.daos.impl;

import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.OrderLine;
import dat.entities.Pizza;
import dat.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderLineDAO {

    private static OrderLineDAO instance;
    private final EntityManagerFactory emf;

    private OrderLineDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static OrderLineDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new OrderLineDAO(emf);
        }
        return instance;
    }

    public OrderLineDTO create(OrderLineDTO orderLineDTO) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Create a new OrderLine entity from the DTO
        OrderLine orderLine = new OrderLine(orderLineDTO);

        // Find the existing Order and Pizza entities
        Order order = em.find(Order.class, orderLineDTO.getOrder().getOrderId());
        Pizza pizza = em.find(Pizza.class, orderLineDTO.getPizza().getPizzaId());

        // Check if the Order and Pizza entities exist
        if (order == null || pizza == null) {
            em.getTransaction().rollback();
            em.close();
            throw new IllegalArgumentException("Order or Pizza not found");
        }

        // Set the Order and Pizza for the OrderLine
        orderLine.setOrder(order);
        orderLine.setPizza(pizza);

        // Add the new OrderLine to the Order's set of OrderLines
        Set<OrderLine> orderLines = order.getOrderLines();
        orderLines.add(orderLine);
        order.setOrderLines(orderLines);

        // Persist the new OrderLine and merge the updated Order
        em.persist(orderLine);
        em.merge(order);

        // Commit the transaction and close the EntityManager
        em.getTransaction().commit();
        em.close();

        // Return the created OrderLine as a DTO
        return new OrderLineDTO(orderLine);
    }

    public OrderLineDTO read(int id) {
        EntityManager em = emf.createEntityManager();
        OrderLine orderLine = em.find(OrderLine.class, id);
        em.close();
        return orderLine != null ? new OrderLineDTO(orderLine) : null; // Returner OrderLineDTO
    }

    public List <OrderLineDTO> readAll() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<OrderLine> query = em.createQuery("SELECT o FROM OrderLine o", OrderLine.class);
        List<OrderLine> orders = query.getResultList();
        em.close();
        return orders.stream().map(OrderLineDTO::new).collect(Collectors.toList()); // Konverter til OrderLineDTO
    }

    public List<OrderLineDTO> readAllOrderLinesByOrder(Integer orderId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<OrderLine> query = em.createQuery("SELECT o FROM OrderLine o WHERE o.order.id = :orderId", OrderLine.class);
        query.setParameter("orderId", orderId);
        List<OrderLine> orderLines = query.getResultList();
        em.close();
        return orderLines.stream().map(OrderLineDTO::new).collect(Collectors.toList());
    }

    public OrderLineDTO update(Integer id, OrderLineDTO orderLineDTO) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        OrderLine orderLine = em.find(OrderLine.class, id);
        if (orderLine != null) {
            orderLine.setOrder(new Order(orderLineDTO.getOrder()));
            orderLine.setPizza(new Pizza(orderLineDTO.getPizza()));
            orderLine.setQuantity(orderLineDTO.getQuantity());
            orderLine.setPrice(orderLineDTO.getPrice());
            em.merge(orderLine);
        }
        em.getTransaction().commit();
        em.close();
        return new OrderLineDTO(orderLine);}

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        OrderLine orderLine = em.find(OrderLine.class, id);
        if (orderLine != null) {
            em.remove(orderLine); // Slet orderLine
            for (OrderLine o : orderLine.getOrder().getOrderLines()) {
                if (o.getId().equals(orderLine.getId())) {
                    orderLine.getOrder().getOrderLines().remove(o);
                    break;
                }

            }
        }
        em.getTransaction().commit();
        em.close();
    }

    public boolean validatePrimaryKey(Integer id) {
        EntityManager em = emf.createEntityManager();
        OrderLine orderLine = em.find(OrderLine.class, id);
        em.close();
        return orderLine != null; // Returner true, hvis orderLine findes
    }





}
