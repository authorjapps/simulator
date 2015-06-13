package org.jsmart.simulator.domain;

/**
 * Created by Siddha on 27/04/2015.
 */
public class RestResponse {
    private String headers;
    private int status;
    private String body;

    public RestResponse(String headers, int status, String body) {
        this.headers = headers;
        this.status = status;
        this.body = body;
    }

    public String getHeaders() {
        return headers;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
