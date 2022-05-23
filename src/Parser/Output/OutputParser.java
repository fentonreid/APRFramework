package Parser.Output;

public class OutputParser {
    private Summary summary;
    private Full full;

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }

    public Full getFull() { return full; }
    public void setFull(Full full) { this.full = full; }

    public void setup() throws Exception {
        if( getSummary() == null) { throw new Exception("Summary property is missing"); }
        if( getFull() == null) { throw new Exception("Full property is missing"); }
    }
}
