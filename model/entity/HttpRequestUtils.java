package model.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpRequestUtils {

    public static String readRequestBody(BufferedReader reader) throws IOException {
        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;
        requestBodyBuilder = new StringBuilder();
        int contentLength = 0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
            } else if (line.isEmpty()) {
                // Reached the end of the headers, start reading the request body
                break;
            }
        }
        // Read the request body based on the content length
        for (int i = 0; i < contentLength; i++) {
            requestBodyBuilder.append((char) reader.read());
        }
        return requestBodyBuilder.toString();
    }

    public HashMap<String, String> parseParameters(String params) {
        HashMap<String, String> parameters = new HashMap<>();
        String[] keyValuePairs = params.split("&");
        for (String keyValuePair : keyValuePairs) {
            String[] parts = keyValuePair.split("=");
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    public static HashMap<String, String> parseEncodedParameters(String url) {
        HashMap<String, String> parameters = new HashMap<>();
        // Check if the URL contains a query string
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex >= 0 && questionMarkIndex < url.length() - 1) {
            String queryString = url.substring(questionMarkIndex + 1);
            // Split the query string into individual parameters
            String[] paramPairs = queryString.split("&");
            for (String paramPair : paramPairs) {
                // Split each parameter into name and value
                String[] parts = paramPair.split("=");
                if (parts.length == 2) {
                    String paramName = parts[0];
                    String paramValue = parts[1];
                    // URL decode the parameter value
                    try {
                        paramValue = URLDecoder.decode(paramValue, StandardCharsets.UTF_8.toString());
                    } catch (java.io.UnsupportedEncodingException e) {
                        // Handle the exception appropriately
                    }
                    parameters.put(paramName, paramValue);
                }
            }
        }
        return parameters;
    }

    public static HashMap<String, String> parseRequest(String requestLine) {
        HashMap<String, String> request = new HashMap<>();

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length >= 2) {
            String method = requestParts[0];
            String pathWithParams = requestParts[1];

            // Split path and parameters if available
            String[] pathParams = pathWithParams.split("\\?", 2);
            String path = pathParams[0];
            if (pathParams.length > 1) {
                String paramsString = pathParams[1];
                String[] paramPairs = paramsString.split("&");
                for (String paramPair : paramPairs) {
                    String[] paramParts = paramPair.split("=");
                    if (paramParts.length == 2) {
                        String paramName = decodeURLComponent(paramParts[0]);
                        String paramValue = decodeURLComponent(paramParts[1]);

                        request.put(paramName, paramValue);
                    }
                }
            }

            request.put("method", method);
            request.put("path", path);
        }

        return request;
    }

    public static String decodeURLComponent(String component) {
        try {
            return URLDecoder.decode(component, StandardCharsets.UTF_8.toString());
        } catch (java.io.UnsupportedEncodingException e) {
            // Handle the exception appropriately
        }
        return component;
    }
}
