package Parser;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Runner {
    private final String configFileName;

    public Runner(String[] args) {
        this.configFileName = args[0];
    }
    
    public Parser main() {
        try {
            // Parse the given YAML configuration file using the Parser.Parser class definition
            Parser configData = new Yaml(new Constructor(Parser.class)).load(new FileInputStream(configFileName));

            // Ensure that the gp, defects4J and output properties are not null
            if(!configData.propertiesSet()) {
                throw new Exception("Please include a gp, defects4J and output model in your yaml configuration");
            }

            // Parse generic object properties
            try {
                // Cast mutationOperators object to ArrayList<String>
                configData.getGp().parseObjectStructure();

                // Cast testCaseSelection object to TestCaseSelection class
                configData.getDefects4J().getTestCaseSelection().parseObjectStructure();

            } catch (ClassNotFoundException ex) {
                throw new Exception(ex);
            }

            System.out.println("Initial parse complete");
            return configData;

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Missing config.yml argument");
            throw new RuntimeException(ex);

        } catch (FileNotFoundException ex) {
            System.out.println("Specified config.yml file cannot be found with name: " + configFileName);
            throw new RuntimeException(ex);

        } catch (RuntimeException ex) {
            System.out.println("An error with parsing the YAML file was encountered. ");
            throw new RuntimeException(ex);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
