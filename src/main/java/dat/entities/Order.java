package dat.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dat.dtos.OrderDTO;
import dat.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity
@Setter
@EqualsAndHashCode(exclude = {"orderLines", "user"})
@JsonIgnoreProperties
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "order_date", nullable = false)
    private String orderDate;

    @Setter
    @Column(name = "order_price", nullable = false)
    private Double orderPrice;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<OrderLine> orderLines = new HashSet<>();

    // Helper methods for managing bidirectional relationship
    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setOrder(this);
    }

    public void removeOrderLine(OrderLine orderLine) {
        orderLines.remove(orderLine);
        orderLine.setOrder(null);
    }

    public void setOrderLines(Set<OrderLine> orderLines) {
        this.orderLines.clear();
        if (orderLines != null) {
            orderLines.forEach(this::addOrderLine);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(orderPrice, order.orderPrice) &&
                Objects.equals(user, order.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderDate, orderPrice, user);
    }

    public Order(OrderDTO orderDTO) {
        this.id = orderDTO.getId();
        this.orderDate = orderDTO.getOrderDate();
        this.orderPrice = orderDTO.getOrderPrice();

        if (orderDTO.getUser() != null) {
            UserDTO userDTO = orderDTO.getUser();
            this.user = new User(userDTO.getUsername(),
                    userDTO.getRoles().stream()
                            .map(r -> new dat.security.entities.Role(r))
                            .collect(Collectors.toSet()));
        }

        if (orderDTO.getOrderLines() != null) {
            orderDTO.getOrderLines().forEach(orderLineDTO -> {
                OrderLine orderLine = new OrderLine(orderLineDTO);
                this.addOrderLine(orderLine);
            });
        }
    }
}