package YAMLParser;

import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
public class Output {
    public String summaryCSV;
    public String fullJARName;

    public Output(LinkedHashMap<String, Object> outputHashMap) throws Exception {
        parse(outputHashMap);
    }

    public void parse(LinkedHashMap<String, Object> outputHashMap) throws Exception {
        // Ensure property values of the YAML gp object are present
        if (!outputHashMap.containsKey("summary")) { throw new Exception("'summary' object is missing in the config.yml 'output' object "); }
        if (!(outputHashMap.get("summary") instanceof LinkedHashMap)) { throw new Exception("'summary' object is not in the correct form"); }

        if (!outputHashMap.containsKey("full")) { throw new Exception("'full' object is missing in the config.yml 'output' object "); }
        if (!(outputHashMap.get("full") instanceof LinkedHashMap)) { throw new Exception("'full' object is not in the correct form"); }

        LinkedHashMap<String, Object> summary = (LinkedHashMap<String, Object>) outputHashMap.get("summary");
        LinkedHashMap<String, Object> full = (LinkedHashMap<String, Object>) outputHashMap.get("full");

        // Ensure that the given property values are of the correct type
        if (!summary.containsKey("csv")) { throw new Exception("'csv' property is missing in the config.yml 'output/summary' object "); }
        if (!full.containsKey("jar")) { throw new Exception("'jar' property is missing in the config.yml 'output/full' object "); }

        // Check property types match
        if (!(summary.get("csv") instanceof String)) { throw new Exception("'csv' property must be a String"); }
        if (!(full.get("jar") instanceof String)) { throw new Exception("'jar' property must be a String"); }

        summaryCSV = summary.get("csv").toString();
        fullJARName = full.get("jar").toString();
    }
}