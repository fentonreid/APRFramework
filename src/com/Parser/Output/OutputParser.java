package com.Parser.Output;

public class OutputParser {
    private Summary summary;
    private Full full;

    private String ConfigYAMLName;

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }

    public Full getFull() { return full; }
    public void setFull(Full full) { this.full = full; }

    public String getConfigYAMLName() { return ConfigYAMLName; }
    public void setConfigYAMLName(String configYAMLName) { ConfigYAMLName = configYAMLName; }

    public void setup() throws Exception {
        if( getSummary() == null) { throw new Exception("Summary property is missing"); }
        if( getFull() == null) { throw new Exception("Full property is missing"); }
    }
}
