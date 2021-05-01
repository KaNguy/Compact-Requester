package core;

// Data streaming
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

// Networking and HTTP/HTTPS
import java.net.HttpURLConnection;
import java.net.URL;

// Utilities
import java.util.zip.GZIPInputStream;

// Constants
import core.constants.Constants;

public class Request {
    private String method;
    private String url;

    public Request(String url) {
        this.url = url;
        this.method = Constants.GET;

        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod(this.method);

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request(String url, String method) {

    }

    public static String read(HttpURLConnection connection) {
        InputStream connectionInputStream = null;
        try {
            connectionInputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Reader reader = null;
        if (connection.getContentEncoding() != null) {
            try {
                assert connectionInputStream != null;
                reader = new InputStreamReader(new GZIPInputStream(connectionInputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                reader = new InputStreamReader(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Empty char value
        int ch = 0;

        // String Builder to add to the final string
        StringBuilder stringBuilder = new StringBuilder();

        // Appending the data to a String Builder
        while (true) {
            try {
                assert reader != null;
                ch = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ch == -1) {
                return stringBuilder.toString();
            }

            stringBuilder.append((char) ch);
        }
    }
}
