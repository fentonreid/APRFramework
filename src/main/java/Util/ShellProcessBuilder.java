package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public static void runCommand(String[] command) throws Exception {
        try {
            List<String> updatedCommand = new ArrayList<>(Arrays.asList(command));
            updatedCommand.add(">");
            updatedCommand.add("/dev/null");
            updatedCommand.add("2>&1");

            ProcessBuilder ps = new ProcessBuilder(updatedCommand);
            ps.directory(new File("../defects4j/framework/bin/"));

            Process runningProcess = ps.start();
            runningProcess.waitFor();

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
    public static void runCommand(String[] command, File workingDirectory) throws Exception {
        try {
            List<String> updatedCommand = new ArrayList<>(Arrays.asList(command));
            updatedCommand.add(">");
            updatedCommand.add("/dev/null");
            updatedCommand.add("2>&1");

            ProcessBuilder ps = new ProcessBuilder(updatedCommand);
            ps.directory(workingDirectory);

            Process runningProcess = ps.start();
            runningProcess.waitFor();

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

        List<String> updatedCommand = new ArrayList<>(Arrays.asList(command));
        updatedCommand.add("2>");
        updatedCommand.add("/dev/null");

        ProcessBuilder ps = new ProcessBuilder(updatedCommand);
        ps.directory(new File("../defects4j/framework/bin/"));

        Process runningProcess = ps.start();
        ArrayList<String> output = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(runningProcess.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            output.add(line);
        }

        runningProcess.waitFor();
        return output;
    }

	/**
	* Get the standard input of the defects4j test command, return the number of failing test cases as a string if found
	* 
	* @param 		A String array that specifies the command for the process builder to execute
	* @return		The number of failing test cases of a given project, null if an error occurred
	*/
    public static String getFailingTestCases(String[] command) {
        try {
			List<String> updatedCommand = new ArrayList<>(Arrays.asList(command));
			updatedCommand.add("2>");
			updatedCommand.add("/dev/null");
			
            ProcessBuilder ps = new ProcessBuilder(updatedCommand);
			ps.directory(new File("../defects4j/framework/bin/"));
			
            Process runningProcess = ps.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(runningProcess.getInputStream()));

            String failingTests = "";
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Failing tests: ")) {
                    failingTests = line;
                }
            }

            runningProcess.waitFor();

            if (failingTests.contains("Failing tests: "))
                return failingTests.replaceAll("Failing tests: ", "");

            return null;

        } catch (Exception ex) { return null; }
    }
}