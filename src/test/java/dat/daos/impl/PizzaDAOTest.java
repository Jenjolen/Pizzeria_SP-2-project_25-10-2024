package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.dtos.PizzaDTO;
import dat.entities.Pizza;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PizzaDAOTest {

    private static EntityManagerFactory emf;
    private static PizzaDAO dao;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = PizzaDAO.getInstance(emf);
        Populate populate = new Populate();
        populate.populate();
    }


    @BeforeEach
    void init() {

    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    @DisplayName("Find pizza by ID")
    void findById() {
        // Create a new PizzaDTO and persist it
        PizzaDTO pizzaDTO = new PizzaDTO("Pizza 6", "Description 5", "Pepperoni", 50.0, Pizza.PizzaType.REGULAR);
        PizzaDTO createdPizza = dao.create(pizzaDTO);

        // Ensure the createdPizza has a valid ID
        assertNotNull(createdPizza.getId(), "The created pizza should have a valid ID");

        // Retrieve the pizza by its ID
        PizzaDTO foundPizza = dao.read(createdPizza.getId());

        // Assert that the found pizza matches the created pizza
        assertEquals(createdPizza, foundPizza);
    }

    @Test
    @DisplayName("Create pizza")
    void create() {

        PizzaDTO pizzaDTO = new PizzaDTO("Pizza 4", "Description 4", "Cheese", 40.0, Pizza.PizzaType.REGULAR);
        PizzaDTO actual = dao.create(pizzaDTO);
        PizzaDTO expected = new PizzaDTO("Pizza 4", "Description 4", "Cheese", 40.0, Pizza.PizzaType.REGULAR);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update pizza")
    void update() {
        // Create a new PizzaDTO and persist it
        PizzaDTO pizzaDTO = new PizzaDTO("Pizza 4", "Description 4", "Cheese", 40.0, Pizza.PizzaType.FAMILY);
        PizzaDTO createdPizza = dao.create(pizzaDTO);

        // Ensure the createdPizza has a valid ID
        assertNotNull(createdPizza.getId(), "The created pizza should have a valid ID");

        // ACTUAL -> Update the name of the created pizza
        createdPizza.setName("Pizza 8");
        PizzaDTO updatedPizza = dao.update(createdPizza.getId(), createdPizza);

        // EXPECTED -> PizzaDTO after update
        PizzaDTO expected = new PizzaDTO("Pizza 8", "Description 4", "Cheese", 40.0, Pizza.PizzaType.FAMILY);

        // Assert that the updated pizza matches the expected pizza
        assertEquals(expected, updatedPizza);
    }

    @Test
    @DisplayName("Delete pizza")
    void delete() {
        // Create a new PizzaDTO and persist it
        PizzaDTO pizzaDTO = new PizzaDTO("Pizza 4", "Description 4", "Cheese", 40.0, Pizza.PizzaType.REGULAR);
        PizzaDTO createdPizza = dao.create(pizzaDTO);

        // Ensure the createdPizza has a valid ID
        assertNotNull(createdPizza.getId(), "The created pizza should have a valid ID");

        // Delete the created pizza
        dao.delete(createdPizza.getId());

        // Assert that the deleted pizza matches the created pizza
        //assertEquals(createdPizza, deletedPizza);

        // Verify that the pizza is actually removed from the database
        EntityManager em = emf.createEntityManager();
        Pizza pizza = em.find(Pizza.class, createdPizza.getId());
        assertNull(pizza);
        em.close();
    }

    @Test
    @DisplayName("Read all pizzas, from all Tests")
    void readAll() {
        assertEquals(2, dao.readAll().size());
    }

}