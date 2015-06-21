package org.jsmart.simulator.domain;

/**
 * A single REST api representation.
 *
 * @author Siddha.
 */
public class RestApi extends Api{
    private String name;
    private String operation;
    private String url;
    private RestResponse response;

    public RestApi(String name, String operation, String url, RestResponse response) {
        super(name, operation, url, response);
    }

    public RestApi() {
    }

    public RestApi build(){
        return new RestApi(name, operation, url, response);
    }

    public RestApi name(String name){
        this.name = name;
        return this;
    }

    public RestApi operation(String method){
        this.operation = method;
        return this;
    }

    public RestApi url(String url){
        this.url = url;
        return this;
    }

    public RestApi response(RestResponse restResponse){
        this.response = restResponse;
        return this;
    }

    //TODO: Add other setters

}
