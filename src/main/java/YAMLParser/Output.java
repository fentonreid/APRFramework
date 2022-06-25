package YAMLParser;

import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
public class Output {
    public String summaryCSV;
    public Boolean javadoc;
    public Boolean patches;
    public Boolean gp;

    public Output(LinkedHashMap<String, Object> outputHashMap) throws Exception {
        parse(outputHashMap);
    }

    public void parse(LinkedHashMap<String, Object> outputHashMap) throws Exception {
        // Ensure property values of the YAML gp object are present
        if (!outputHashMap.containsKey("csv")) { throw new Exception("'csv' property is missing in the config.yml 'output' object "); }
        if (!(outputHashMap.get("csv") instanceof String)) { throw new Exception("'csv' property is not a String"); }

        // Check for optional properties
        if (outputHashMap.containsKey("javadoc") && !(outputHashMap.get("javadoc") instanceof Boolean)) { throw new Exception("'javadoc' property is not a Boolean"); }
        if (outputHashMap.containsKey("patches") && !(outputHashMap.get("patches") instanceof Boolean)) { throw new Exception("'patches' property is not a Boolean"); }
        if (outputHashMap.containsKey("gp") && !(outputHashMap.get("gp") instanceof Boolean)) { throw new Exception("'gp' property is not a Boolean"); }

        if (outputHashMap.containsKey("javadoc") && (outputHashMap.get("javadoc") instanceof Boolean)) { javadoc = (Boolean) outputHashMap.get("javadoc"); }
        if (outputHashMap.containsKey("patches") && (outputHashMap.get("patches") instanceof Boolean)) { patches = (Boolean) outputHashMap.get("patches"); }
        if (outputHashMap.containsKey("gp") && (outputHashMap.get("gp") instanceof Boolean)) { gp = (Boolean) outputHashMap.get("gp"); }
        
        summaryCSV = outputHashMap.get("csv").toString();
    }
}