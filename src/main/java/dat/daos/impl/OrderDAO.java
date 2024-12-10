package dat.daos.impl;

import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.entities.Order;
import dat.entities.OrderLine;
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
        try {
            em.getTransaction().begin();

            Order order = new Order(orderDTO);
            User user = em.find(User.class, orderDTO.getUser().getUsername());
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            order.setUser(user);

            // Handle OrderLines
            if (orderDTO.getOrderLines() != null) {
                order.getOrderLines().clear();
                for (OrderLineDTO lineDTO : orderDTO.getOrderLines()) {
                    OrderLine orderLine = new OrderLine(lineDTO);
                    orderLine.setOrder(order);
                    order.getOrderLines().add(orderLine);
                }
            }

            em.persist(order);
            em.getTransaction().commit();
            return new OrderDTO(order);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public OrderDTO read(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Order order = em.find(Order.class, id);
            return order != null ? new OrderDTO(order) : null;
        } finally {
            em.close();
        }
    }

    public List<OrderDTO> readAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
            List<Order> orders = query.getResultList();
            return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public OrderDTO update(Integer id, OrderDTO orderDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, id);
            if (order == null) {
                throw new IllegalArgumentException("Order not found");
            }

            order.setOrderDate(orderDTO.getOrderDate());
            order.setOrderPrice(orderDTO.getOrderPrice());

            // Update OrderLines
            order.getOrderLines().clear();
            if (orderDTO.getOrderLines() != null) {
                for (OrderLineDTO lineDTO : orderDTO.getOrderLines()) {
                    OrderLine orderLine = new OrderLine(lineDTO);
                    orderLine.setOrder(order);
                    order.getOrderLines().add(orderLine);
                }
            }

            order = em.merge(order);
            em.getTransaction().commit();
            return new OrderDTO(order);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, id);
            if (order != null) {
                em.remove(order);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean validatePrimaryKey(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Order.class, id) != null;
        } finally {
            em.close();
        }
    }
}