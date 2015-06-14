package org.jsmart.simulator.domain;

/**
 * A single REST api representation.
 *
 * @author Siddha.
 */
public class Api {
    private String name;
    private String operation;
    private String url;
    private RestResponse response;

    /**
     * @param name the short description about the REST api
     * @param operation one of the http methods e.g. GET, PUT, POST, DELETE, HEAD
     * @param url the REST end point being simulated
     * @param response the REST response expected from the end point
     */
    public Api(String name, String operation, String url, RestResponse response) {
        this.name = name;
        this.operation = operation;
        this.url = url;
        this.response = response;
    }

    public Api() {

    }

    public String getName() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getUrl() {
        return url;
    }

    public RestResponse getResponse() {
        return response;
    }

}
