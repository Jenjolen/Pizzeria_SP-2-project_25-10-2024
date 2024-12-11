package dat.daos.impl;

import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.OrderLine;
import dat.exceptions.ApiException;
import dat.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDAO {
    private static OrderDAO instance;
    private final EntityManagerFactory emf;

    private OrderDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static OrderDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new OrderDAO(emf);
        }
        return instance;
    }

    public OrderDTO create(OrderDTO orderDTO) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Order order = new Order(orderDTO);
        User user = em.find(User.class, orderDTO.getUser().getUsername());
        order.setUser(user);
        em.persist(order);
        em.getTransaction().commit();
        em.close();
        return new OrderDTO(order);
    }

    public OrderDTO read(int id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Order order = em.find(Order.class, id);
            return order != null ? new OrderDTO(order) : null; // Returner OrderDTO
        } catch (Exception e) {
            throw new ApiException(404, "Order not found");
        }
    }

    public List<OrderDTO> readAll() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
        List<Order> orders = query.getResultList();
        em.close();
        return orders.stream().map(OrderDTO::new).collect(Collectors.toList()); // Konverter til OrderDTO
    }

    public OrderDTO update(Integer id, OrderDTO orderDTO) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Order order = em.find(Order.class, id);
        if (order != null) {
            order.setOrderDate(orderDTO.getOrderDate());
            order.setOrderPrice(orderDTO.getOrderPrice());
            order.getOrderLines().clear();
            for (OrderLineDTO orderLineDTO : orderDTO.getOrderLines()) {
                order.getOrderLines().add(new OrderLine(orderLineDTO));
            }
            User user = em.find(User.class, orderDTO.getUser().getUsername());
            order.setUser(user);
            em.merge(order);
        }
        em.getTransaction().commit();
        em.close();
        if (order == null) {
            return null;
        }
        return new OrderDTO(order);}

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Order order = em.find(Order.class, id);
        if (order != null) {
            if (order.getOrderLines() != null) {
                for (OrderLine orderLine : order.getOrderLines()) {
                    em.remove(orderLine); // Slet orderLine
                }
            }
            em.remove(order); // Slet order
        }
        em.getTransaction().commit();
        em.close();
    }

    public boolean validatePrimaryKey(Integer id) {
        EntityManager em = emf.createEntityManager();
        Order order = em.find(Order.class, id);
        em.close();
        return order != null; // Returner true, hvis order findes
    }

}