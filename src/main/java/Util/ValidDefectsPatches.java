package Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import YAMLParser.Defects4J;
import org.apache.commons.io.FileUtils;

public final class ValidDefectsPatches {
    public ValidDefectsPatches() {}

    // This function, checkouts each Defects4j bug and ensures that it has a valid properties file, and is uploaded to Firebase for use in the Vue website
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
