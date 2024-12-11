package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Pizza;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Setter
@JsonIgnoreProperties

public class PizzaDTO{
    private Integer pizzaId;
    private String name;
    private String description;
    private String toppings;
    private Double price;
    private Pizza.PizzaType pizzaType;


    public PizzaDTO(Pizza pizza){
        this.pizzaId = pizza.getId();
        this.name = pizza.getName();
        this.description = pizza.getDescription();
        this.toppings = pizza.getToppings();
        this.price = pizza.getPrice();
        this.pizzaType = pizza.getPizzaType();


    }

    public PizzaDTO(String name, String description, String topping, Double price, Pizza.PizzaType pizzaType)
    {
        this.name = name;
        this.description = description;
        this.toppings = topping;
        this.price = price;
        this.pizzaType = pizzaType;
    }

    public static List<PizzaDTO> toPizzaDTOList(List<Pizza> pizzas) {
        return pizzas.stream().map(PizzaDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PizzaDTO pizzaDTO)) return false;
        return Objects.equals(getName(), pizzaDTO.getName()) && Objects.equals(getDescription(), pizzaDTO.getDescription()) && Objects.equals(getToppings(), pizzaDTO.getToppings()) && Objects.equals(getPrice(), pizzaDTO.getPrice()) && getPizzaType() == pizzaDTO.getPizzaType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getToppings(), getPrice(), getPizzaType());
    }
}
