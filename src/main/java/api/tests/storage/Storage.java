package api.tests.storage;

import api.tests.util.FunctionalTestsException;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import swagger.pet.store.model.Order;
import swagger.pet.store.model.Pet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 * @since 26.09.23.
 */
@Getter
@Component
@Scope("cucumber-glue")
public class Storage {
    private final List<Pet> pets = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final ScenarioEntity testScenario = new ScenarioEntity();

    /**
     * Get last pet from storage
     *
     * @return last pet
     */
    public Pet getLastPet() {
        return pets.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Last Pet not found!"));
    }

    public Order getLastOrder() {
        return orders.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Last Order not found!"));
    }
}
