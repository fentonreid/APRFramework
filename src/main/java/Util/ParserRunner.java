package Util;

import YAMLParser.Defects4J;
import YAMLParser.Gp;
import YAMLParser.Output;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ParserRunner {
    private final String configFileName;
    public Gp gp;
    public Output output;
    public Defects4J defects4j;

    public ParserRunner() {
        this.configFileName = "config.yml";
    }

    public void main() throws Exception {
        InputStream inputStream = Files.newInputStream(Paths.get(configFileName));
        Map<String, Object> data = new Yaml().load(inputStream);

        Object gpObj = data.get("gp");
        Object outputObj = data.get("output");
        Object defects4jObj = data.get("defects4j");

        if (gpObj == null) { throw new Exception("'gp' property in config.yml is missing"); }
        if (outputObj == null) { throw new Exception("'output' property in config.yml is missing"); }
        if (defects4jObj == null) { throw new Exception("'defects4j' property in config.yml is missing"); }

        if (!(gpObj instanceof LinkedHashMap)) { throw new Exception("'gp' property is not in the correct form"); }
        if (!(outputObj instanceof LinkedHashMap)) { throw new Exception("'output' property is not in the correct form"); }
        if (!(defects4jObj instanceof LinkedHashMap)) { throw new Exception("'defects4j' property is not in the correct form"); }

        Gp gp = new Gp((LinkedHashMap<String, Object>) gpObj);
        Output output = new Output((LinkedHashMap<String, Object>) outputObj);
        Defects4J defects4j = new Defects4J((LinkedHashMap<String, Object>) defects4jObj);
    }
}