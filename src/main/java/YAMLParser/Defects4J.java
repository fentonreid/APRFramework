package YAMLParser;

import Util.ShellProcessBuilder;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The Defects4J class validates the YAML Defects4J properties.
 */
@SuppressWarnings("unchecked")
public class Defects4J {
    public Map<String, HashSet<Integer>> selectedTestCases = new HashMap<>();
    public HashMap<String, HashSet<Integer>> validBugs;

    /**
    * Class constructor setting the valid bugs field.
    *
    * @exception Exception Valid bugs could not be assigned
     */
    public Defects4J() throws Exception {
        validBugs = getAllValidBugs();
    }

    /**
    * Class constructor setting the valid bugs field and parsing the given LinkedHashMap.
    *
    * @exception Exception YAML properties could not be created or
     */
    public Defects4J(LinkedHashMap<String, Object> defects4jHashMap) throws Exception {
        validBugs = getAllValidBugs();
        parse(defects4jHashMap);
    }

    /**
    * Parse method ensures presence and correct instance types of YAML properties for the Defects4j project.
    * Parsing of the selected Defects4j bugs from config are performed and unsupported bug ids are removed.
    *
    * @param defects4jHashMap   Defects4j YAML object from config
    * @exception Exception      If defects4jHashMap is missing a property or is of the wrong type
    */
    public void parse(LinkedHashMap<String, Object> defects4jHashMap) throws Exception {

        // Ensure property values of the YAML gp object are present
        if (!defects4jHashMap.containsKey("testCaseSelection")) {
            throw new Exception("'testCaseSelection' object is missing in the config.yml 'defects4j' object ");
        }
        if (!(defects4jHashMap.get("testCaseSelection") instanceof LinkedHashMap)) {
            throw new Exception("'testCaseSelection' object is not in the correct form");
        }

        LinkedHashMap<String, Object> testCaseSelection = (LinkedHashMap<String, Object>) defects4jHashMap.get("testCaseSelection");

        // Ensure that the given property values are of the correct type
        if (!testCaseSelection.containsKey("method")) {
            throw new InvalidParameterException("'method' property is missing in the config.yml 'output/testCaseSelection' object ");
        }

        // Get method type
        if (!(testCaseSelection.get("method") instanceof String)) { throw new Exception("'method' property must be a String"); }
        String method = (String) testCaseSelection.get("method");

        if (method.equals("identifier")) {
            if (!(testCaseSelection.get("selection") instanceof LinkedHashMap)) { throw new InvalidParameterException("'selection' property is missing in the config.yml 'output/testCaseSelection' object "); }
            LinkedHashMap<String, Object> selection = (LinkedHashMap<String, Object>) testCaseSelection.get("selection");

            for (String identifier : selection.keySet()) {
                if (!validBugs.containsKey(identifier)) { throw new NoSuchElementException("Identifier '" + identifier + "' is not a valid Defects4J identifier"); }
                if (!(selection.get(identifier) instanceof String) && !((selection.get(identifier)) instanceof ArrayList)) { throw new IllegalArgumentException("'" + identifier + "' identifier must be a range of id's or a String with a value of 'all'"); }

                if ((selection.get(identifier)) instanceof String) {
                    if (!(selection.get(identifier).equals("all"))) { throw new Exception("For the '" + identifier + "' identifier the string value must be 'all' "); }
                    selectedTestCases.put(identifier, validBugs.get(identifier));

                } else {
                    selectedTestCases.put(identifier, getRangeSingleIdentifier(identifier, (ArrayList<Object>) selection.get(identifier)));
                }
            }

        } else if (method.equals("all")) {
            if (testCaseSelection.containsKey("selection")) { throw new InvalidParameterException("'selection' property is not needed for method 'identifier' in the config.yml 'output/testCaseSelection' object "); }
            selectedTestCases = validBugs;

        } else {
            throw new Exception("Incorrect method type");
        }
    }

    /**
    * All current active Defects4j bugs that have only one modified class are returned.
    *
    * @return               A HashMap of Defects4j projects to a hashset of valid bug id's
    * @exception Exception  If the Defects4j command output is invalid or throws
     */
    public HashMap<String, HashSet<Integer>> getAllValidBugs() throws Exception {
        HashMap<String, HashSet<Integer>> validBugs = new HashMap<>();

        // Need project ids
        ArrayList<String> defect4jIdentifiers = new ArrayList<>(ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "pids"}));

        for (String identifier : defect4jIdentifiers) {
            HashSet<Integer> bids = new HashSet<>();

            // Get all valid bug ids
            ArrayList<String> bugProperties = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "query", "-p", identifier, "-q", "bug.id,classes.modified,classes.relevant.src"});

            for (String property : bugProperties) {

                String[] split = property.split(",");

                if (split.length != 3) { continue; }

                String modifiedClass = split[1].replaceAll("\"", "");
                String relevantClasses = split[2];

                // Ensure the number of modified classes to achieve the patch is one
                if (modifiedClass.split(";").length > 1) { continue; }
                if (!relevantClasses.contains(modifiedClass)) { continue; }

                bids.add(Integer.valueOf(split[0]));
            }

            if (bids.size() > 0) { validBugs.put(identifier, bids); }
        }

        return validBugs;
    }

    /**
    * Given a range of values in the form of an ArrayList determine if they specify range or single bug id's and collect the valid bug id's to return.
    *
    * @param identifier         String value of the Defects4j project name to collect bug id's from
    * @param selectionRange     ArrayList of Object, can be a mix of single bug id's ([5]) or a range ([1-3])
    * @return                   HashSet of valid bug id's
    * @exception Exception      The range identifier could not be split or no valid bug id's for a specific identifier could be returned
     */
    public HashSet<Integer> getRangeSingleIdentifier(String identifier, ArrayList<Object> selectionRange) throws Exception {
        HashSet<Integer> bugIds = new HashSet<>();

        // For each object in the ArrayList
        for (Object bid : selectionRange ) {
            if (!(bid instanceof Integer) && !(bid instanceof String)) { throw new Exception("'" + identifier + "' identifier must only contain a range or integer value"); }

            // If the bug is a single bug and is contained in the valid bugs HashMap
            if (bid instanceof Integer && validBugs.get(identifier).contains((Integer) bid)) {
                bugIds.add((Integer) bid);
                continue;
            }

            try {
                // Try and split the range by '-'
                String[] idSplit = ((String) bid).split("-");
                int[] idRange = IntStream.rangeClosed(Integer.parseInt(idSplit[0]), Integer.parseInt(idSplit[1])).toArray();
                for (int intInRange: idRange) {
                    if(validBugs.get(identifier).contains(intInRange)) { bugIds.add(intInRange); }
                }
            } catch (Exception ex) { throw new Exception("For identifier " + identifier + " could not split " + bid + ", make sure that you split by '-' and do not include non-numeric values"); }
        }

        if (bugIds.isEmpty()) { throw new Exception("No valid bug id's chosen for the given range for '" + identifier + "'"); }

        return bugIds;
    }
}
