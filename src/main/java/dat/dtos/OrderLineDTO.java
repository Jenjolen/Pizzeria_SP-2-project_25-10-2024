package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Order;
import dat.entities.OrderLine;
import dat.entities.Pizza;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLineDTO that)) return false;
        return Objects.equals(getOrder(), that.getOrder()) && Objects.equals(getPizza(), that.getPizza()) && Objects.equals(getQuantity(), that.getQuantity()) && Objects.equals(getPrice(), that.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrder(), getPizza(), getQuantity(), getPrice());
    }
}
