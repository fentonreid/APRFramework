package Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * The Firebase class provides helper functions for post methods to be constructed and sent to a valid Firebase endpoint.
 */
public final class Firebase {
    
    /**
     * Establish a connection to the Firebase database and send a post request with a JSON payload to a given Firebase endpoint.
     *
     * @param endpoint      The firebase endpoint that a JSON payload will be sent too
     * @param params        The JSON payload that will be sent to the Firebase database
     * @throws Exception    Failure to send the JSON payload or establish a connection to the database
     */
    public static void UploadPatchToFirebase (String endpoint, Map<String, Object> params) throws Exception {
        // Send JSON patch data to firebase for use with the APRFramework Vue website
        HttpURLConnection http = (HttpURLConnection) new URL("https://aprframeworkvue-default-rtdb.europe-west1.firebasedatabase.app/" + endpoint).openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] payload = (new ObjectMapper().writeValueAsString(params)).getBytes();

        // Set content-type as JSON and make request to Firebase
        http.setFixedLengthStreamingMode(payload.length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();

        try(OutputStream os = http.getOutputStream()) { os.write(payload); }
    }
}