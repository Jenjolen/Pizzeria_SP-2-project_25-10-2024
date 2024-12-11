package dat.controllers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.controllers.IController;
import dat.daos.impl.PizzaDAO;
import dat.dtos.PizzaDTO;
import dat.entities.Pizza;
import dat.exceptions.ApiException;
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
    public void read(Context ctx) throws ApiException {
        // request
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            // DTO
            PizzaDTO pizzaDTO = dao.read(id);
            // response
            ctx.res().setStatus(200);
            ctx.json(pizzaDTO, PizzaDTO.class);
        } catch (Exception e) {
            throw new ApiException(404, "Pizza not found");
        }
    }

    @Override
    public void readAll(Context ctx) throws ApiException {
        // List of DTOS
        List<PizzaDTO> pizzaDTOS = dao.readAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(pizzaDTOS, PizzaDTO.class);
    }

    @Override
    public void create(Context ctx) throws ApiException {
        // request
        PizzaDTO jsonRequest = ctx.bodyAsClass(PizzaDTO.class);
        // DTO
        PizzaDTO pizzaDTO = dao.create(jsonRequest);
        // response
        ctx.res().setStatus(201);
        ctx.json(pizzaDTO, PizzaDTO.class);
    }

    public void createMultiple(Context ctx) throws ApiException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<PizzaDTO> pizzaDTOS;

        try {
            // Deserialize the JSON array into a list of PizzaDTO objects
            pizzaDTOS = objectMapper.readValue(ctx.body(), new TypeReference<List<PizzaDTO>>() {});
        } catch (Exception e) {
            ctx.res().setStatus(400);
            ctx.json(Map.of("error", "Invalid JSON format"));
            return;
        }

        // Iterate over the list and create each pizza
        for (PizzaDTO pizzaDTO : pizzaDTOS) {
            dao.create(pizzaDTO);
        }

        // Set the response status to 201 Created
        ctx.res().setStatus(201);
        ctx.json(pizzaDTOS);
    }

    public void populate(Context ctx) throws ApiException {
        populateService.populate();
        List<PizzaDTO> pizzaDTOS = dao.readAll();
        ctx.res().setStatus(200);
        ctx.json(pizzaDTOS);
        ctx.json("{\"message\": \"Database has been populated\"}");

    }

    @Override
    public void update(Context ctx) throws ApiException {
        // request
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PizzaDTO pizzaDTO = ctx.bodyAsClass(PizzaDTO.class);
            // dto
            PizzaDTO updatedPizzaDTO = dao.update(id, pizzaDTO);
            // response
            ctx.res().setStatus(200);
            ctx.json(updatedPizzaDTO, Pizza.class);
        } catch (Exception e) {
            throw new ApiException(404, "Pizza not updated");
        }
    }

    @Override
    public void delete(Context ctx) throws ApiException {
        // request
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            dao.delete(id);
            // response
            ctx.res().setStatus(204);
        } catch (Exception e) {
            throw new ApiException(404, "Pizza not deleted");
        }
    }
}



