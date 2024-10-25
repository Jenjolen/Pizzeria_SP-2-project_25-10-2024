package dat.routes;

import dat.controllers.impl.PizzaController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PizzaRoute {

    private final PizzaController pizzaController = new PizzaController();

    protected EndpointGroup getRoutes() {

        return () -> {
            //  get("/populate", pizzaController::populate);
            post("/", pizzaController::create);
            post("/multiple", pizzaController::createMultiple);
            get("/populate", pizzaController::populate);
            get("/", pizzaController::readAll);
            get("/{id}", pizzaController::read);
            put("/{id}", pizzaController::update);
            delete("/{id}", pizzaController::delete);
        };
    }
}