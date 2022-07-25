package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * The ShellProcessBuilder class creates process builders that can interact with the systems command line to interact with the Defects4j cli.
 */
public final class ShellProcessBuilder {

    /**
     * Creates a process builder in the Defects4J binaries folder and starts the process executing a given command.
     *
     * @param command       A String array that specifies the command for the process builder to execute. Commonly a Defects 4j command such as; "perl defects4j ..."
     * @return              A process object is returned that can be manipulated further if needed
     * @throws Exception    If the process builder cannot be created
     */
    public static Process runCommand(String[] command) throws Exception {
        try {
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.directory(new File("../defects4j/framework/bin/"));
            Process runningProcess =  ps.start();

            BufferedReader errorInput = new BufferedReader(new InputStreamReader(runningProcess.getErrorStream()));
            while (errorInput.readLine() != null) {}

            return runningProcess;

        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            throw new Exception("The command passed is empty or contains null, consult the defects4j github examples for valid command user or man pages is using a bash command");
        } catch (IOException ex) {
            throw new Exception("An input/output error has occurred " + ex);
        } catch (Exception ex) {
            throw new Exception("Another exception has been raised");
        }
    }

    /**
     * Creates a process builder in a chosen directory and starts the process executing a given command.
     *
     * @param command               A String array that specifies the command for the process builder to execute. Commonly a Defects 4j command such as; "perl defects4j ..."
     * @param workingDirectory      The working directory for the process builder to be started in
     * @return                      A process object is returned that can be manipulated further if needed
     * @throws Exception            If the process builder cannot be created
     */
    public static Process runCommand(String[] command, File workingDirectory) throws Exception {
        try {
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.directory(workingDirectory);
            Process runningProcess =  ps.start();

            BufferedReader errorInput = new BufferedReader(new InputStreamReader(runningProcess.getErrorStream()));
            while (errorInput.readLine() != null) {}

            return runningProcess;

        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            throw new Exception("The command passed is empty or contains null, consult the defects4j github examples for valid command user or man pages is using a bash command");
        } catch (IOException ex) {
            throw new Exception("An input/output error has occurred " + ex);
        } catch (Exception ex) {
            throw new Exception("Another exception has been raised " + ex);
        }
    }

    /**
     * Get the standard input of the process and return an ArrayList of Strings.
     *
     * @param command           A String array that specifies the command for the process builder to execute
     * @return                  An ArrayList of strings for each line of Standard input recorded
     * @throws Exception        If no standard input was recorded
     */
    public static ArrayList<String> getStandardInput(String[] command) throws Exception {
        try {
            Process runningProcess = ShellProcessBuilder.runCommand(command);
            return getStrings(runningProcess);

        } catch (Exception ex) {
            throw new Exception("Command '" + Arrays.toString(command) + "' failed no standard input was recorded" + " " + ex);
        }
    }

    /**
     * The standard input of the process is returned in an ArrayList.
     *
     * @param runningProcess    A String array that specifies the command for the process builder to execute
     * @return                  An ArrayList of strings for each line of Standard input recorded
     * @throws Exception        If no standard input was recorded
     */
    private static ArrayList<String> getStrings(Process runningProcess) throws IOException, InterruptedException {
        BufferedReader standardInput = new BufferedReader(new InputStreamReader(runningProcess.getInputStream()));
        ArrayList<String> result = new ArrayList<>();

        String line;
        while ((line = standardInput.readLine()) != null)
            if (!line.equals("")) {
                result.add(line);
            }

        // WAIT for 60 seconds then fail
        runningProcess.waitFor(60, TimeUnit.SECONDS);

        if (result.size() < 1) { throw new IOException("No standard input was recorded"); }
        return result;
    }
}