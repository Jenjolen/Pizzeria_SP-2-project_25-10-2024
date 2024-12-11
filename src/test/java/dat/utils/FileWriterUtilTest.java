package dat.utils;

import dat.dtos.OrderDTO;
import dat.dtos.OrderLineDTO;
import dat.dtos.PizzaDTO;
import dat.entities.Order;
import dat.entities.Pizza;
import dk.bugelhartmann.UserDTO;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileWriterUtilTest { // Kommentar så jeg kan commit :)

    @Test
    public void testSaveOrders() throws Exception {
        FileWriterUtil fileWriterUtil = new FileWriterUtil();
        List<OrderDTO> orders = new ArrayList<>();
        // Opret en UserDTO for at kunne bruge den i OrderDTO
        UserDTO user = new UserDTO("testUser", new HashSet<>());
        orders.add(new OrderDTO("2024-01-01", 100.0, user, new HashSet<>()));

        fileWriterUtil.saveOrders(orders);

        File file = new File("orders.json");
        assertTrue(file.exists(), "Orders file should exist");
    }

    @Test
    public void testSaveOrderLines() throws Exception {
        FileWriterUtil fileWriterUtil = new FileWriterUtil();
        List<OrderLineDTO> orderLines = new ArrayList<>();
        // Opret en Order og Pizza for at kunne bruge dem i OrderLineDTO
        Order order = new Order(); // Opret en dummy Order
        Pizza pizza = new Pizza(); // Opret en dummy Pizza
        orderLines.add(new OrderLineDTO(new OrderDTO (order), new PizzaDTO(pizza), 2, 20.0));

        fileWriterUtil.saveOrderLines(orderLines);

        File file = new File("order_lines.json");
        assertTrue(file.exists(), "Order lines file should exist");
    }

    @Test
    public void testSavePizzas() throws Exception {
        FileWriterUtil fileWriterUtil = new FileWriterUtil();
        List<PizzaDTO> pizzas = new ArrayList<>();
        // Opret en PizzaDTO med de nødvendige parametre
        pizzas.add(new PizzaDTO("Margherita", "Tomato and cheese", "Cheese", 10.0, Pizza.PizzaType.REGULAR));

        fileWriterUtil.savePizzas(pizzas);

        File file = new File("pizzas.json");
        assertTrue(file.exists(), "Pizzas file should exist");
    }
}