package dat.daos.impl;

import dat.config.Populate;
import dat.daos.IDAO;
import dat.dtos.PizzaDTO;
import dat.entities.Pizza;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PizzaDAO implements IDAO<PizzaDTO, Integer> {
    private static PizzaDAO instance;
    private static EntityManagerFactory emf;

    public static PizzaDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PizzaDAO();
        }
        return instance;
    }

    @Override
    public PizzaDTO read(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Pizza pizza = em.find(Pizza.class, integer);
            return new PizzaDTO(pizza);
        }
    }

    @Override
    public List<PizzaDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<PizzaDTO> query = em.createQuery("SELECT new dat.dtos.PizzaDTO(p) FROM Pizza p", PizzaDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public PizzaDTO create(PizzaDTO pizzaDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Pizza pizza = new Pizza(pizzaDTO);
            em.persist(pizza);
            em.getTransaction().commit();
            return new PizzaDTO(pizza);
        }
    }

//    public List<PizzaDTO> populate () {
//        try (EntityManager em = emf.createEntityManager()) {
//            Populate populator = new Populate();
//            populator.populate();
//
//
//        }
//
//    }

    @Override
    public PizzaDTO update(Integer integer, PizzaDTO pizzaDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Pizza p = em.find(Pizza.class, integer);
            p.setName(pizzaDTO.getName());
            p.setDescription(pizzaDTO.getDescription());
            p.setToppings(pizzaDTO.getToppings());
            p.setPrice(pizzaDTO.getPrice());
            p.setPizzaType(pizzaDTO.getPizzaType());
            Pizza mergedPizza = em.merge(p);
            em.getTransaction().commit();
            return mergedPizza != null ? new PizzaDTO(mergedPizza) : null;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Pizza pizza = em.find(Pizza.class, integer);
            if (pizza != null) {
                em.remove(pizza);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Pizza pizza = em.find(Pizza.class, integer);
            return pizza != null;
        }
    }
}
