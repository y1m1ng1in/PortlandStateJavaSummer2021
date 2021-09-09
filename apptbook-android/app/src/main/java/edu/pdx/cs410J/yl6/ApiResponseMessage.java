package edu.pdx.cs410J.yl6;

public class ApiResponseMessage {

    private final String message;
    private final int status;

    public ApiResponseMessage(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
