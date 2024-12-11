package dat.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.dtos.OrderLineDTO;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    public OrderLine(Order order, Pizza pizza, Integer quantity, Double price) {
        this.order = order;
        this.pizza = pizza;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderLine(OrderLineDTO orderLineDTO) {
        this.id = orderLineDTO.getOrderLineId();
        this.order = orderLineDTO.getOrder();
        this.pizza = orderLineDTO.getPizza();
        this.quantity = orderLineDTO.getQuantity();
        this.price = orderLineDTO.getPrice();
    }
}