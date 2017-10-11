package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.ChicShock;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_Scale_1 extends BaseExperiment implements Experiment {

    private ContextService cms;
    private int counter;

    public Experiment_Scale_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);

        // Prepare the experiments to be run
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            units.add(new Experiment_Scale_1.ExperimentUnit_Scale_1());
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public void finish() {
        super.finish();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, "END OF EXPERIMENT Scale_1. # times a predicate was run: " + counter);
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations();
    }

    private void addContentToNode() throws URISyntaxException {

        // Add a bunch of data, versions, and so on here
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation("https://www.takemefishing.org/tmf/assets/images/fish/american-shad-464x170.png"));
        VersionBuilder versionBuilder = new VersionBuilder().setAtomBuilder(atomBuilder);

        node.getAgent().addData(versionBuilder);
    }

    private void addContexts() throws Exception {

        /*

{
	"context": {
		"name": "All",
		"domain": {
			"type": "LOCAL",
			"nodes": []
		},
		"codomain": {
			"type": "SPECIFIED",
			"nodes": ["SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4"]
		}
	},
	"predicate": {
		"Type": "Predicate",
		"Predicate": "true;",
		"Dependencies": []
	},
	"policies": [{
		"Type": "Policy",
		"Apply": "",
		"Satisfied": "return true;",
		"Dependencies": []
	}]
}

         */
        node.getCMS().addContext(
                "{\n" +
                        "  \"context\": {\n" +
                        "    \"name\": \"All\",\n" +
                        "    \"domain\": {\n" +
                        "      \"type\": \"LOCAL\",\n" +
                        "      \"nodes\": []\n" +
                        "    },\n" +
                        "    \"codomain\": {\n" +
                        "      \"type\": \"SPECIFIED\",\n" +
                        "      \"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"predicate\": {\n" +
                        "    \"type\": \"Predicate\",\n" +
                        "    \"Predicate\": \"true;\",\n" +
                        "    \"Dependencies\": []\n" +
                        "  },\n" +
                        "  \"policies\": [{\n" +
                        "    \"type\": \"Policy\",\n" +
                        "    \"Apply\": \"\",\n" +
                        "    \"Satisfied\": \"return true;\",\n" +
                        "    \"Dependencies\": []\n" +
                        "  }]\n" +
                        "}"
        );
    }

    private class ExperimentUnit_Scale_1 implements ExperimentUnit {

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"SETTING UP EXPERIMENT");

            try {
                cms = node.getCMS();

                addContentToNode();
                addContexts();
            } catch (Exception e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"RUNNING EXPERIMENT");

            try {
                counter = cms.runPredicates();
            } catch (ContextException e) {
                SOS_LOG.log(LEVEL.ERROR, "Experiment Scale_1 - Unable to run predicates properly");
            }
        }
    }

    public static void main(String[] args) throws ChicShockException, ConfigurationException, ExperimentException, InterruptedException {

        System.out.println("L/l for Local run and H/h for hogun cluster run");

        Scanner in = new Scanner(System.in);
        String option = in.nextLine();
        switch(option) {
            case "l": case "L":
                runLocal();
                break;

            case "h": case "H":
                runHogun();
                break;
        }
    }

    private static void runLocal() throws ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "scale_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_Scale_1 experiment_pr_1 = new Experiment_Scale_1(experimentConfiguration);
        experiment_pr_1.process();
    }

    private static void runHogun() throws ConfigurationException, ChicShockException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "scale_1") + "configuration-hogun.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.chic();
        chicShock.chicExperiment();

        chicShock.shock();
        chicShock.shockExperiment();

        Thread.sleep(60000);

        chicShock.unShockExperiment();
        chicShock.unChicExperiment();

        chicShock.unShock();
        chicShock.unChic();
    }
}
