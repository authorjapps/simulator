package org.jsmart.simulator.domain;

/**
 * @author Siddha
 */
public class RestResponse {
    private String headers;
    private int status;
    private String body;

    /**
     * This object encapsulates the REST response.
     *
     * @param headers http headers and custom headers in json format
     * @param status http status code of the response
     * @param body the response entity in a json or string or xml format
     */
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
