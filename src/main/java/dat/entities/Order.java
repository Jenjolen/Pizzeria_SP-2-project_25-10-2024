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

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private Set<OrderLine> orderLines = new HashSet<>();

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



    public Order (OrderDTO orderDTO) {
        this.id = orderDTO.getId();
        this.orderDate = orderDTO.getOrderDate();
        this.orderPrice = orderDTO.getOrderPrice();
        UserDTO userDTO = orderDTO.getUser();
        this.user = new User(userDTO.getUsername(), userDTO.getRoles().stream().map(r -> new dat.security.entities.Role(r)).collect(Collectors.toSet()));
        this.orderLines = orderDTO.getOrderLines().stream().map(orderLineDTO -> new OrderLine(orderLineDTO)).collect(Collectors.toSet());
    }

}