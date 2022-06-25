package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public final class ShellProcessBuilder {
    public static Process runCommand(String[] command) throws Exception {
        try {
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.directory(new File("../defects4j/framework/bin/"));

            return ps.start();

        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            throw new Exception("The command passed is empty or contains null, consult the defects4j github examples for valid command user or man pages is using a bash command");
        } catch (IOException ex) {
            throw new Exception("An input/output error has occurred " + ex);
        }
    }

    public static Process runCommand(String[] command, File workingDirectory) throws Exception {
        try {
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.directory(workingDirectory);

            return ps.start();

        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            throw new Exception("The command passed is empty or contains null, consult the defects4j github examples for valid command user or man pages is using a bash command");
        } catch (IOException ex) {
            throw new Exception("An input/output error has occurred " + ex);
        }
    }

    public static ArrayList<String> getStandardInput(String[] command) throws Exception {
        try {
            Process runningProcess = ShellProcessBuilder.runCommand(command);
            runningProcess.waitFor();

            BufferedReader standardInput = new BufferedReader(new InputStreamReader(runningProcess.getInputStream()));
            ArrayList<String> result = new ArrayList<>();

            for (String line = standardInput.readLine(); line != null; line = standardInput.readLine()) {
                if (!line.equals("")) {
                    result.add(line);
                }
            }

            if (result.size() > 0) { return result; }
            throw new Exception();

        } catch (Exception ex) {
            throw new Exception("Command '" + Arrays.toString(command) + "' failed no standard input was recorded");
        }
    }
}