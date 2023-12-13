package api.tests.stepdefs;

import api.tests.rest.proxy.PetServiceProxy;
import api.tests.storage.Storage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import swagger.pet.store.model.Pet;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 */
@Slf4j
public class PetStepdef {

    @Autowired
    private Storage storage;

    @Autowired
    private PetServiceProxy petServiceProxy;


    @Given("^[Uu]ser add(?:s|ed) pet \"(.*)\" to the pet store$")
    public void addPet(final String petName) {

        final Pet newPet = new Pet();
        newPet.setName(petName);
        newPet.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        newPet.setStatus(Pet.StatusEnum.AVAILABLE);

        final Pet newlyAddedPet = petServiceProxy.addPetToStore(newPet);

        storage.getPets().add(newlyAddedPet);

        log.info("Pet " + petName + " added to the store.");
    }

    @When("^[Pp]et will have status \"(available|pending|sold)\"$")
    public void setPetStatus(final String petStatus) {
        final Pet pet = storage.getLastPet();
        assertThat(pet.getStatus().getValue()).as("Pet is not available!").isEqualTo(petStatus);

        log.info("It is possible to sell the Pet.");
    }

    @When("^[Pp]et is removed from the pet store$")
    public void removePet() {
        final Pet pet = storage.getLastPet();
        final boolean isPetDeleted = petServiceProxy.removePet(pet.getId());

        assertThat(isPetDeleted).as("Pet is not deleted!").isTrue();

        log.info("Pet is removed from pet store.");
    }

    @Then("^[Pp]et (?:will not be|is)? present in the pet store$")
    public void validatePetRemovedFromStore() {
        final Pet pet = storage.getLastPet();
        final String actualErrorMessage = petServiceProxy.getUnavailablePet(pet.getId());
        assertThat(actualErrorMessage).as("Pet is available in the pet store!").isEqualTo("Pet not found");
    }
}
