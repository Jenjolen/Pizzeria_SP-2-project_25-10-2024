package dat.service;

import dat.daos.impl.PizzaDAO;
import dat.dtos.PizzaDTO;
import dat.entities.Pizza;

import java.util.List;

public class PizzaService {

    private final PizzaDAO pizzaDAO;

    public PizzaService(PizzaDAO pizzaDAO) {
        this.pizzaDAO = pizzaDAO;
    }

    // Create a new Pizza entity from PizzaDTO
    public Pizza createPizza(PizzaDTO pizzaDTO) {
        Pizza pizza = new Pizza();
        pizza.setName(pizzaDTO.getName());
        pizza.setDescription(pizzaDTO.getDescription());
        pizza.setToppings(pizzaDTO.getToppings());
        pizza.setPrice(pizzaDTO.getPrice());
        return pizza;
    }

    // Save Pizza entity to the database
    public PizzaDTO savePizza(PizzaDTO pizzaDTO) {
        return pizzaDAO.create(pizzaDTO);
    }

    // Get a Pizza by ID
    public PizzaDTO getPizzaById(Integer id) {
        return pizzaDAO.read(id);
    }

    // Get all pizzas
    public List<PizzaDTO> getAllPizzas() {
        return pizzaDAO.readAll();
    }
}
