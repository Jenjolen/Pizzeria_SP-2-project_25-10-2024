package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Order;
import dk.bugelhartmann.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;  // Tilføj denne import


@Getter
@NoArgsConstructor
@Setter
@JsonIgnoreProperties
public class OrderDTO {
    private Integer orderId;
    private String orderDate;
    private Double orderPrice;
    private Set<OrderLineDTO> orderLines = new HashSet<>();
    private UserDTO user;

    public OrderDTO(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderPrice = order.getOrderPrice();
        this.user = new UserDTO(order.getUser().getUsername(), order.getUser().getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));

        if (order.getOrderLines() != null)
        {
            order.getOrderLines().forEach(orderLine -> this.orderLines.add(new OrderLineDTO(orderLine)));
        }
    }

    public OrderDTO(String orderDate, Double orderPrice, UserDTO user)
    {
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.user = user;

    }

    public OrderDTO(String orderDate, Double orderPrice, UserDTO user, Set<OrderLineDTO> orderLines) {
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.user = user;
        this.orderLines = orderLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDTO orderDTO)) return false;
        return Objects.equals(getOrderDate(), orderDTO.getOrderDate()) && Objects.equals(getOrderPrice(), orderDTO.getOrderPrice()) && Objects.equals(getOrderLines(), orderDTO.getOrderLines()) && Objects.equals(getUser(), orderDTO.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderDate(), getOrderPrice(), getOrderLines(), getUser());
    }
}