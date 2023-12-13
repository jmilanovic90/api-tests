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
import swagger.pet.store.model.Pet;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class PetServiceProxy {

    private BaseRestClient petRestClient;

    @Autowired
    public PetServiceProxy(@Value("${pet.service.url}") final String serviceUrl, Storage storage) {
        this.petRestClient = new BaseRestClient(serviceUrl, storage);
    }

    /**
     * Add new pet to the Pet Store.
     *
     * @param newPet
     */
    public Pet addPetToStore(final Pet newPet) {

        final Response response = petRestClient.post(newPet, null, "");
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Pet can not be added. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }

        return response.as(Pet.class);
    }

    /**
     * Try to get unavailable pet from Pet Store.
     *
     * @param petId
     * @return error message
     */
    public String getUnavailablePet(final Long petId) {

        final Response response = petRestClient.get(null, String.valueOf(petId));
        if (response.statusCode() == HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Pet is available in the pet store. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
                    response.statusCode(), response.getBody().prettyPrint());
        }
        return response.getBody().jsonPath().getString("message");
    }

    /**
     * Remove pet from Pet Store.
     *
     * @param petId pet to be removed
     * @return true if pet is removed successfully, false otherwise
     */
    public boolean removePet(final Long petId) {
        final Response response = petRestClient.delete(null, String.valueOf(petId));
        return response.statusCode() == HttpStatus.SC_OK;
    }
}
