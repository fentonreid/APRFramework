package Parser.Output;

public class Summary {
    private String csv;

    public String getCsv() { return csv; }
    public void setCsv(String csv) { this.csv = csv; }

    public void setup() throws Exception {
        if(getCsv() == null) { throw new Exception("csv property is missing"); }
    }
}