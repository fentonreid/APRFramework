import Parser.Parser;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            // Parse the given YAML configuration file using the Parser.Parser class definition
            Parser configData = new Yaml(new Constructor(Parser.class)).load(new FileInputStream(args[0]));

            System.out.println("Complete");

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Missing config.yml argument");
            return;
        } catch (FileNotFoundException ex) {
            System.out.println("Specified config.yml file cannot be found with name: " + args[0]);
            return;
        } catch (RuntimeException ex) {
            System.out.println("An error with parsing the YAML file was encountered. ");
            throw new RuntimeException(ex);
        }
    }
}
