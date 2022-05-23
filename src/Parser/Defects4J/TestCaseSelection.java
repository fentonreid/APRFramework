package Parser.Defects4J;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class TestCaseSelection {
    private String method;
    private Object selection;

    private Map<String, HashSet<Integer>> identifierToIdMap;

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

    public void setup() throws Exception {
        if (getMethod() == null) { throw new Exception("Method property must be provided"); }

        parseObjectStructure();
        validateIdentifierToIdMap();
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
                // If the current identifier has a value of all
                if(selections.get(selection) instanceof String) {
                    if(selections.get(selection).toString().toLowerCase().trim().equals("all")) {
                        // Convert string id to integer id
                        identifierToIdMap.put(selection, null);
                        continue;
                    }
                        throw new Exception(" For selection '" + selection + "' the string value is invalid");

                } else if(selections.get(selection) instanceof ArrayList) {
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

                                for(int intInRange: idRange) {
                                    integerIdSet.add(intInRange);
                                }

                            } catch (Exception ex) {
                                throw new Exception("For identifier " + selection + " could not split " + (String) id + ", make sure that you split by '-' and do not include non-numeric values");
                            }

                        } else {
                            throw new Exception("For identifier " + selection + " there contains a value that is neither integer or string");
                        }
                    }

                    identifierToIdMap.put(selection, integerIdSet);
                }
            }

        } else if (getMethod().equals("all")) {
            // Ensure selection property is excluded
            if (getSelection() != null) {
                throw new Exception("For method " + getMethod() + " the selection property is not allowed");
            }

            identifierToIdMap.put(null, null);

            return;

        } else {
            throw new Exception("The method property '" + getMethod() + "' is invalid");
        }

        setIdentifierToIdMap(identifierToIdMap);
    }

    private void validateIdentifierToIdMap() throws IOException {
        // •	We want a list of all projects
        // •	Get this via ‘perl defects4j pids” -> a list of projects, one each line

        // Figure out how to call perl defects4j and get the list id's back
        System.out.println("RUNNING PROCESS BUILDER");
        // Need to change working directory
        ProcessBuilder ps = new ProcessBuilder("perl", "defects4j");
        ps.directory(new File("C:\\Users\\Fenton\\Documents\\Masters_Year\\Dissertation\\defects4j\\framework\\bin"));

        ps.start();
        System.out.println("STOPPING PROCESS BUILDER");

        // We start the process in the defects4j folder

        //List<String> results = readOutput(ps.getInputStream());



    }
}