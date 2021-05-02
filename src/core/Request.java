package core;

// Data streaming
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.IOException;

// Networking and HTTP/HTTPS
import java.net.HttpURLConnection;
import java.net.URL;

// Charsets
import java.nio.charset.StandardCharsets;

// Utilities
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

// Constants
import core.constants.Constants;

public class Request {
    public static void main(String[] args) {
        Request request1 = new Request("https://docs.scala-lang.org/", "GET", null);
        //System.out.println(request1.output);

        Request request2 = new Request("https://reqres.in/api/users", "POST", "{\"name\": \"Li Xi\", \"job\": \"Java POST\"}");
        //System.out.println(request2.output);

        Request request3 = new Request(
                "https://api.github.com/",
                "GET",
                null,
                new String[][] {
                    {"Accept", "*/*"},
                    {"User-Agent", "*"}
                });
        System.out.println(request3.output);
    }
    public String method;
    public String url;
    public String output;
    public HttpURLConnection connection;

    public Request(String url) {
        this.url = url;
        this.method = Constants.GET;

        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            this.connection = connection;

            connection.setRequestMethod(this.method);

            connection.setConnectTimeout(Constants.STANDARD_TIMEOUT);
            connection.setReadTimeout(Constants.STANDARD_TIMEOUT);

            this.output = read(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request(String url, String method, String data, String[][] headers) {
        this.url = url;
        this.method = method.toUpperCase();

        if (data == null) data = "{}";

        HttpURLConnection connection;

        if (this.method.equals(Constants.POST) || this.method.equals(Constants.DELETE) || this.method.equals(Constants.PUT) || this.method.equals(Constants.PATCH)) {
            if (headers == null) {
                Request writable = new Request(url, method, data);
                this.output = writable.output;
            } else {
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    this.connection = connection;

                    connection.setRequestMethod(this.method);

                    connection.setConnectTimeout(Constants.STANDARD_TIMEOUT);
                    connection.setReadTimeout(Constants.STANDARD_TIMEOUT);

                    connection.setDoOutput(true);

                    // Add the headers
                    this.setHeaders(connection, headers);

                    try {
                        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
                        int length = bytes.length;
                        connection.setFixedLengthStreamingMode(length);

                        OutputStream outputStream = connection.getOutputStream();
                        outputStream.write(bytes, 0, length);
                        outputStream.flush();
                        outputStream.close();
                    } finally {
                        this.output = read(connection);
                        connection.getInputStream().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (headers == null) {
                Request readOnly = new Request(url);
                this.output = readOnly.output;
            } else {
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    this.connection = connection;

                    connection.setRequestMethod(this.method);

                    connection.setConnectTimeout(Constants.STANDARD_TIMEOUT);
                    connection.setReadTimeout(Constants.STANDARD_TIMEOUT);

                    // Adds headers
                    this.setHeaders(connection, headers);

                    this.output = read(connection);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
     }

    public Request(String url, String method, String data) {
        this.url = url;
        this.method = method.toUpperCase();

        if (data == null) data = "{}";

        HttpURLConnection connection;

        if (this.method.equals(Constants.POST) || this.method.equals(Constants.DELETE) || this.method.equals(Constants.PUT) || this.method.equals(Constants.PATCH)) {
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                this.connection = connection;

                connection.setRequestMethod(this.method);

                connection.setConnectTimeout(Constants.STANDARD_TIMEOUT);
                connection.setReadTimeout(Constants.STANDARD_TIMEOUT);

                connection.setDoOutput(true);

                try {
                    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
                    int length = bytes.length;
                    connection.setFixedLengthStreamingMode(length);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(bytes, 0, length);
                    outputStream.flush();
                    outputStream.close();
                } finally {
                    this.output = read(connection);
                    connection.getInputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Request readOnly = new Request(url);
            this.output = readOnly.output;
        }
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
        int ch;

        // String Builder to add to the final string
        StringBuilder stringBuilder = new StringBuilder();

        // Appending the data to a String Builder
        while (true) {
            try {
                assert reader != null;
                ch = reader.read();
                if (ch == -1) {
                    return stringBuilder.toString();
                }

                stringBuilder.append((char) ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHeaders(HttpURLConnection connection, String[][] headers) {
        final Map<String, String> mapHeaders = new HashMap<>(headers.length);
        for (String[] map : headers) {
            mapHeaders.put(map[0], map[1]);
        }

        mapHeaders.forEach(connection::setRequestProperty);
    }
}
