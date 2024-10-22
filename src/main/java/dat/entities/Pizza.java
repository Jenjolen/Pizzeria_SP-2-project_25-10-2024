package dat.entities;

import dat.dtos.PizzaDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Setter
@Table(name = "pizza")
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizza_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "toppings")
    private String toppings;

    @Setter
    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "pizza", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderList> orderLists = new HashSet<>();

    // Equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pizza pizza = (Pizza) o;
        return Objects.equals(name, pizza.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Pizza (PizzaDTO pizzaDTO) {
        this.id = pizzaDTO.getId();
        this.name = pizzaDTO.getName();
        this.description = pizzaDTO.getDescription();
        this.toppings = pizzaDTO.getTopping();
        this.price = pizzaDTO.getPrice();
    }

}