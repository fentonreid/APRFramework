package com.Parser.Defects4J;

public class Defects4JParser {
    private TestCaseSelection testCaseSelection;
    public TestCaseSelection getTestCaseSelection() { return testCaseSelection; }
    public void setTestCaseSelection(TestCaseSelection testCaseSelection) { this.testCaseSelection = testCaseSelection; }

    public void setup() throws Exception {
        if(getTestCaseSelection() == null) { throw new Exception("testCaseSelection property is missing"); }
    }
}