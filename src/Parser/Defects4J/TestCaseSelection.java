package Parser.Defects4J;

import Defects4J.perlInterpreter;
import java.util.*;
import java.util.stream.IntStream;

public class TestCaseSelection {
    private String method;
    private Object selection;

    private Map<String, HashSet<Integer>> identifierToIdMap;

    private Map<String, HashSet<Integer>> defects4JIdentifierToIdMap;

    public String getMethod() { return method.toLowerCase().trim(); }
    public void setMethod(String method) {
        this.method = method;
    }

    public Object getSelection() {
        return selection;
    }
    public void setSelection(Object selection) {
        this.selection = selection;
    }

    public Map<String, HashSet<Integer>> getIdentifierToIdMap() { return identifierToIdMap; }
    public void setIdentifierToIdMap(Map<String, HashSet<Integer>> identifierToIdMap) { this.identifierToIdMap = identifierToIdMap; };

    public Map<String, HashSet<Integer>> getDefects4JIdentifierToIdMap() { return defects4JIdentifierToIdMap; }
    public void setDefects4JIdentifierToIdMap(Map<String, HashSet<Integer>> defects4JIdentifierToIdMap) { this.defects4JIdentifierToIdMap = defects4JIdentifierToIdMap; };

    public void setup() throws Exception {
        if (getMethod() == null) { throw new Exception("Method property must be provided"); }

        getProjectIds();
        parseObjectStructure();
    }

    public void parseObjectStructure() throws Exception {
        // Declare a dictionary of identifiers to ids
        Map<String, HashSet<Integer>> identifierToIdMap = new HashMap<>();

        // Based on the method used we do different things
        if (getMethod().equals("identifier")) {
            if (!(getSelection() instanceof LinkedHashMap)) {
                throw new Exception("For method " + getMethod() + " the selection property must be a dictionary of Defects4J identifiers with associated values");
            }

            LinkedHashMap<String, ?> selections = (LinkedHashMap<String, ?>) getSelection();

            for (String selection : selections.keySet()) {
                // Make sure that the selection value is not null
                if (selections.get(selection) == null) {
                   throw new Exception("Identifier '" + selection + "' there is no associated value. Specify a list of bug id's or the provide the string all as the value");
                }

                // If selection is a valid project id
                if(!getDefects4JIdentifierToIdMap().containsKey(selection)) {
                    throw new Exception("Identifier '" + selection + "' is not a valid Defects4J project id.\n The following project id's are valid: " + getDefects4JIdentifierToIdMap().keySet());
                }

                // If the current identifier has a value of all
                if (selections.get(selection) instanceof String) {
                    if (selections.get(selection).toString().toLowerCase().trim().equals("all")) {
                        identifierToIdMap.put(selection, getDefects4JIdentifierToIdMap().get(selection));
                        continue;
                    }
                        throw new Exception(" For selection '" + selection + "' the string value is invalid");

                } else if (selections.get(selection) instanceof ArrayList) {
                    Object[] selectedBugIds = ((ArrayList<?>) selections.get(selection)).toArray();
                    HashSet<Integer> integerIdSet = new HashSet<>();

                    // Convert Object id values e.g. ["1-4", 6, "8-12"] to integer id's
                    for (Object id : selectedBugIds) {
                        if (id instanceof Integer) {
                            integerIdSet.add((Integer) id);

                        } else if (id instanceof String) {
                            // Parse for example, "1-4"
                            try {
                                String[] idSplit = ((String) id).split("-");
                                int[] idRange = IntStream.rangeClosed(Integer.parseInt(idSplit[0]), Integer.parseInt(idSplit[1])).toArray();

                                for (int intInRange: idRange) {
                                    integerIdSet.add(intInRange);
                                }

                            } catch (Exception ex) {
                                throw new Exception("For identifier " + selection + " could not split " + id + ", make sure that you split by '-' and do not include non-numeric values");
                            }

                        } else {
                            throw new Exception("For identifier " + selection + " there contains a value that is neither integer or string");
                        }
                    }

                    identifierToIdMap.put(selection, integerIdSet);
                }
            }

            if (validIdentifierToIdMap(identifierToIdMap)) {
                setIdentifierToIdMap(identifierToIdMap);
            }

        } else if (getMethod().equals("all")) {
            if (getSelection() != null) { throw new Exception("For method " + getMethod() + " the selection property is not allowed"); }
            setIdentifierToIdMap(getDefects4JIdentifierToIdMap());

        } else {
            throw new Exception("The method property '" + getMethod() + "' is invalid");
        }
    }

    private void getProjectIds() throws Exception {
        Map<String, HashSet<Integer>> defectsIdentifierToIdMap = new HashMap<>();

        // Get current project ids that are a part of Defects4J
        ArrayList<String> projectIds = perlInterpreter.getStandardInput(new String[]{"perl", "defects4J", "pids"});

        // Active bug id's of each project
        for (String project : projectIds) {
            ArrayList<String> currentBugIdsAsString = perlInterpreter.getStandardInput(new String[]{"perl", "defects4J", "bids", "-p", project});
            HashSet<Integer> currentBugIds = new HashSet<>();

            for (String id : currentBugIdsAsString) {
                currentBugIds.add(Integer.parseInt(id));
            }

            defectsIdentifierToIdMap.put(project, currentBugIds);
        }

        setDefects4JIdentifierToIdMap(defectsIdentifierToIdMap);
    }

    private boolean validIdentifierToIdMap(Map<String, HashSet<Integer>> identifierIdMap) throws Exception {
        for (String identifier : identifierIdMap.keySet()) {
            // Perform relative complement of identifier ids against known Defects4J ids
            Set<Integer> relativeComplement = new HashSet<>(identifierIdMap.get(identifier));
            relativeComplement.removeAll(getDefects4JIdentifierToIdMap().get(identifier));

            if(relativeComplement.size() != 0) {
                throw new Exception("Identifier '" + identifier + "' has invalid id(s) " + relativeComplement.toString());
            }
        }

        return true;
    }
}