package org.jsmart.simulator.domain;

/**
 * Created by Siddha on 25/04/2015.
 */
public class Api {
    private String name;
    private String operation;
    private String url;
    private RestResponse response;

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
