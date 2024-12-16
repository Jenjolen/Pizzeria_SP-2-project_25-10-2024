package dat.routes;

import dat.controllers.impl.OrderController;
import dat.controllers.impl.PizzaController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class OrderRoute {

    private final OrderController orderController = new OrderController();

    protected EndpointGroup getRoutes() {

        return () -> {
            //  get("/populate", orderController::populate);
            // Order routes
            post("/", orderController::create);
            get("/", orderController::readAll);
            get("/{id}", orderController::read);
            put("/{id}", orderController::update);
            delete("/{id}", orderController::delete);

            // OrderLine routes
            post("/{id}/orderline", orderController::addOrderLine); // vi laver en ny orderline på en order med et bestemt orderId
//            put("/orderline/{id}", orderController::updateOrderLine); // orderLineId er hvad Id refererer til
            delete("/orderline/{id}", orderController::deleteOrderLine); // orderLineId er hvad Id refererer til
            get("/orderline/{id}", orderController::readOrderLine); // orderLineId er hvad Id refererer til - vi henter en orderline med et bestemt id
            get("/orderline", orderController::readAllOrderLines); // vi henter alle orderlines uanset order
            get("/{id}/orderline", orderController::readAllOrderLinesByOrder); // orderId er hvad Id refererer til - vi henter alle orderlines på en order med et bestemt orderId

        };
    }



}
