package Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import YAMLParser.Defects4J;
import org.apache.commons.io.FileUtils;

public final class ValidDefectsPatches {
    public ValidDefectsPatches() {}

    // This function, checkouts each defects4j bug and assure that it has a valid properties file, next the src patch is copied to the /patches folder for use with the website
    public static void main() throws Exception {
        for (String identifier : ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "pids"})) {
            HashSet<Integer> bids = new Defects4J().getAllSingleIdentifier(identifier);
            for (int bid : bids) {
                // Checkout the bug
                String checkoutPath = "/tmp/" + identifier + "_" + bid + "/";
                Process finishedProcess = ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", identifier, "-v", bid + "b", "-w", checkoutPath});
                if (new InputStreamReader(finishedProcess.getInputStream()).read() != -1) { throw new Exception("Error when trying to checkout '" + identifier + "' with a bug id of '" + bid); }

                // Ensure bug is valid
                try { ProjectPaths.getBuggyProgramPath(checkoutPath); }
                catch(Exception ex) { continue; }
                finally { FileUtils.deleteDirectory(new File(checkoutPath)); }

                // Send JSON patch data to firebase for use with the APRFramework Vue website
                HttpURLConnection http = (HttpURLConnection) new URL("https://aprframeworkvue-default-rtdb.europe-west1.firebasedatabase.app/allpatches.json").openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);

                String id = identifier + "_" + bid;
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("identifier", identifier);
                params.put("bid", bid);
                params.put("patch", FileUtils.readFileToString(ProjectPaths.getFixedProgramPath(identifier, bid).toFile(), "UTF-8").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
                params.put("classification", "Unassigned");
                byte[] payload = (new ObjectMapper().writeValueAsString(params)).getBytes();

                http.setFixedLengthStreamingMode(payload.length);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                http.connect();
                try(OutputStream os = http.getOutputStream()) {
                    os.write(payload);
                }
            }
        }
    }
}
