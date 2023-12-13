package api.tests.stepdefs;

import api.tests.rest.proxy.OrderServiceProxy;
import api.tests.storage.Storage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import swagger.pet.store.model.Order;
import swagger.pet.store.model.Order.StatusEnum;
import swagger.pet.store.model.Pet;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 */
@Slf4j
public class OrderStepdef {

    @Autowired
    private Storage storage;

    @Autowired
    private OrderServiceProxy orderServiceProxy;

    @When("^user place[sd] an order for a pet with quantity of (\\d+) and status (placed|approved|delivered)$")
    public void orderPet(final int quantity, final String statusString) {
        final Pet pet = storage.getLastPet();
        final OffsetDateTime shipDate = OffsetDateTime.from(LocalDateTime.now().atOffset(ZoneOffset.UTC).withNano(0));

        final Order order = new Order();

        StatusEnum status = ("placed".equals(statusString)) ? StatusEnum.PLACED
                : ("approved".equals(statusString)) ? StatusEnum.APPROVED
                : ("delivered".equals(statusString)) ? StatusEnum.DELIVERED : null;

        order.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        order.setPetId(pet.getId());
        order.status(status);
        order.setComplete(false);
        order.setQuantity(quantity);
        order.setShipDate(shipDate);

        storage.getOrders().add(order);

        orderServiceProxy.placeAnOrderForPet(order);
    }

    @Then("^[Oo]rder (?:will be|is)? placed$")
    public void validateOrderIsPlaced() {
        final Order expectedOrder = storage.getLastOrder();
        final Order actualOrder = orderServiceProxy.getOrder(expectedOrder.getId());

        assertSoftly(softly -> {
            softly.assertThat(actualOrder.getId()).as("Order id is not correct!").isEqualTo(expectedOrder.getId());
            softly.assertThat(actualOrder.getPetId()).as("Oder pet ID is not correct!").isEqualTo(expectedOrder.getPetId());
            softly.assertThat(actualOrder.getQuantity()).as("Oder quantity is not correct!").isEqualTo(expectedOrder.getQuantity());
            softly.assertThat(actualOrder.getShipDate()).as("Oder Ship Date is not correct!").isEqualTo(expectedOrder.getShipDate());
            softly.assertThat(actualOrder.getStatus()).as("Oder status is not correct!").isEqualTo(expectedOrder.getStatus());
            softly.assertThat(actualOrder.getComplete()).as("Oder Complete is not correct!").isEqualTo(expectedOrder.getComplete());
        });
    }
}
