package main.java.YAMLParser;

import main.java.Util.ShellProcessBuilder;
import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class Defects4J {
    public Map<String, HashSet<Integer>> selectedTestCases = new HashMap<>();

    public Defects4J(LinkedHashMap<String, Object> defects4jHashMap) throws Exception {
        parse(defects4jHashMap);
    }

    public void parse(LinkedHashMap<String, Object> defects4jHashMap) throws Exception {
        // Ensure property values of the YAML gp object are present
        if (!defects4jHashMap.containsKey("testCaseSelection")) { throw new Exception("'testCaseSelection' object is missing in the config.yml 'defects4j' object "); }
        if (!(defects4jHashMap.get("testCaseSelection") instanceof LinkedHashMap)) { throw new Exception("'testCaseSelection' object is not in the correct form"); }

        LinkedHashMap<String, Object> testCaseSelection = (LinkedHashMap<String, Object>) defects4jHashMap.get("testCaseSelection");

        // Ensure that the given property values are of the correct type
        if (!testCaseSelection.containsKey("method")) { throw new Exception("'method' property is missing in the config.yml 'output/testCaseSelection' object "); }
        if (!testCaseSelection.containsKey("selection")) { throw new Exception("'selection' property is missing in the config.yml 'output/testCaseSelection' object "); }

        // Check property types match
        if (!(testCaseSelection.get("method") instanceof String)) { throw new Exception("'method' property must be a String"); }
        if (!(testCaseSelection.get("selection") instanceof LinkedHashMap)) { throw new Exception("'selection' property is missing in the config.yml 'output/testCaseSelection' object "); }

        String method = (String) testCaseSelection.get("method");
        LinkedHashMap<String, Object> selection = (LinkedHashMap<String, Object>) testCaseSelection.get("selection");

        // Ensure constraints of properties are correct
        if (!(method.equals("identifier"))) { throw new Exception("Method must be 'identifier' and is currently " + method); }

        // Get an arrayList of Defects4J project identifiers
        ArrayList<String> defect4jIdentifiers = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "pids"});

        for (String identifier : selection.keySet()) {
            if (!(defect4jIdentifiers.contains(identifier))) { throw new Exception("Identifier '" + identifier + "' is not a valid Defects4J identifier" ); }
            if(!(selection.get(identifier) instanceof String) && !((selection.get(identifier)) instanceof ArrayList)) { throw new Exception("'" + identifier + "' identifier must be a range of id's or a String with a value of 'all'"); }

            if ((selection.get(identifier)) instanceof String) {
                if (!(selection.get(identifier).equals("all"))) { throw new Exception("For the '" + identifier +"' identifier the string value must be 'all' "); }
                selectedTestCases.put(identifier, getAllSingleIdentifier(identifier));
            } else {
                selectedTestCases.put(identifier, getRangeSingleIdentifier(identifier, (ArrayList<Object>) selection.get(identifier)));
            }

            if (!(validateBugIds(identifier, selectedTestCases.get(identifier)))) { throw new Exception(); };
        }
    }

    public HashSet<Integer> getAllSingleIdentifier(String identifier) throws Exception {
        HashSet<Integer> bugIds = new HashSet<>();
        for (String id : ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "bids", "-p", identifier})) {
            bugIds.add(Integer.parseInt(id));
        }

        return bugIds;
    }

    public HashSet<Integer> getRangeSingleIdentifier(String identifier, ArrayList<Object> selectionRange) throws Exception {
        HashSet<Integer> bugIds = new HashSet<>();

        for (Object e :selectionRange ) {
            if (!(e instanceof Integer) && !(e instanceof String)) { throw new Exception("'" + identifier + "' identifier must only contain a range or integer value"); }

            if (e instanceof Integer) {
                bugIds.add((Integer) e);
                continue;
            }

            try {
                String[] idSplit = ((String) e).split("-");
                int[] idRange = IntStream.rangeClosed(Integer.parseInt(idSplit[0]), Integer.parseInt(idSplit[1])).toArray();
                for (int intInRange: idRange) {
                    bugIds.add(intInRange);
                }
            } catch (Exception ex) {
                throw new Exception("For identifier " + identifier + " could not split " + e + ", make sure that you split by '-' and do not include non-numeric values");
            }
        }

        return bugIds;
    }

    public boolean validateBugIds(String identifier, HashSet<Integer> bugIds) throws Exception {
        Set<Integer> defects4jBugs = new HashSet<>(getAllSingleIdentifier(identifier));
        Set<Integer> currentBugs = new HashSet<>(bugIds);

        // Perform relative complement of user chosen ids against known Defects4J ids
        currentBugs.removeAll(defects4jBugs);

        if(currentBugs.size() != 0) {
            throw new Exception("Identifier '" + identifier + "' has invalid id(s) " + currentBugs.toString());
        }

        return true;
    }
}
