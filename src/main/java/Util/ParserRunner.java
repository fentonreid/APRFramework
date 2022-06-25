package Util;

import YAMLParser.Defects4J;
import YAMLParser.Gp;
import YAMLParser.Output;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class ParserRunner {
    public static Gp gp;
    public static Output output;
    public static Defects4J defects4j;

    public static void main(String configurationFileName) throws Exception {
        InputStream inputStream;
        try { inputStream = Files.newInputStream(Paths.get(configurationFileName)); }
        catch (IOException ex) { throw new IOException("Could not get the configuration file '" + configurationFileName + "'"); }

        Map<String, Object> data = new Yaml().load(inputStream);
        Object gpObj = data.get("gp");
        Object outputObj = data.get("output");
        Object defects4jObj = data.get("defects4j");

        if (gpObj == null) { throw new UnsupportedOperationException("'gp' property in config.yml is missing"); }
        if (outputObj == null) { throw new UnsupportedOperationException("'output' property in config.yml is missing"); }
        if (defects4jObj == null) { throw new UnsupportedOperationException("'defects4j' property in config.yml is missing"); }

        if (!(gpObj instanceof LinkedHashMap)) { throw new UnsupportedOperationException("'gp' property is not in the correct form"); }
        if (!(outputObj instanceof LinkedHashMap)) { throw new UnsupportedOperationException("'output' property is not in the correct form"); }
        if (!(defects4jObj instanceof LinkedHashMap)) { throw new UnsupportedOperationException("'defects4j' property is not in the correct form"); }

        gp = new Gp((LinkedHashMap<String, Object>) gpObj);
        output = new Output((LinkedHashMap<String, Object>) outputObj);
        defects4j = new Defects4J((LinkedHashMap<String, Object>) defects4jObj);
    }
}