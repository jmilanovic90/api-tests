package api.tests.storage;

import io.cucumber.java.Scenario;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ScenarioEntity {

    private String scenarioName;

    private String fileDirectory;

    private String workingDirectory;

    @Getter(AccessLevel.NONE)
    private int screenshotCounter = 0;

    private Scenario scenario;

}