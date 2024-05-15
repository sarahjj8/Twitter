package model.entity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientResponse {
    private String response;
    private int responseCode;
    //host and port will be determined by model.entity.SetUp class
    private static String HOST;
    private static int PORT;

    public HttpClientResponse(String response, int responseCode) {
        this.response = response;
        this.responseCode = responseCode;
    }

    public static void setConfigure(int PORT, String HOST) {
        HttpClientResponse.PORT = PORT;
        HttpClientResponse.HOST = HOST;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public static HttpClientResponse sendGetRequest(String urlStr, Map<String, String> params) {
        try {
            // Construct the URL with parameters
            StringBuilder urlBuilder = new StringBuilder(urlStr);
            StringBuilder response = new StringBuilder();
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    urlBuilder.append(key).append("=").append(value).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove the trailing "&"
                replaceSpacesWithUrlEncoding(urlBuilder);
            }
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return new HttpClientResponse(response.toString(), responseCode);
            } else {
                StringBuilder errorResponse = new StringBuilder();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                return new HttpClientResponse(errorResponse.toString(), responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * this method must be used when using a url that contains spaces (this method will replace them with '%20')
     */
    private static void replaceSpacesWithUrlEncoding(StringBuilder urlBuilder) {
        for (int i = 0; i < urlBuilder.length(); i++) {
            if (urlBuilder.charAt(i) == ' ') {
                urlBuilder.replace(i, i + 1, "%20");
            }
        }
    }

    public static HttpClientResponse sendPatchRequest(String urlString, String payload) throws IOException {
        URL url = new URL(urlString);
        String serverHost = url.getHost();
        int serverPort = url.getPort();
        String endpoint = url.getPath();
        // Construct the HTTP request
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("PATCH ").append(endpoint).append(" HTTP/1.1\r\n");
        requestBuilder.append("Host: ").append(serverHost).append("\r\n");
        requestBuilder.append("Content-Type: application/json\r\n");
        requestBuilder.append("Content-Length: ").append(payload.length()).append("\r\n");
        requestBuilder.append("\r\n");
        requestBuilder.append(payload);
        Socket socket = new Socket(serverHost, serverPort);
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(requestBuilder.toString());
        writer.flush();
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        // Read the response status line
        String statusLine = reader.readLine();
        System.out.println("Response Status: " + statusLine);
        // Parse the response code from the status line
        int responseCode = Integer.parseInt(statusLine.split(" ")[1]);
        // Read the response headers
        String headerLine;
        while ((headerLine = reader.readLine()).length() > 0) {
            System.out.println(headerLine);
        }
        System.out.println();
        // Reading the response body
        StringBuilder response = new StringBuilder();
        while ((headerLine = reader.readLine()) != null) {
            response.append(headerLine).append("\n");
        }
        writer.close();
        reader.close();
        socket.close();

        return new HttpClientResponse(response.toString(), responseCode);
    }

    /**
     * This method can be used for GET, POST and DELETE methods(not PATCH)
     */
    public static HttpClientResponse sendRequest(String urlStr, String requestBody, Map<String, String> params, String httpMethod) {
        HttpURLConnection connection = null;
        try {
            StringBuilder urlBuilder = new StringBuilder(urlStr);
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    urlBuilder.append(key).append("=").append(value).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove the trailing "&"
                replaceSpacesWithUrlEncoding(urlBuilder);
            }
            URL url = new URL(urlBuilder.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod);
            if (httpMethod.equals("POST") || httpMethod.equals("PATCH")) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                if (requestBody != null) {
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBody.getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            }
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return new HttpClientResponse(response.toString(), responseCode);
            } else {
                StringBuilder errorResponse = new StringBuilder();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                return new HttpClientResponse(errorResponse.toString(), responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    public static String createURL(String path) {
        return "http://" + HOST + ":" + PORT + "/" + path;
    }
}
