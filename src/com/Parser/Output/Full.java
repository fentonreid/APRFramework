package com.Parser.Output;

public class Full {
    private String jar;

    public String getJar() { return jar; }
    public void setJar(String jar) { this.jar = jar; }

    public void setup() throws Exception {
        if(getJar() == null) { throw new Exception("'jar' property is missing"); }
    }
}