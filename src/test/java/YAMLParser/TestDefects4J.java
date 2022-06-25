package YAMLParser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import Util.ShellProcessBuilder;
import java.security.InvalidParameterException;
import java.util.*;

public class TestDefects4J {

    LinkedHashMap<String, Object> testCaseSelectionHashSet;
    LinkedHashMap<String, Object> selectionHashSet;
    LinkedHashMap<String, Object> defects4jHashMap;

    public void resetTemplateHashMap() {
        // Create template hashmap
        testCaseSelectionHashSet = new LinkedHashMap<>();
        selectionHashSet = new LinkedHashMap<>();
        defects4jHashMap = new LinkedHashMap<>();
        defects4jHashMap.put("testCaseSelection", testCaseSelectionHashSet);
    }

    @Test
    @DisplayName("Test Case Selection")
    public void testTestCaseSelection() {
        resetTemplateHashMap();
        defects4jHashMap.remove("testCaseSelection"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Defects4J(defects4jHashMap));
    }

    @Test
    @DisplayName("Test Case Selection -> Method identifier")
    public void testMethodIdentifier() throws Exception {
        // Get all lang bugs
        ArrayList<Integer> langBids = new ArrayList<>(new Defects4J().getAllSingleIdentifier("Lang"));

        assertNotNull(langBids);
        assertTrue(langBids.size() >= 1);

        // Create a valid and invalid bug id for testing purposes
        int validBugId = langBids.get(0);
        int invalidBugId = langBids.get(langBids.size()-1) + 10;

        /* Invalid :: Selection value must be array type
            method: identifier
            selection:
              Lang: {valid}
         */
        resetTemplateHashMap();

        testCaseSelectionHashSet.put("method", "identifier");
        selectionHashSet.put("Lang", validBugId);
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        assertThrows(IllegalArgumentException.class, () -> new Defects4J(defects4jHashMap));

        /* Invalid :: Selection value is a depreciated Defects4J bug
            method: identifier
            selection:
              Lang: [{invalid}]
         */
        resetTemplateHashMap();
        ArrayList<Integer> identifiers = new ArrayList<>();
        identifiers.add(invalidBugId);

        testCaseSelectionHashSet.put("method", "identifier");
        selectionHashSet.put("Lang", identifiers);
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        assertThrows(NoSuchElementException.class, () -> new Defects4J(defects4jHashMap));

        /* Invalid :: Selection key is an invalid Defects4J project
            method: identifier
            selection:
              LangIncorrect: [{valid}]
         */
        resetTemplateHashMap();
        identifiers = new ArrayList<>();
        identifiers.add(validBugId);

        testCaseSelectionHashSet.put("method", "identifier");
        selectionHashSet.put("LangIncorrect", identifiers);
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        assertThrows(NoSuchElementException.class, () -> new Defects4J(defects4jHashMap));

        /* Invalid :: Missing method property
            selection:
              Lang: [{valid}]
         */
        resetTemplateHashMap();
        identifiers = new ArrayList<>();
        identifiers.add(validBugId);

        selectionHashSet.put("Lang", identifiers);
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        assertThrows(InvalidParameterException.class, () -> new Defects4J(defects4jHashMap));

        /* Invalid :: Missing selection property
            method: identifier
         */
        resetTemplateHashMap();
        testCaseSelectionHashSet.put("method", "identifier");
        assertThrows(InvalidParameterException.class, () -> new Defects4J(defects4jHashMap));

         /* Invalid :: Incorrect identifier String value
            method: identifier
            selection:
                Lang: allBugs
         */
        resetTemplateHashMap();

        selectionHashSet.put("Lang", "allBugs");
        testCaseSelectionHashSet.put("method", "identifier");
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        assertThrows(Exception.class, () -> new Defects4J(defects4jHashMap));

        /* Valid :: Defects4J project and bug exists
            method: identifier
            selection:
              Lang: [{valid}]
         */
        resetTemplateHashMap();
        identifiers = new ArrayList<>();
        identifiers.add(validBugId);

        selectionHashSet.put("Lang", identifiers);
        testCaseSelectionHashSet.put("method", "identifier");
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        HashSet<Integer> bids = new HashSet<>();
        bids.add(validBugId);
        assertEquals(new Defects4J(defects4jHashMap).selectedTestCases.get("Lang"), bids);

        /* Valid :: Defects4J project and bug exists
            method: identifier
            selection:
              Lang: [{valid-valid}]
         */
        resetTemplateHashMap();
        identifiers = new ArrayList<>();
        identifiers.add(langBids.get(0));
        identifiers.add(langBids.get(1));
        identifiers.add(langBids.get(2));

        selectionHashSet.put("Lang", identifiers);
        testCaseSelectionHashSet.put("method", "identifier");
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        bids = new HashSet<>();
        bids.add(langBids.get(0));
        bids.add(langBids.get(1));
        bids.add(langBids.get(2));
        assertEquals(new Defects4J(defects4jHashMap).selectedTestCases.get("Lang"), bids);

        /* Valid :: Defects4J project and bugs exist
            method: identifier
            selection:
              Lang: all
         */
        resetTemplateHashMap();

        selectionHashSet.put("Lang", "all");
        testCaseSelectionHashSet.put("method", "identifier");
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        int langBidSize = langBids.size();
        langBids.retainAll(new Defects4J(defects4jHashMap).selectedTestCases.get("Lang"));

        assertEquals(langBids.size(), langBidSize);
    }

    @Test
    @DisplayName("Test Case Selection -> Method All")
    public void testMethodAll() throws Exception {
        /* Invalid :: Selection is an invalid property
            method: all
            selection:
              Lang: 1
         */
        resetTemplateHashMap();
        testCaseSelectionHashSet.put("method", "all");
        testCaseSelectionHashSet.put("selection", "");
        assertThrows(InvalidParameterException.class, () -> new Defects4J(defects4jHashMap));

        /* Valid :: Ensure all bug ids are selected
            method: all
         */
        int confirmedBugCount = 0;

        ArrayList<String> identifiers = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "pids"});
        assertNotNull(identifiers);
        assertTrue(identifiers.size() >= 1);

        for (String identifier : identifiers) {
            confirmedBugCount += ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "bids", "-p", identifier}).size();
        }

        // Get all Defects4J bugs
        resetTemplateHashMap();
        testCaseSelectionHashSet.put("method", "all");

        int testBugCount = 0;
        for (HashSet<Integer> identifier : new Defects4J(defects4jHashMap).selectedTestCases.values()) { testBugCount += identifier.size(); }

        assertEquals(confirmedBugCount, testBugCount);
    }

    @Test
    @DisplayName("Test Case Selection -> Method incorrect name")
    public void testMethodIncorrectName() {
        resetTemplateHashMap();
        testCaseSelectionHashSet.put("method", "incorrectName");
        assertThrows(Exception.class, () -> new Defects4J(defects4jHashMap));
    }

    @Test
    @DisplayName("Get Range Single Identifier")
    public void testGetRangeSingleIdentifier() throws Exception {
        ArrayList<Integer> langBids = new ArrayList<>(new Defects4J().getAllSingleIdentifier("Lang"));

        /* Invalid :: Range is in incorrect form
                method: identifier
                selection:
                    Lang: [1;3]
         */
        ArrayList<Object> range = new ArrayList<>();
        range.add("1;3");
        ArrayList<Object> finalRange = range;
        assertThrows(Exception.class, () -> new Defects4J().getRangeSingleIdentifier("Lang", finalRange));

        /* Invalid :: Range contains wrong type
                method: identifier
                selection:
                    Lang: [1-a]
        */
        range = new ArrayList<>();
        range.add("1-a");
        ArrayList<Object> finalRange1 = range;
        assertThrows(Exception.class, () -> new Defects4J().getRangeSingleIdentifier("Lang", finalRange1));

        /* Valid :: Range
                method: identifier
                selection:
                    Lang: [1]
         */
        range = new ArrayList<>();
        range.add(langBids.get(0));
        ArrayList<Object> finalRange2 = range;
        assertTrue(new Defects4J().getRangeSingleIdentifier("Lang", finalRange2).contains(langBids.get(0)));

        /* Invalid :: Range with one in-between being an invalid id
                method: identifier
                selection:
                    Lang: [1-3]
         */
        resetTemplateHashMap();

        testCaseSelectionHashSet.put("method", "identifier");
        ArrayList<String> identifiers = new ArrayList<>();
        identifiers.add("1-3");
        selectionHashSet.put("Lang", identifiers);
        testCaseSelectionHashSet.put("selection", selectionHashSet);

        assertThrows(Exception.class, () -> new Defects4J(defects4jHashMap));
    }
}