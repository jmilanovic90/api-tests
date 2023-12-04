Feature: Pets

  User is able to add new pet to the store, when it is added to the store user can fetch the information about the pet,
  change pet information or delete the pet from the store.

  Scenario: User is able to sell a pet in status available
    When User adds pet "Doberman" to the pet store
    Then Pet will have status "available"

  Scenario: User is able to remove pet from the Pet Store
    Given User added pet "Labrador" to the pet store
    When Pet is removed from the pet store
    Then Pet will not be present in the pet store

  Scenario: User is able to place an order for a pet
    Given User adds pet "German Shepherd" to the pet store
    When  user places order for a pet with quantity of 5 with status placed
    Then  Order will be placed