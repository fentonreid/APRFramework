package Parser;

import Parser.Defects4J.Defects4JParser;
import Parser.GP.GpParser;
import Parser.Output.OutputParser;

import java.util.ArrayList;

public class Parser {
    private GpParser gp;
    private Defects4JParser defects4J;
    private OutputParser output;

    public Parser() {}

    public GpParser getGp() { return gp; }
    public void setGp(GpParser gp) { this.gp = gp; }

    public Defects4JParser getDefects4J() { return defects4J; }
    public void setDefects4J(Defects4JParser defects4J) { this.defects4J = defects4J; }

    public OutputParser getOutput() { return output; }
    public void setOutput(OutputParser output) { this.output = output; }

    public boolean propertiesSet() throws Exception {
        if(getGp() == null) { throw new Exception("gp property is missing"); }
        if(getDefects4J() == null) { throw new Exception("defects4J property is missing"); }
        if(getOutput() == null) { throw new Exception("output property is missing"); }

        return true;
    }
}
