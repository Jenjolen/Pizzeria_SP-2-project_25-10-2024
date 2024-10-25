package dat.dtos;


import dat.entities.Order;
import dat.entities.OrderLine;
import dat.entities.Pizza;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class OrderLineDTO {

    private Integer id;
    private Order order;
    private Pizza pizza;
    private Integer quantity;
    private Double price;

    public OrderLineDTO(Order order, Pizza pizza, Integer quantity, Double price) {
        this.order = order;
        this.pizza = pizza;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderLineDTO(OrderLine orderLine) {
        this.id = orderLine.getId();
        this.order = orderLine.getOrder();
        this.pizza = orderLine.getPizza();
        this.quantity = orderLine.getQuantity();
        this.price = orderLine.getPrice();
    }


}
