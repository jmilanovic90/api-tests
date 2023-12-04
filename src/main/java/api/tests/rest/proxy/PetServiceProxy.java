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
import swagger.pet.store.model.Pet;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class PetServiceProxy {

    public static final String REST_PET = "/v2/pet/";
    public static final String REST_STORE_ORDER = "/v2/store/order/";
    private BaseRestClient petRestClient;

    @Autowired
    public PetServiceProxy(@Value("${service.petstore.url}") final String serviceUrl, Storage storage) {
        this.petRestClient = new BaseRestClient(serviceUrl, storage);
    }

    /**
     * Add new pet to the Pet Store.
     *
     * @param newPet
     */
    public Pet addPetToStore(final Pet newPet) {

        final Response response = petRestClient.post(newPet, null, REST_PET);
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Pet can not be added. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }

        return response.as(Pet.class);
    }

    public void placeAnOrderForPet(final Order order) {

        final Response response = petRestClient.post(order, null, REST_STORE_ORDER);
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Order could not be placed!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }
    }

    /**
     * Try to get unavailable pet from Pet Store.
     *
     * @param petId
     * @return error message
     */
    public String getUnavailablePet(final Long petId) {

        final Response response = petRestClient.get(null, REST_PET + petId);
        if (response.statusCode() == HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Pet is available in the pet store. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }
        return response.getBody().jsonPath().getString("message");
    }

    /**
     * Remove pet from Pet Store.
     *
     * @param pet pet to be removed
     * @return true if pet is removed successfully, false otherwise
     */
    public boolean removePet(final Pet pet) {
        final Response response = petRestClient.delete(null, REST_PET + pet.getId());
        return response.statusCode() == HttpStatus.SC_OK;
    }

    public Order getOrder(final Long orderId) {

        final Response response = petRestClient.get(null, REST_STORE_ORDER + orderId);
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Order not found!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }
        return response.as(Order.class);
    }
}
