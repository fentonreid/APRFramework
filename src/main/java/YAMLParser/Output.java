package main.java.YAMLParser;

import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
public class Output {
    public String summaryCSV;

    public Output(LinkedHashMap<String, Object> outputHashMap) throws Exception {
        parse(outputHashMap);
    }

    public void parse(LinkedHashMap<String, Object> outputHashMap) throws Exception {
        // Ensure property values of the YAML gp object are present
        if (!outputHashMap.containsKey("csv")) { throw new Exception("'csv' property is missing in the config.yml 'output' object "); }
        if (!(outputHashMap.get("csv") instanceof String)) { throw new Exception("'csv' property is not a String"); }

        summaryCSV = outputHashMap.get("csv").toString();
    }
}