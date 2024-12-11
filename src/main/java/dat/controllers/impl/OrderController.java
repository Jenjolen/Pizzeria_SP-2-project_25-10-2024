package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.OrderDAO;
import dat.daos.impl.OrderLineDAO;
import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.exceptions.ApiException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class OrderController implements IController<OrderDTO, Integer> {

    private final OrderDAO dao;
    private final OrderLineDAO orderLineDAO = OrderLineDAO.getInstance(HibernateConfig.getEntityManagerFactory());

    public OrderController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = OrderDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) throws ApiException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            OrderDTO orderDTO = dao.read(id);
            ctx.res().setStatus(200);
            ctx.json(orderDTO, OrderDTO.class);
        } catch (Exception e) {
            throw new ApiException(404, "Order not found");
        }
    }

    @Override
    public void readAll(Context ctx) {
        List<OrderDTO> orderDTOS = dao.readAll();
        ctx.res().setStatus(200);
        ctx.json(orderDTOS, OrderDTO.class);
    }

    @Override
    public void create(Context ctx) {
        OrderDTO jsonRequest = ctx.bodyAsClass(OrderDTO.class);
        OrderDTO orderDTO = dao.create(jsonRequest);
        ctx.res().setStatus(201);
        ctx.json(orderDTO, OrderDTO.class);
    }

    @Override
    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        OrderDTO orderDTO = ctx.bodyAsClass(OrderDTO.class);
        OrderDTO updatedOrderDTO = dao.update(id, orderDTO);
        ctx.res().setStatus(200);
        ctx.json(updatedOrderDTO, OrderDTO.class); // Ændret fra Order.class til OrderDTO.class
    }

    @Override
    public void delete(Context ctx) throws ApiException {
        try {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.res().setStatus(204);
        } catch (Exception e) {
            throw new ApiException(404, "Order not found");
        }
    }

    public void addOrderLine(Context ctx) throws ApiException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            OrderLineDTO orderLineDTO = ctx.bodyAsClass(OrderLineDTO.class);
            OrderLineDTO addedOrderLineDTO = orderLineDAO.create(orderLineDTO);
            OrderDTO orderDTO = dao.read(id);
            orderDTO.getOrderLines().add(orderLineDTO);
            dao.update(id, orderDTO);
            ctx.res().setStatus(201);
            ctx.json(orderDTO, OrderDTO.class);
        } catch (Exception e) {
            throw new ApiException(404, "Order for OrderLine not found");
        }
    }

    public void readOrderLine (Context ctx) {
        int orderLineId = Integer.parseInt(ctx.pathParam("id"));
        OrderLineDTO orderLineDTO = orderLineDAO.read(orderLineId);
        ctx.res().setStatus(200);
        ctx.json(orderLineDTO, OrderLineDTO.class);
    }

    public void readAllOrderLines (Context ctx) {
        List<OrderLineDTO> orderLineDTOS = orderLineDAO.readAll();
        ctx.res().setStatus(200);
        ctx.json(orderLineDTOS, OrderLineDTO.class);
    }

    public void readAllOrderLinesByOrder (Context ctx) {
        int orderId = Integer.parseInt(ctx.pathParam("id"));
        List<OrderLineDTO> orderLineDTOS = orderLineDAO.readAllOrderLinesByOrder(orderId);
        ctx.res().setStatus(200);
        ctx.json(orderLineDTOS, OrderLineDTO.class);
    }

    public void updateOrderLine(Context ctx) throws ApiException {
        int orderLineId = Integer.parseInt(ctx.pathParam("id"));
        OrderLineDTO orderLineDTO = orderLineDAO.update(orderLineId, ctx.bodyAsClass(OrderLineDTO.class));
        int orderId = orderLineDTO.getOrder().getId();
        OrderDTO orderDTO = dao.read(orderId);
        dao.update(orderId, orderDTO);
        ctx.res().setStatus(200);
        ctx.json(orderDTO, OrderDTO.class);
    }

    public void deleteOrderLine(Context ctx) throws ApiException {
        int orderLineId = Integer.parseInt(ctx.pathParam("id"));
        OrderLineDTO orderLineDTO = orderLineDAO.read(orderLineId);
        int orderId = orderLineDTO.getOrder().getId();
        OrderDTO orderDTO = dao.read(orderId);
        orderDTO.getOrderLines().removeIf(o -> orderLineDTO.getOrderLineId().equals(orderLineId)); // tjekker om orderLineId matcher et id i orderDTO - løber alle Order's orderlines
        dao.update(orderId, orderDTO);
        ctx.res().setStatus(204);
    }

}