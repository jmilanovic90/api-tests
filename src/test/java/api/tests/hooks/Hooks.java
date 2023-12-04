package api.tests.hooks;

import api.tests.config.SpringConfig;
import api.tests.storage.ScenarioEntity;
import api.tests.storage.Storage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 * @since 26.09.23.
 */
@Slf4j
@CucumberContextConfiguration
@ContextConfiguration(classes = {SpringConfig.class})
public class Hooks {

    @Autowired
    private Storage storage;

    @Before(order = 0)
    public void scenarioStart(final Scenario scenario) {
        log.info("SCENARIO: '{}' started!", scenario.getName());
        final ScenarioEntity testScenario = storage.getTestScenario();
        testScenario.setScenario(scenario);
        testScenario.setScenarioName(scenario.getName());
        testScenario.setWorkingDirectory(System.getProperty("user.dir"));
    }

    @After
    public void scenarioEnd(final Scenario scenario) {
        log.info("SCENARIO: '{}' finished with status {}!", scenario.getName(), scenario.getStatus());
    }
}
