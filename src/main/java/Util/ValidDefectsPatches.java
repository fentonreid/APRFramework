package Util;

import java.io.*;
import java.util.*;
import YAMLParser.Defects4J;
import org.apache.commons.io.FileUtils;

/**
 * The ValidDefectsPatches class uploads the valid Defects4j bugs to the Vue website.
 */
public final class ValidDefectsPatches {

    /**
     * Each Defects4j bug is checked out and validated to ensure that it has a valid properties file which is then uploaded to the Firebase database to be processed by the Vue website.
     *
     * @throws Exception    The valid bug could not be checked out or uploaded to the Firebase database
     */
    public static void main() throws Exception {
        HashMap<String, HashSet<Integer>> validBugs =  new Defects4J().validBugs;

        for (String identifier : validBugs.keySet()) {
            Set<Integer> bids = validBugs.get(identifier);
            for (int bid : bids) {
                // Checkout the bug
                String checkoutPath = "/tmp/" + identifier + "_" + bid + "/";
                ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", identifier, "-v", bid + "b", "-w", checkoutPath}).waitFor();

                // Ensure bug is valid
                try { ProjectPaths.getBuggyProgramPath(checkoutPath); }
                catch (Exception ex) { continue; }
                finally { FileUtils.deleteDirectory(new File(checkoutPath)); }

                // Prepare parameters for JSON payload to firebase
                String id = identifier + "_" + bid;
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("identifier", identifier);
                params.put("bid", bid);
                params.put("patch", FileUtils.readFileToString(ProjectPaths.getFixedProgramPath(identifier, bid).toFile(), "UTF-8").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
                params.put("classification", "Unassigned");

                // Upload patch to Firebase
                try { Firebase.UploadPatchToFirebase("allpatches.json", params); }
                catch (Exception ex) { System.out.println("Failed to upload to firebase patch: " + identifier + " " + bid); }
            }
        }
    }
}
