import Parser.Parser;
import Parser.Runner;

public class Main {
    public static void main(String[] args) {
        // Parse config.yaml
        Parser configData = new Runner(args).main();
    }
}
