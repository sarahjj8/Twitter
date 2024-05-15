package model.entity;

import java.io.PrintWriter;

public class HttpResponse {
    private StringBuilder responseBuilder = new StringBuilder();
    private boolean gotResponse;
    private PrintWriter writer;

    public HttpResponse(PrintWriter writer) {
        this.writer = writer;
        gotResponse = false;
    }

    public boolean getGotResponse() {
        return gotResponse;
    }

    public StringBuilder getResponseBuilder() {
        return responseBuilder;
    }

    public void setGotResponse(boolean gotResponse) {
        this.gotResponse = gotResponse;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void append(String response) {
        responseBuilder.append(response);
    }

    public void writeResponse(int code, String message){
        switch (code){
            case 200:
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/plain");
                writer.println(); // Empty line to indicate end of headers
                if (message != null)
                    writer.println(message);
                break;
            case 201:
                writer.println("HTTP/1.1 201 Created");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                break;
            case 204:
                writer.println("HTTP/1.1 204 No Content");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                else {
                    writer.println("Content-Length: 0");
                }
                break;
            case 400:
                writer.println("HTTP/1.1 400 Bad Request");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                else {
                    writer.println("Content-Length: 0");
                }
                break;
            case 401:
                writer.println("HTTP/1.1 401 Unauthorized");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                else {
                    writer.println("Content-Length: 0");
                }
                break;
            case 403:
                writer.println("HTTP/1.1 403 Forbidden");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                break;
            case 404:
                writer.println("HTTP/1.1 404 Not Found");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                break;
            case 405:
                writer.println("HTTP/1.1 405 Method Not Allowed");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                else {
                    writer.println("Content-Length: 0");
                }
                break;
            case 409:
                writer.println("HTTP/1.1 409 Conflict");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                else {
                    writer.println("Content-Length: 0");
                }
                break;
            case 500:
                writer.println("HTTP/1.1 500 Internal Server Error");
                writer.println("Content-Type: text/plain");
                writer.println();
                if (message != null)
                    writer.println(message);
                break;
        }
    }

    public void writeJsonResponse(int code, String message) {
        switch (code) {
            case 200:
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: application/json");
                writer.println(); // Empty line to indicate end of headers
                if (message != null)
                    writer.println(message);
                break;
        }
    }
}