package Defects4J;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public final class perlInterpreter {

    public static ArrayList<String> getStandardInput(String[] command) throws Exception {
        Process runningProcess;
        try {
            // Change the working directory to the defects4j perl file
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.directory(new File(System.getProperty("user.dir") + "\\defects4j\\framework\\bin"));

            // Start process and gather the defects4j output through the input stream
            runningProcess = ps.start();
            BufferedReader standardInput = new BufferedReader(new InputStreamReader(runningProcess.getInputStream()));

            // Read the standard input line by line and store in result
            ArrayList<String> result = new ArrayList<>();

            for (String line = standardInput.readLine(); line != null; line = standardInput.readLine()) {
                if (!line.equals("")) {
                    //System.out.println(line);
                    result.add(line);
                }
            }

            if (result.size() > 0) {
                return result;
            }

            throw new Exception();

        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            throw new Exception("The command passed is empty or contains null, consult the defects4j github examples for valid command uses");
        } catch (IOException ex) {
            throw new Exception("An input/output error has occurred " + ex);
        } catch (Exception ex) {
            throw new Exception("Command '" + command.toString() + "' failed no standard input was recorded");
        }
    }
}