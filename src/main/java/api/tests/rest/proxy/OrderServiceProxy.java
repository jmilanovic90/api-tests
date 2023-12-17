package api.tests.rest.proxy;

import api.tests.rest.client.BaseRestClient;
import api.tests.storage.Storage;
import api.tests.util.FunctionalTestsException;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import swagger.pet.store.model.Order;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class OrderServiceProxy {

    private BaseRestClient orderRestClient;

    @Autowired
    public OrderServiceProxy(@Value("${order.service.url}") final String serviceUrl, Storage storage) {
        this.orderRestClient = new BaseRestClient(serviceUrl, storage);
    }

    public void placeAnOrderForPet(final Order order) {

        final Response response = orderRestClient.post(order, null, "");
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Order could not be placed!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }
    }

    public Order getOrder(final Long orderId) {
        final Response response = orderRestClient.get(null, String.valueOf(orderId));
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Order not found!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }
        return response.as(Order.class);
    }
}
