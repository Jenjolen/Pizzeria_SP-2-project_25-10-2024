package dat.entities;

import dat.dtos.OrderDTO;
import dat.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderLine> orderLines = new HashSet<>();

    // Equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderDate, order.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderDate);
    }

    public Order (OrderDTO orderDTO) {
        this.id = orderDTO.getId();
        this.orderDate = orderDTO.getOrderDate();
        this.orderPrice = orderDTO.getOrderPrice();
        UserDTO userDTO = orderDTO.getUser();
        this.user = new User(userDTO.getUsername(), userDTO.getRoles().stream().map(r -> new dat.security.entities.Role(r)).collect(Collectors.toSet()));
        this.orderLines = orderDTO.getOrderLines().stream().map(orderLineDTO -> new OrderLine(orderLineDTO)).collect(Collectors.toSet());
    }

}