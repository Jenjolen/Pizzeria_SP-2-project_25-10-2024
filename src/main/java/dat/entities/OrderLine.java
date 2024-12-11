package dat.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.dtos.OrderLineDTO;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Setter
@EqualsAndHashCode(exclude = {"order", "pizza"})
@JsonIgnoreProperties
@Table(name = "orderline")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @JoinColumn(name = "pizza_id", nullable = false)
    @ManyToOne
    private Pizza pizza;

    @Setter
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Setter
    @Column(name = "price", nullable = false)
    private Double price;

    public OrderLine(Pizza pizza, Integer quantity, Double price) {
        this.pizza = pizza;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderLine(OrderLineDTO orderLineDTO) {
        this.id = orderLineDTO.getOrderLineId();
        this.pizza = new Pizza(orderLineDTO.getPizza());
        this.quantity = orderLineDTO.getQuantity();
        this.price = orderLineDTO.getPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine)) return false;
        OrderLine that = (OrderLine) o;
        return Objects.equals(pizza, that.pizza) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pizza, quantity, price);
    }


}