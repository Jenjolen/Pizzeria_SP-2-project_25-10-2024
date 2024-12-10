package dat.controllers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.controllers.IController;
import dat.daos.impl.PizzaDAO;
import dat.dtos.PizzaDTO;
import dat.entities.Pizza;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class PizzaController implements IController<PizzaDTO, Integer> {

    private final PizzaDAO dao;
    private final Populate populateService;

    public PizzaController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = PizzaDAO.getInstance(emf);
        this.populateService = new Populate();
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        PizzaDTO pizzaDTO = dao.read(id);
        ctx.res().setStatus(200);
        ctx.json(pizzaDTO, PizzaDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        List<PizzaDTO> pizzaDTOS = dao.readAll();
        ctx.res().setStatus(200);
        ctx.json(pizzaDTOS, PizzaDTO.class);
    }

    @Override
    public void create(Context ctx) {
        PizzaDTO jsonRequest = validateEntity(ctx);
        PizzaDTO pizzaDTO = dao.create(jsonRequest);
        ctx.res().setStatus(201);
        ctx.json(pizzaDTO, PizzaDTO.class);
    }

    public void createMultiple(Context ctx) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<PizzaDTO> pizzaDTOS;
        try {
            pizzaDTOS = objectMapper.readValue(ctx.body(), new TypeReference<List<PizzaDTO>>() {});
        } catch (Exception e) {
            ctx.res().setStatus(400);
            ctx.json(Map.of("error", "Invalid JSON format"));
            return;
        }

        for (PizzaDTO pizzaDTO : pizzaDTOS) {
            dao.create(pizzaDTO);
        }

        ctx.res().setStatus(201);
        ctx.json(pizzaDTOS);
    }

    public void populate(Context ctx) {
        populateService.populate();
        List<PizzaDTO> pizzaDTOS = dao.readAll();
        ctx.res().setStatus(200);
        ctx.json(pizzaDTOS);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        PizzaDTO pizzaDTO = dao.update(id, validateEntity(ctx));
        ctx.res().setStatus(200);
        ctx.json(pizzaDTO, Pizza.class);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        dao.delete(id);
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public PizzaDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(PizzaDTO.class)
                .check(p -> p.getName() != null && !p.getName().isEmpty(), "Pizza name must be set")
                .check(p -> p.getDescription() != null && !p.getDescription().isEmpty(), "Pizza description must be set")
                .check(p -> p.getPrice() != null, "Price must be set")
                .check(p -> p.getPizzaType() != null, "Pizza type must be set")
                .get();
    }
}