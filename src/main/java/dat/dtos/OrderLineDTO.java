package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.OrderLine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties
public class OrderLineDTO {

    private Integer orderLineId;
    private OrderDTO order; // Use OrderDTO instead of Order
    private PizzaDTO pizza; // Use PizzaDTO instead of Pizza
    private Integer quantity;
    private Double price;

    public OrderLineDTO(OrderDTO order, PizzaDTO pizza, Integer quantity, Double price) {
        this.order = order;
        this.pizza = pizza;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderLineDTO(OrderLine orderLine) {
        this.orderLineId = orderLine.getId();
        this.order = orderLine.getOrder() != null ? new OrderDTO(orderLine.getOrder()) : null; // Convert to DTO
        this.pizza = orderLine.getPizza() != null ? new PizzaDTO(orderLine.getPizza()) : null; // Convert to DTO
        this.quantity = orderLine.getQuantity();
        this.price = orderLine.getPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLineDTO that)) return false;
        return Objects.equals(getOrder(), that.getOrder()) &&
                Objects.equals(getPizza(), that.getPizza()) &&
                Objects.equals(getQuantity(), that.getQuantity()) &&
                Objects.equals(getPrice(), that.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrder(), getPizza(), getQuantity(), getPrice());
    }
}
