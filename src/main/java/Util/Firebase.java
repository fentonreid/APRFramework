package Util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Firebase {
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
